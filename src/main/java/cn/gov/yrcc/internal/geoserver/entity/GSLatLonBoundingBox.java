package cn.gov.yrcc.internal.geoserver.entity;

import lombok.Data;

@Data
public class GSLatLonBoundingBox {

	private Double minx;
	private Double maxx;
	private Double miny;
	private Double maxy;
	private String crs;
}
