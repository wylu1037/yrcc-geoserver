package cn.gov.yrcc.app.module.wms.request;

import cn.gov.yrcc.internal.constant.DownloadLayerFormatEnum;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DownloadLayerRequest {

	@Schema(description = "工作空间名称")
	private String workspaceName;

	@Schema(description = "图层名称")
	private String layerName;

	@Schema(description = "下载格式", example = "PNG/SHP/TIF")
	private String format = "PNG";

	@Schema(description = "下载后的文件名称")
	private String fileName;

	@Schema(description = "宽度，高度像素")
	private Integer width;

	@Schema(description = "高度，单位像素")
	private Integer height;

	public DownloadLayerFormatEnum getFormat() {
		Optional<DownloadLayerFormatEnum> optional = Enums.getIfPresent(DownloadLayerFormatEnum.class, this.format);
		if (optional.isPresent()) {
			return optional.get();
		}

		return DownloadLayerFormatEnum.JPEG;
	}
}
