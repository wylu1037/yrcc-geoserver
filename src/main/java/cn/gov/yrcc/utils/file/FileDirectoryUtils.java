package cn.gov.yrcc.utils.file;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Set;


public class FileDirectoryUtils {

	private static final Set<String> SHP = ImmutableSet.of(".dbf", ".pri",".shp", ".shx");

	public static boolean isValidShp(String path) {
		int count = 0;
		File folder = new File(path); // 指定文件夹路径
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles(); // 获取文件夹下的文件列表
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					String suffix = fileName.substring(fileName.lastIndexOf("."));
					if (SHP.contains(suffix)) {
						count = count+1;
					}
				}
			}
		}
		return true;
	}
}
