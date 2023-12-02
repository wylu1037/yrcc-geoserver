package cn.gov.yrcc.app.module.featuretype.service.impl;

import cn.gov.yrcc.app.module.featuretype.response.FeatureTypeResponse;
import cn.gov.yrcc.app.module.featuretype.service.FeatureTypeService;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.http.HttpUtils;
import cn.gov.yrcc.utils.json.JsonUtils;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import org.springframework.stereotype.Service;

@Service
public class FeatureTypeServiceImpl implements FeatureTypeService {
	private final GeoServerURLManager geoServerURLManager;
	private final GeoServerRESTReader geoServerRESTReader;
	private final HttpUtils httpUtils;

	public FeatureTypeServiceImpl(GeoServerURLManager geoServerURLManager, GeoServerRESTReader geoServerRESTReader, HttpUtils httpUtils) {
		this.geoServerURLManager = geoServerURLManager;
		this.geoServerRESTReader = geoServerRESTReader;
		this.httpUtils = httpUtils;
	}

	@Override
	public FeatureTypeResponse details(String workspaceName, String layerName) {
		RESTLayer resource = geoServerRESTReader.getLayer(workspaceName, layerName);
		if (resource == null) {
			throw new BusinessException("检索图层信息失败");
		}
		String resourceUrl = resource.getResourceUrl();
		resourceUrl = resourceUrl.substring(0, resourceUrl.lastIndexOf(".")) + ".json";

		String response = httpUtils.get(resourceUrl, geoServerURLManager.getBasicAuth());
		FeatureTypeResponse feature = JsonUtils.toBean(response, FeatureTypeResponse.class);
		if (feature == null) {
			throw new BusinessException("feature type为空");
		}
		return feature;
	}
}
