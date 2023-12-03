package cn.gov.yrcc.app.module.wms.controller;

import cn.gov.yrcc.app.module.wms.request.DownloadLayerRequest;
import cn.gov.yrcc.app.module.wms.service.WmsService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Web Map Service")
@RestController
@RequestMapping("/v1/wms")
public class WmsController {

    private final WmsService wmsService;

    public WmsController(WmsService wmsService) {
        this.wmsService = wmsService;
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "获取图例", method = "POST")
    @GetMapping("legend-graphic")
    public BaseResult<Object> getLegendGraphicDelivery(
            @Parameter(description = "工作空间名称", in = ParameterIn.QUERY) @RequestParam("workspaceName") String workspaceName,
			@Parameter(description = "图层名称", in = ParameterIn.QUERY) @RequestParam("layerName") String layerName) {
        return BaseResult.success(wmsService.getLegendGraphic(workspaceName, layerName));
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "下载图层文件", method = "POST")
	@PostMapping("layer/download")
	public BaseResult<Null> downloadLayerDelivery(
		@RequestBody DownloadLayerRequest request, HttpServletResponse response) {
		wmsService.getMap(response, request);
		return BaseResult.success();
	}
}
