package cn.gov.yrcc.app.module.layers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishTifRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3141508765117809099L;

    @JsonIgnore
    @Schema(description = "TIFF文件，ZIP包")
    private MultipartFile file;

    @Schema(description = "工作空间")
    private String workspace;

    @Schema(description = "存储仓库名称")
    private String storeName;

    public File toTifFile() throws IOException {
        String filename = this.file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            filename = "file.tif";
        }
        int index = filename.lastIndexOf(".");
        var tempFile = Files.createTempFile(filename.substring(0, index), filename.substring(index));
        file.transferTo(tempFile);
        return tempFile.toFile();
    }
}
