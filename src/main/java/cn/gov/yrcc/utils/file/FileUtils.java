package cn.gov.yrcc.utils.file;

import cn.gov.yrcc.internal.error.BusinessException;
import com.google.common.base.Throwables;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FileUtils {

	public static void download(HttpServletResponse response, InputStream stream, String fileName) {
		byte[] buffer = new byte[1024];
		try {
			response.setContentType("application/x-download");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			ServletOutputStream out = response.getOutputStream();
			int length;
			while ((length = stream.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			stream.close();
			out.close();
			out.flush();
		} catch (IOException e) {
			log.error("[FileUtils] download() called with Params: response = {}, stream = {}, fileName = {}, Error message = {}",
				response, stream, fileName, Throwables.getStackTraceAsString(e));
			throw new BusinessException(String.format("下载文件%s失败", fileName));
		}
	}
}
