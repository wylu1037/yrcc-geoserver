package cn.gov.yrcc.app.module.layers.response;

import lombok.Data;

@Data
public class CoverageResponse {

	private Coverage coverage;

	@Data
	public static class Coverage {

		private LatLonBoundingBox latLonBoundingBox;

		@Data
		public static class LatLonBoundingBox {
			private Double minx;
			private Double maxx;
			private Double miny;
			private Double maxy;
			private String crs;
		}
	}
}
