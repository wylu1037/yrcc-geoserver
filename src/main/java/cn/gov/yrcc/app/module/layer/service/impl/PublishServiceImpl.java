package cn.gov.yrcc.app.module.layer.service.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.coverage.response.CoverageResponse;
import cn.gov.yrcc.app.module.coverage.service.CoverageService;
import cn.gov.yrcc.app.module.datastore.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.featuretype.response.FeatureTypeResponse;
import cn.gov.yrcc.app.module.featuretype.service.FeatureTypeService;
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
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.jdbc.JDBCDataStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

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
	private final GeoServerRESTManager geoServerRESTManager;
	private final GeoServerBuilder geoServerBuilder;
	private final FeatureTypeService featureTypeService;
	private final PostGisProperties properties;

	public PublishServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher, WorkspaceRepository workspaceRepository, Executor fileThreadPool, DatastoreRepository datastoreRepository, MessageNotificationRepository messageNotificationRepository, LayerRepository layerRepository, CoverageService coverageService, ShpService shpService, GeoServerRESTManager geoServerRESTManager, GeoServerBuilder geoServerBuilder, FeatureTypeService featureTypeService, PostGisProperties properties) {
		this.geoServerRESTPublisher = geoServerRESTPublisher;
		this.workspaceRepository = workspaceRepository;
		this.fileThreadPool = fileThreadPool;
		this.datastoreRepository = datastoreRepository;
		this.messageNotificationRepository = messageNotificationRepository;
		this.layerRepository = layerRepository;
		this.coverageService = coverageService;
		this.shpService = shpService;
		this.geoServerRESTManager = geoServerRESTManager;
		this.geoServerBuilder = geoServerBuilder;
		this.featureTypeService = featureTypeService;
		this.properties = properties;
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
			StopWatch watch = new StopWatch();
			watch.start();
			try {
				boolean success;
				success = geoServerRESTPublisher.publishGeoTIFF(request.getWorkspace(), request.getStoreName(), file);
				log.info("publish tif file {}", success ? "success" : "failure");
				if (!success) {
					throw new BusinessException("文件存储发布失败");
				} else {
					// 存储仓库
					watch.stop();
					onSuccessPublishTif(layer, request, watch);
				}
			} catch (FileNotFoundException e) {
				log.error("[PublishServiceImpl] publishTif() called with Params: request = {}, Error message = {}",
					request, Throwables.getStackTraceAsString(e));
				throw new BusinessException("文件存储发布异常");
			}
		}, fileThreadPool).exceptionally(ex -> onFailurePublishTif(layer));
	}

	private void onSuccessPublishTif(Layer layer, PublishTifRequest request, StopWatch watch) {
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
		layer.setCost(watch.getTotalTimeMillis());
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

	private Void onFailurePublishTif(Layer layer) {
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
		Workspace workspace = workspaceRepository.findByName(request.getWorkspace());
		if (workspace == null) {
			throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
		}
		Datastore datastore = datastoreRepository.findByWorkspaceAndName(request.getWorkspace(), request.getStoreName());
		if (datastore != null) {
			throw new BusinessException(String.format("存储仓库%s名称已存在", request.getStoreName()));
		}
		boolean exists = layerRepository.exists(request.getWorkspace(), request.getLayerName());
		if (exists) {
			throw new BusinessException(String.format("%s在工作空间下%s已存在", request.getWorkspace(), request.getLayerName()));
		}

		File file = request.toShpFile();
		final Layer layer = Layer.builder()
			.name(request.getLayerName())
			.title(request.getLayerName())
			.workspace(request.getWorkspace())
			.datastore(request.getStoreName())
			.size(FileCalculator.calculateSize(file))
			.type(LayerTypeEnum.VECTOR.name())
			.status(PublishLayerStatusEnum.uploading.name())
			.enable(true)
			.createdAt(new Date())
			.deleted(false)
			.build();
		layerRepository.saveAndFlush(layer);
		log.info("[PublishServiceImpl] publishShp() save layer {}, return id {}", request.getLayerName(), layer.getId());

		// 提交异步任务
		CompletableFuture.runAsync(() -> {
			StopWatch watch = new StopWatch();
			watch.start();
			publishShp(file, request);
			watch.stop();
			onSuccessPublishShp(layer, request, watch);
		}, fileThreadPool).exceptionally(ex -> onFailurePublishShp(layer));
	}

	private void publishShp(File file, PublishShpRequest request) {
		String path = null;
		JDBCDataStore jdbcDataStore = null;
		try {
			path = ZipUtils.unzip(file, request.getLayerName());
			FileDirectoryUtils.checkShpDirectory(path);

			SimpleFeatureSource simpleFeatureSource = shpService.readFile(new File(path + File.separator + request.getLayerName() + ".shp"));
			jdbcDataStore = (JDBCDataStore) DataStoreFinder.getDataStore(properties.toMap());
			JDBCDataStore ds = shpService.createTable(jdbcDataStore, simpleFeatureSource, null);
			shpService.write2db(ds, simpleFeatureSource);

			String tableName = request.getLayerName();
			String storeName = request.getStoreName();
			GSPostGISDatastoreEncoder store = geoServerBuilder.buildGSPostGISDatastoreEncoder(storeName);
			boolean storeCreated = geoServerRESTManager.getStoreManager().create(request.getWorkspace(), store);
			log.info("[PublishServiceImpl] publishShp() create datastore {} {}", storeName, storeCreated);

			GSFeatureTypeEncoder pds = new GSFeatureTypeEncoder();
			pds.setTitle(tableName);
			pds.setName(tableName);
			pds.setSRS("EPSG:4326");
			GSLayerEncoder layerEncoder = new GSLayerEncoder();
			boolean published = geoServerRESTManager.getPublisher().publishDBLayer(request.getWorkspace(), storeName, pds, layerEncoder);
			log.info("[PublishServiceImpl] publishShp() publish layer {} {}", request.getLayerName(), published);
		} catch (Exception e) {
			log.error("[PublishServiceImpl] publishShp() called with Params: request = {}, Error message = {}",
				request, Throwables.getStackTraceAsString(e));
			throw new BusinessException("发布shp失败");
		} finally {
			FileDirectoryUtils.delete(path);
			if (jdbcDataStore != null) {
				jdbcDataStore.dispose();
			}
		}
	}

	/**
	 * 发布shp成功的处理动作
	 *
	 * @param layer   layer
	 * @param request request
	 */
	private void onSuccessPublishShp(Layer layer, PublishShpRequest request, StopWatch watch) {
		// 存储仓库
		datastoreRepository.save(Datastore.builder()
			.name(request.getStoreName())
			.type("ShapeFile")
			.enabled(true)
			.workspace(request.getWorkspace())
			.deleted(false)
			.createdAt(new Date())
			.build());

		// 更新图层
		FeatureTypeResponse feature = featureTypeService.details(request.getWorkspace(), request.getLayerName());
		layer.setStatus(PublishLayerStatusEnum.success.name());
		layer.setCrs(feature.getFeatureType().getLatLonBoundingBox().getCrs());
		layer.setMinx(feature.getFeatureType().getLatLonBoundingBox().getMinx());
		layer.setMaxx(feature.getFeatureType().getLatLonBoundingBox().getMaxx());
		layer.setMiny(feature.getFeatureType().getLatLonBoundingBox().getMiny());
		layer.setMaxy(feature.getFeatureType().getLatLonBoundingBox().getMaxy());
		layer.setCost(watch.getTotalTimeMillis());
		layerRepository.save(layer);

		// 发送成功的消息通知
		messageNotificationRepository.save(MessageNotification.builder()
			.category("NOTIFICATION")
			.message(String.format("上传发布Shp文件【%s】成功", layer.getName()))
			.receiverId(1L)
			.read(false)
			.createdAt(new Date())
			.deleted(false)
			.build());
	}

	/**
	 * 发布shp失败的处理动作
	 *
	 * @param layer layer
	 * @return Void
	 */
	private Void onFailurePublishShp(Layer layer) {
		// 更新状态
		layer.setStatus(PublishLayerStatusEnum.failure.name());
		layerRepository.save(layer);

		// 发送失败的消息通知
		messageNotificationRepository.save(MessageNotification.builder()
			.category("WARN")
			.message(String.format("上传发布Shp文件【%s】失败", layer.getName()))
			.receiverId(1L)
			.read(false)
			.createdAt(new Date())
			.deleted(false)
			.build());
		return null;
	}
}
