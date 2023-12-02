package cn.gov.yrcc.utils.file;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipUtils {

	/**
	 *
	 * @param srcFile
	 * @param layerName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static String unzip(File srcFile, String layerName) throws Exception {
		String destDirPath = "/app/tmp/" + System.currentTimeMillis();
		if (!srcFile.exists()) {
			throw new Exception(srcFile.getPath() + "所指文件不存在");
		}

		try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(srcFile))) {
			ZipEntry entry;
			while ((entry = inputStream.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				File file = new File(destDirPath, entry.getName());
				if (!file.exists()) {
					new File(file.getParent()).mkdirs();
				}
				try (OutputStream out = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(out)) {
					int len;
					byte[] buf = new byte[1024];
					while ((len = inputStream.read(buf)) != -1) {
						bos.write(buf, 0, len);
					}
				}
			}
		}
		FileDirectoryUtils.rename(destDirPath, layerName);
		return destDirPath;
	}
}
