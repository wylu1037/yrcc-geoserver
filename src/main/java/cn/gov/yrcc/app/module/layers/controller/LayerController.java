package cn.gov.yrcc.app.module.layers.controller;

import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.module.layers.service.LayerService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "图层")
@RestController
@RequestMapping("/v1")
public class LayerController {

    private final LayerService layerService;

    public LayerController(LayerService layerService) {
        this.layerService = layerService;
    }

    @GetMapping("layer/list")
    public BaseResult<Object> get() {
        return BaseResult.success(layerService.list());
    }

    @GetMapping("layer/details")
    public BaseResult<Object> detailsDelivery(
            @RequestParam("workspace") String workspace,
            @RequestParam("layerName") String layerName) {
        return BaseResult.success(layerService.details(workspace, layerName));
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "分页查询图层", method = "GET")
	@GetMapping("layer/pages/{page}/{size}")
	public BaseResult<Page<Layer>> pagesDelivery(
		@Parameter(description = "页码", in = ParameterIn.PATH) @PathVariable("page") Integer page,
		@Parameter(description = "行数", in = ParameterIn.PATH) @PathVariable("size") Integer size) {
		return BaseResult.success(layerService.pages(page, size, false));
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "分页查询上传失败的图层", method = "GET")
	@GetMapping("layer/unfinished/pages/{page}/{size}")
	public BaseResult<Page<Layer>> pagesUnfinishedDelivery(
		@Parameter(description = "页码", in = ParameterIn.PATH) @PathVariable("page") Integer page,
		@Parameter(description = "行数", in = ParameterIn.PATH) @PathVariable("size") Integer size) {
		return BaseResult.success(layerService.pages(page, size, true));
	}
}
