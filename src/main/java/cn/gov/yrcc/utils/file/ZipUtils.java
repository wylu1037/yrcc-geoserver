package cn.gov.yrcc.utils.file;


import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipUtils {

	public static String unzip(File zipFile, String layerName) {
		String destDir = "/app/tmp/" + System.currentTimeMillis();
		File destDirectory = new File(destDir);
		if (!destDirectory.exists()) {
			boolean mkdir = destDirectory.mkdir();
			log.debug("ZipUtils create dir {} {}", destDirectory, mkdir);
		}

		byte[] buffer = new byte[1024];
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {

				File newFile = new File(destDir + File.separator + zipEntry.getName());
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipInputStream.closeEntry();
		} catch (IOException e) {
			log.error("[ZipUtils] unzip() called with Params: zipFile = {}, Error message = {}",
				zipFile, Throwables.getStackTraceAsString(e));
			throw new RuntimeException(e);
		}
		return destDir;
	}
}
