package cn.gov.yrcc.utils.file;


import cn.gov.yrcc.internal.error.BusinessException;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipUtils {

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static String unzip(File zipFile, String layerName) {
		String directoryPath = "/app/tmp/" + System.currentTimeMillis();
		File targetDirectory = new File(directoryPath);
		if (!targetDirectory.exists()) {
			targetDirectory.mkdir();
		}

		try (ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry;
			while ((zipEntry = zip.getNextEntry()) != null) {
				String name = zipEntry.getName();
				File file = new File(directoryPath + File.separator + name);
				if (name.endsWith(File.separator)) {
					continue;
				}
				BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
				byte[] bytes = new byte[1024];
				int num;
				while ((num = zip.read(bytes, 0, bytes.length)) > 0) {
					outputStream.write(bytes, 0, num);
				}
				outputStream.close();
			}
		} catch (Exception e) {
			log.error("[ZipUtils] unzip() called with Params: zipFile = {}, layerName = {}, Error message = {}",
				zipFile, layerName, Throwables.getStackTraceAsString(e));
			throw new BusinessException("解压zip文件异常");
		}
		return directoryPath;
	}
}
