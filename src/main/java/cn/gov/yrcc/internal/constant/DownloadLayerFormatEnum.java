package cn.gov.yrcc.internal.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DownloadLayerFormatEnum {

	PNG("image/png", ".png"),

	JPEG("image/jpeg", ".jpeg"),

	SHP("SHAPE-ZIP", ".zip"),

	TIF("image/tiff", ".tif");

	private final String format;
	private final String suffix;
}

