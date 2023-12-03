package cn.gov.yrcc.internal.geoserver.entity;

import lombok.Data;

@Data
public class GSNativeBoundingBox {
	private Double minx;
	private Double maxx;
	private Double miny;
	private Double maxy;
	private Crs crs;

	@Data
	public static class Crs {
		private String $;

		public String getCrs() {
			return this.$;
		}
	}

	public double calculate() {
		double height = this.maxy - this.miny;
		double width = this.maxx - this.minx;
		return height / width;
	}
}
