package cn.gov.yrcc.utils.file;

import cn.gov.yrcc.internal.error.BusinessException;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@Slf4j
public class FileDirectoryUtils {

	enum ShpSuffix {
		dbf,
		prj,
		shp,
		shx
	}

	/**
	 * 检查shp文件夹是否有效
	 *
	 * @param path 目录
	 */
	public static void checkShpDirectory(String path) {
		int dbfCount = 0;
		int prjCount = 0;
		int shpCount = 0;
		int shxCount = 0;
		File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new BusinessException("文件夹不存在");
		}
		File[] files = folder.listFiles();
		if (files == null) {
			throw new BusinessException("");
		}
		for (File file : files) {
			String fileName = file.getName();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			Optional<ShpSuffix> optional = Enums.getIfPresent(ShpSuffix.class, suffix);
			if (!optional.isPresent()) {
				continue;
			}
			switch (optional.get()) {
				case dbf -> dbfCount = dbfCount + 1;
				case prj -> prjCount = prjCount + 1;
				case shp -> shpCount = shpCount + 1;
				case shx -> shxCount = shxCount + 1;
			}
		}
		if (dbfCount == 0) {
			throw new BusinessException("dbf文件缺失");
		}
		if (prjCount == 0) {
			throw new BusinessException("prj文件缺失");
		}
		if (shpCount == 0) {
			throw new BusinessException("shp文件缺失");
		}
		if (shxCount == 0) {
			throw new BusinessException("shx文件缺失");
		}
	}

	/**
	 * 重命名文件
	 *
	 * @param file 文件
	 * @param name 文件名
	 */
	public static void rename(File file, String name) {
		String originName = file.getName();
		File newFile = new File(file.getParent(), name + originName.substring(originName.lastIndexOf(".")));
		try {
			boolean result = file.renameTo(newFile);
			log.debug("[FileDirectoryUtils] rename() file {} to {} {}", file.getName(), name, result);
		} catch (Exception e) {
			log.error("[FileDirectoryUtils] rename() called with Params: file = {}, name = {}, Error message = {}",
				file, name, Throwables.getStackTraceAsString(e));
			throw new BusinessException(String.format("文件%s重命名%s失败", file.getName(), name));
		}
	}

	/**
	 * 重命名文件夹下的文件
	 *
	 * @param directoryPath 文件夹
	 * @param name          文件名
	 */
	public static void rename(String directoryPath, String name) {
		File folder = new File(directoryPath);
		if (!folder.exists()) {
			return;
		}
		File[] files = folder.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			rename(file, name);
		}
	}

	/**
	 * 删除文件夹
	 *
	 * @param folderPath 文件夹路径
	 */
	public static void delete(String folderPath) {
		if (StringUtils.isBlank(folderPath)) {
			return;
		}
		File folder = new File(folderPath);
		deleteFolder(folder);
	}

	/**
	 * 删除文件夹
	 *
	 * @param folder 文件夹
	 */
	public static void deleteFolder(File folder) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files == null) {
				return;
			}
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					boolean deleted = file.delete();
					if (!deleted) {
						System.out.println("无法删除文件：" + file.getName());
					}
				}
			}
		}
	}
}
