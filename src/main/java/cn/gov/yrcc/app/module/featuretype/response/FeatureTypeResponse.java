package cn.gov.yrcc.app.module.featuretype.response;

import cn.gov.yrcc.internal.geoserver.entity.GSKeywords;
import cn.gov.yrcc.internal.geoserver.entity.GSLatLonBoundingBox;
import cn.gov.yrcc.internal.geoserver.entity.GSNativeBoundingBox;
import lombok.Data;

@Data
public class FeatureTypeResponse {

	private FeatureType featureType;

	@Data
	public static class FeatureType {
		private String name;
		private String srs;
		private GSNativeBoundingBox nativeBoundingBox;
		private GSLatLonBoundingBox latLonBoundingBox;
		private GSKeywords keywords;
	}
}
