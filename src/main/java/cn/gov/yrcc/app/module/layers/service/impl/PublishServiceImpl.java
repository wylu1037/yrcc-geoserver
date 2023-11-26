package cn.gov.yrcc.app.module.layers.service.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.database.schema.MessageNotification;
import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.datastores.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.layers.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layers.service.PublishService;
import cn.gov.yrcc.app.module.message.repository.MessageNotificationRepository;
import cn.gov.yrcc.app.module.workspaces.repository.WorkspaceRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import com.google.common.base.Throwables;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class PublishServiceImpl implements PublishService {

    private final GeoServerRESTPublisher geoServerRESTPublisher;
    private final WorkspaceRepository workspaceRepository;
    private final Executor fileThreadPool;
    private final DatastoreRepository datastoreRepository;
    private final MessageNotificationRepository messageNotificationRepository;

    public PublishServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher, WorkspaceRepository workspaceRepository, Executor fileThreadPool, DatastoreRepository datastoreRepository, MessageNotificationRepository messageNotificationRepository) {
        this.geoServerRESTPublisher = geoServerRESTPublisher;
        this.workspaceRepository = workspaceRepository;
        this.fileThreadPool = fileThreadPool;
        this.datastoreRepository = datastoreRepository;
        this.messageNotificationRepository = messageNotificationRepository;
    }

    @Override
    public void publishTif(PublishTifRequest request) {
        Workspace workspace = workspaceRepository.findByName(request.getWorkspace());
        if (workspace == null) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }

        // 提交异步上传任务
        CompletableFuture.runAsync(() -> {
            File file;
            try {
                boolean success;
                file = request.toTifFile();
                success = geoServerRESTPublisher.publishGeoTIFF(request.getWorkspace(), request.getStoreName(), file);
                log.info("publish tif file {}", success ? "success" : "failure");
            } catch (IOException e) {
                log.error("[PublishServiceImpl] publishTif() called with Params: request = {}, Error message = {}",
                        request, Throwables.getStackTraceAsString(e));
                throw new BusinessException("文件存储发布异常");
            }

        }, fileThreadPool).whenComplete((result, ex) -> {
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
