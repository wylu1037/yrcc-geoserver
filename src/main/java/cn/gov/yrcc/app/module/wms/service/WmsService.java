package cn.gov.yrcc.app.module.wms.service;

import cn.gov.yrcc.app.module.wms.request.DownloadLayerRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WmsService {

    Object getLegendGraphic(String workspace, String layerName);

	byte[] getMap(HttpServletResponse response, DownloadLayerRequest request);

	void getMap2(HttpServletResponse response, DownloadLayerRequest request);
}
