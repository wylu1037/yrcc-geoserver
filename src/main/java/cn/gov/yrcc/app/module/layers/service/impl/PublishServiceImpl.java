package cn.gov.yrcc.app.module.layers.service.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.datastores.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.layers.repository.LayerRepository;
import cn.gov.yrcc.app.module.layers.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layers.response.CoverageResponse;
import cn.gov.yrcc.app.module.layers.service.PublishService;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.app.module.workspaces.repository.WorkspaceRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.http.HttpUtils;
import cn.gov.yrcc.utils.json.JsonUtils;
import com.google.common.base.Throwables;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
	private final GeoServerRESTReader geoServerRESTReader;
	private final HttpUtils httpUtils;
	private final GeoServerURLManager geoServerURLManager;

    public PublishServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher, WorkspaceRepository workspaceRepository, Executor fileThreadPool, DatastoreRepository datastoreRepository, MessageNotificationRepository messageNotificationRepository, LayerRepository layerRepository, GeoServerRESTReader geoServerRESTReader, HttpUtils httpUtils, GeoServerURLManager geoServerURLManager) {
        this.geoServerRESTPublisher = geoServerRESTPublisher;
        this.workspaceRepository = workspaceRepository;
        this.fileThreadPool = fileThreadPool;
        this.datastoreRepository = datastoreRepository;
        this.messageNotificationRepository = messageNotificationRepository;
		this.layerRepository = layerRepository;
		this.geoServerRESTReader = geoServerRESTReader;
		this.httpUtils = httpUtils;
		this.geoServerURLManager = geoServerURLManager;
	}

    @Override
    public void publishTif(PublishTifRequest request) {
        Workspace workspace = workspaceRepository.findByName(request.getWorkspace());
        if (workspace == null) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }

		// 发布前存储
		Layer entity = layerRepository.findByNameAndWorkspace(request.getLayerName(), request.getWorkspace());
		if (entity == null) {
			layerRepository.save(Layer.builder()
				.name(request.getLayerName())
				.title(request.getLayerName())
				.workspace(request.getWorkspace())
				.datastore(request.getStoreName())
				.type("VECTOR")
				.status("uploading")
				.enable(true)
				.createdAt(new Date())
				.deleted(false)
				.build());
		}

        // 提交异步上传任务
        CompletableFuture.runAsync(() -> {
            File file;
            try {
                boolean success;
                file = request.toTifFile();
                success = geoServerRESTPublisher.publishGeoTIFF(request.getWorkspace(), request.getStoreName(), file);
                log.info("publish tif file {}", success ? "success" : "failure");
				if (!success) {
					throw new BusinessException("文件存储发布失败");
				}
            } catch (IOException e) {
                log.error("[PublishServiceImpl] publishTif() called with Params: request = {}, Error message = {}",
                        request, Throwables.getStackTraceAsString(e));
                throw new BusinessException("文件存储发布异常");
            }

        }, fileThreadPool).whenComplete((result, ex) -> {
			Layer layer = layerRepository.findByNameAndWorkspace(request.getLayerName(), request.getWorkspace());
            if (ex == null) {
                // 存储仓库
                datastoreRepository.save(Datastore.builder()
                        .name(request.getStoreName())
                        .type("GeoTiff")
                        .enabled(true)
                        .workspace(request.getWorkspace())
                        .deleted(false)
                        .createdAt(new Date())
                        .build());
				// 查询并更新图层
				RESTLayer resource = geoServerRESTReader.getLayer(request.getWorkspace(), request.getLayerName());
				String url = resource.getResourceUrl().replace(".xml", ".json");
				String json = httpUtils.get(url, geoServerURLManager.getBasicAuth());
				CoverageResponse coverage = JsonUtils.toBean(json, CoverageResponse.class);
				layer.setStatus("success");
				layer.setCrs(coverage.getCoverage().getLatLonBoundingBox().getCrs());
				layer.setMinx(coverage.getCoverage().getLatLonBoundingBox().getMinx());
				layer.setMaxx(coverage.getCoverage().getLatLonBoundingBox().getMaxx());
				layer.setMiny(coverage.getCoverage().getLatLonBoundingBox().getMiny());
				layer.setMaxy(coverage.getCoverage().getLatLonBoundingBox().getMaxy());
				layerRepository.save(layer);

				// 发送成功的消息通知
                messageNotificationRepository.save(MessageNotification.builder()
                        .category("NOTIFICATION")
                        .message(String.format("上传发布Tif文件【%s】成功", request.getFile().getOriginalFilename()))
                        .receiverId(1L)
                        .read(false)
                        .createdAt(new Date())
                        .deleted(false)
                        .build());
            } else {
				// 更新图层信息
				layer.setStatus("failure");
				layerRepository.save(layer);

                // 发送失败的消息通知
                messageNotificationRepository.save(MessageNotification.builder()
                        .category("WARN")
                        .message(String.format("上传发布Tif文件【%s】失败", request.getFile().getOriginalFilename()))
                        .receiverId(1L)
                        .read(false)
                        .createdAt(new Date())
                        .deleted(false)
                        .build());
            }
        });
    }
}
