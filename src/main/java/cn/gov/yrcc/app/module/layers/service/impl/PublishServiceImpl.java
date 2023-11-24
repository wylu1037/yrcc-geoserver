package cn.gov.yrcc.app.module.layers.service.impl;

import cn.gov.yrcc.app.module.layers.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layers.service.PublishService;
import com.google.common.base.Throwables;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class PublishServiceImpl implements PublishService {

    private final GeoServerRESTPublisher geoServerRESTPublisher;

    public PublishServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher) {
        this.geoServerRESTPublisher = geoServerRESTPublisher;
    }

    @Override
    public boolean publishTif(PublishTifRequest request) {
        boolean success;
        try {
            File file = request.toTifFile();
            success = geoServerRESTPublisher.publishGeoTIFF(request.getWorkspace(), request.getStoreName(), file);
            log.info("publish tif file {}", success ? "success" : "failure");
        } catch (IOException e) {
            log.error("[PublishServiceImpl] publishTif() called with Params: request = {}, Error message = {}",
                    request, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("文件读取异常");
        }
        return success;
    }
}
