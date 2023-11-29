package cn.gov.yrcc.app.module.coverage.service;

import cn.gov.yrcc.app.module.coverage.response.CoverageResponse;

public interface CoverageService {

	CoverageResponse details(String workspaceName, String layerName);
}
