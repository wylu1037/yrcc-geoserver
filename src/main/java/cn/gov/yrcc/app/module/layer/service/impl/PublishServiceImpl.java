package cn.gov.yrcc.app.module.layer.service.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.coverage.response.CoverageResponse;
import cn.gov.yrcc.app.module.coverage.service.CoverageService;
import cn.gov.yrcc.app.module.datastore.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.layer.repository.LayerRepository;
import cn.gov.yrcc.app.module.layer.request.PublishShpRequest;
import cn.gov.yrcc.app.module.layer.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layer.service.PublishService;
import cn.gov.yrcc.app.module.layer.service.ShpService;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.app.module.workspace.repository.WorkspaceRepository;
import cn.gov.yrcc.internal.constant.LayerTypeEnum;
import cn.gov.yrcc.internal.constant.PublishLayerStatusEnum;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import cn.gov.yrcc.internal.geoserver.GeoServerBuilder;
import cn.gov.yrcc.internal.properties.PostGisProperties;
import cn.gov.yrcc.utils.file.FileCalculator;
import cn.gov.yrcc.utils.file.FileDirectoryUtils;
import cn.gov.yrcc.utils.file.ZipUtils;
import com.google.common.base.Throwables;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.jdbc.JDBCDataStore;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@Transactional
public class PublishServiceImpl implements PublishService {

	private final GeoServerRESTPublisher geoServerRESTPublisher;
	private final WorkspaceRepository workspaceRepository;
	private final Executor fileThreadPool;
	private final DatastoreRepository datastoreRepository;
	private final MessageNotificationRepository messageNotificationRepository;
	private final LayerRepository layerRepository;
	private final CoverageService coverageService;
	private final ShpService shpService;
	private final DataStore dataStore;
	private final PostGisProperties properties;
	private final GeoServerRESTManager geoServerRESTManager;
	private final GeoServerBuilder geoServerBuilder;

