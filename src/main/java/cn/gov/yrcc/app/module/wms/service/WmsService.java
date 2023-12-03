package cn.gov.yrcc.app.module.wms.service;

import cn.gov.yrcc.app.module.wms.request.DownloadLayerRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WmsService {

    Object getLegendGraphic(String workspace, String layerName);

	void getMap(HttpServletResponse response, DownloadLayerRequest request);
}
