package cn.gov.yrcc.app.module.coverage.response;

import cn.gov.yrcc.internal.geoserver.entity.GSKeywords;
import cn.gov.yrcc.internal.geoserver.entity.GSLatLonBoundingBox;
import cn.gov.yrcc.internal.geoserver.entity.GSNativeBoundingBox;
import lombok.Data;

@Data
public class CoverageResponse {

	private Coverage coverage;

	@Data
	public static class Coverage {
		private String name;
		private String srs;
		private GSNativeBoundingBox nativeBoundingBox;
		private GSLatLonBoundingBox latLonBoundingBox;
		private GSKeywords keywords;
	}
}
