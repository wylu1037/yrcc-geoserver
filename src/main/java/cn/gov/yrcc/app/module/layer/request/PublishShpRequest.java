package cn.gov.yrcc.app.module.layer.request;

import cn.gov.yrcc.internal.error.BusinessException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Throwables;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishShpRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 7804226473519997709L;

	@JsonIgnore
	@Schema(description = "TIFF文件，ZIP包")
	private MultipartFile file;

	@Schema(description = "工作空间")
	private String workspace;

	@Schema(description = "存储仓库名称")
	private String storeName;

	@Schema(description = "发布图层的名称")
	private String layerName;

	public File toShpFile() {
		String filename = this.file.getOriginalFilename();
		if (StringUtils.isBlank(filename)) {
			filename = "file.tif";
		}
		int index = filename.lastIndexOf(".");
		Path tempFile;
		try {
			tempFile = Files.createTempFile(filename.substring(0, index), filename.substring(index));
			file.transferTo(tempFile);
		} catch (IOException e) {
			log.error("[PublishShpRequest] toTifFile() called, Error message = {}", Throwables.getStackTraceAsString(e));
			throw new BusinessException("文件转换异常");
		}
		return tempFile.toFile();
	}
}
