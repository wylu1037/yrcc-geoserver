package cn.gov.yrcc.app.module.coverage.response;

import cn.gov.yrcc.internal.geoserver.entity.GSKeywords;
import cn.gov.yrcc.internal.geoserver.entity.GSLatLonBoundingBox;
import lombok.Data;

@Data
public class CoverageResponse {

	private Coverage coverage;

	@Data
	public static class Coverage {

		private String name;

		private GSLatLonBoundingBox latLonBoundingBox;

		private GSKeywords keywords;
	}
}
