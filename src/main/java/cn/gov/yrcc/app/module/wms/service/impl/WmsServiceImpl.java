package cn.gov.yrcc.app.module.wms.service.impl;

import cn.gov.yrcc.app.module.wms.service.WmsService;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.http.HttpUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class WmsServiceImpl implements WmsService {

    private final HttpUtils httpUtils;

    private final GeoServerURLManager geoServerURLManager;

    public WmsServiceImpl(HttpUtils httpUtils, GeoServerURLManager geoServerURLManager) {
        this.httpUtils = httpUtils;
        this.geoServerURLManager = geoServerURLManager;
    }

    @Override
    public Object getLegendGraphic(String workspace, String layerName) {
        String response = httpUtils.get(geoServerURLManager.getLegendGraphic(workspace, layerName), geoServerURLManager.getBasicAuth());
        return Base64.getEncoder().encodeToString(response.getBytes(StandardCharsets.UTF_8));
    }
}
