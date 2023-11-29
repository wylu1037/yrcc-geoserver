package cn.gov.yrcc.utils.file;

import cn.gov.yrcc.internal.constant.FileSizeUnitEnum;
import cn.gov.yrcc.internal.error.BusinessException;

import java.io.File;

public class FileCalculator {

	private FileCalculator() {
		// privatization construction method
	}

	public static String calculateSize(File file) {
		return calculateSize(file, FileSizeUnitEnum.MB);
	}

	public static String calculateSize(File file, FileSizeUnitEnum unit) {
		if (file == null) {
			return "0MB";
		}
		// unit is byte
		long length = file.length();
		double size = 0d;
		switch (unit) {
			case KB -> size = (double) length / 1024;
			case MB -> size = (double) length / (1024 * 1024);
			case G -> size = (double) length / (1024 * 1024 * 1024);
			default -> throw new BusinessException("");
		}
		return String.format("%.2f", size) + unit.name();
	}
}
