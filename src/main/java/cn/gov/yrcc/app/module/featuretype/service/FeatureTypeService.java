package cn.gov.yrcc.app.module.featuretype.service;

import cn.gov.yrcc.app.module.featuretype.response.FeatureTypeResponse;

public interface FeatureTypeService {

	FeatureTypeResponse details(String workspaceName, String layerName);
}
