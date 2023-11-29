package cn.gov.yrcc.app.module.coverage.service.impl;

import cn.gov.yrcc.app.module.coverage.response.CoverageResponse;
import cn.gov.yrcc.app.module.coverage.service.CoverageService;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.http.HttpUtils;
import cn.gov.yrcc.utils.json.JsonUtils;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import org.springframework.stereotype.Service;

@Service
public class CoverageServiceImpl implements CoverageService {

	private final GeoServerURLManager geoServerURLManager;
	private final GeoServerRESTReader geoServerRESTReader;
	private final HttpUtils httpUtils;

	public CoverageServiceImpl(GeoServerURLManager geoServerURLManager, GeoServerRESTReader geoServerRESTReader, HttpUtils httpUtils) {
		this.geoServerURLManager = geoServerURLManager;
		this.geoServerRESTReader = geoServerRESTReader;
		this.httpUtils = httpUtils;
	}

	@Override
	public CoverageResponse details(String workspaceName, String layerName) {
		RESTLayer resource = geoServerRESTReader.getLayer(workspaceName, layerName);
		if (resource == null) {
			throw new BusinessException("检索图层信息失败");
		}
		String resourceUrl = resource.getResourceUrl();
		resourceUrl = resourceUrl.substring(0, resourceUrl.lastIndexOf(".")) + ".json";

		String response = httpUtils.get(resourceUrl, geoServerURLManager.getBasicAuth());
		CoverageResponse coverage = JsonUtils.toBean(response, CoverageResponse.class);
		if (coverage == null) {
			throw new BusinessException("coverage为空");
		}
		return coverage;
	}
}