	public PublishServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher, WorkspaceRepository workspaceRepository, Executor fileThreadPool, DatastoreRepository datastoreRepository, MessageNotificationRepository messageNotificationRepository, LayerRepository layerRepository, CoverageService coverageService, ShpService shpService, DataStore dataStore, PostGisProperties properties, GeoServerRESTManager geoServerRESTManager, GeoServerBuilder geoServerBuilder) {
		this.geoServerRESTPublisher = geoServerRESTPublisher;
		this.workspaceRepository = workspaceRepository;
		this.fileThreadPool = fileThreadPool;
		this.datastoreRepository = datastoreRepository;
		this.messageNotificationRepository = messageNotificationRepository;
		this.layerRepository = layerRepository;
		this.coverageService = coverageService;
		this.shpService = shpService;
		this.dataStore = dataStore;
		this.properties = properties;
		this.geoServerRESTManager = geoServerRESTManager;
		this.geoServerBuilder = geoServerBuilder;
	}

	@Override
	public void publishTif(PublishTifRequest request) {
		Workspace workspace = workspaceRepository.findByName(request.getWorkspace());
		if (workspace == null) {
			throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
		}
		boolean exists = layerRepository.exists(request.getWorkspace(), request.getLayerName());
		if (exists) {
			throw new BusinessException(String.format("%s在工作空间下%s已存在", request.getWorkspace(), request.getLayerName()));
		}

		// 发布前存储
		File file = request.toTifFile();
		final Layer layer = Layer.builder()
			.name(request.getLayerName())
			.title(request.getLayerName())
			.workspace(request.getWorkspace())
			.datastore(request.getStoreName())
			.size(FileCalculator.calculateSize(file))
			.type(LayerTypeEnum.RASTER.name())
			.status(PublishLayerStatusEnum.uploading.name())
			.enable(true)
			.createdAt(new Date())
			.deleted(false)
			.build();
		layerRepository.saveAndFlush(layer);
		log.info("[PublishServiceImpl] publishTif() save layer {}, return id {}", request.getLayerName(), layer.getId());

		// 提交异步上传任务
		CompletableFuture.runAsync(() -> {
			try {
				boolean success;
				success = geoServerRESTPublisher.publishGeoTIFF(request.getWorkspace(), request.getStoreName(), file);
				log.info("publish tif file {}", success ? "success" : "failure");
				if (!success) {
					throw new BusinessException("文件存储发布失败");
				} else {
					// 存储仓库
					onSuccess(layer, request);
				}
			} catch (FileNotFoundException e) {
				log.error("[PublishServiceImpl] publishTif() called with Params: request = {}, Error message = {}",
					request, Throwables.getStackTraceAsString(e));
				throw new BusinessException("文件存储发布异常");
			}
		}, fileThreadPool).exceptionally(ex -> onFailure(layer));
	}

	private void onSuccess(Layer layer, PublishTifRequest request) {
		// 存储仓库
		datastoreRepository.save(Datastore.builder()
			.name(request.getStoreName())
			.type("GeoTiff")
			.enabled(true)
			.workspace(request.getWorkspace())
			.deleted(false)
			.createdAt(new Date())
			.build());

		// 更新图层
		CoverageResponse coverage = coverageService.details(request.getWorkspace(), request.getLayerName());
		layer.setStatus(PublishLayerStatusEnum.success.name());
		layer.setCrs(coverage.getCoverage().getLatLonBoundingBox().getCrs());
		layer.setMinx(coverage.getCoverage().getLatLonBoundingBox().getMinx());
		layer.setMaxx(coverage.getCoverage().getLatLonBoundingBox().getMaxx());
		layer.setMiny(coverage.getCoverage().getLatLonBoundingBox().getMiny());
		layer.setMaxy(coverage.getCoverage().getLatLonBoundingBox().getMaxy());
		layerRepository.save(layer);

		// 发送成功的消息通知
		messageNotificationRepository.save(MessageNotification.builder()
			.category("NOTIFICATION")
			.message(String.format("上传发布Tif文件【%s】成功", layer.getName()))
			.receiverId(1L)
			.read(false)
			.createdAt(new Date())
			.deleted(false)
			.build());
	}

	private Void onFailure(Layer layer) {
		// 更新状态
		layer.setStatus(PublishLayerStatusEnum.failure.name());
		layerRepository.save(layer);

		// 发送失败的消息通知
		messageNotificationRepository.save(MessageNotification.builder()
			.category("WARN")
			.message(String.format("上传发布Tif文件【%s】失败", layer.getName()))
			.receiverId(1L)
			.read(false)
			.createdAt(new Date())
			.deleted(false)
			.build());
		return null;
	}

	@Override
	public void publishShp(PublishShpRequest request) {
		String path = null;
		try {
			path = ZipUtils.unzip(request.toFile(), request.getLayerName());
			FileDirectoryUtils.checkShpDirectory(path);

			SimpleFeatureSource simpleFeatureSource = shpService.readFile(new File(path + File.separator + request.getLayerName() + ".shp"));
			JDBCDataStore ds = shpService.createTable((JDBCDataStore) dataStore, simpleFeatureSource, null);
			shpService.write2db(ds, simpleFeatureSource);

			String tableName = request.getLayerName();
			String storeName = request.getStoreName();
			GSPostGISDatastoreEncoder store = geoServerBuilder.buildGSPostGISDatastoreEncoder(storeName);
			boolean createStore = geoServerRESTManager.getStoreManager().create(request.getWorkspace(), store);
			System.out.println("create store " + createStore);

			GSFeatureTypeEncoder pds = new GSFeatureTypeEncoder();
			pds.setTitle(tableName);
			pds.setName(tableName);
			pds.setSRS("EPSG:4326");
			GSLayerEncoder layerEncoder = new GSLayerEncoder();
			boolean publish = geoServerRESTManager.getPublisher().publishDBLayer(request.getWorkspace(), storeName, pds,
				layerEncoder);

			System.out.println("publish " + publish);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			FileDirectoryUtils.delete(path);
		}
	}
}