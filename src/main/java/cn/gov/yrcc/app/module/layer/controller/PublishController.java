package cn.gov.yrcc.app.module.layer.controller;

import cn.gov.yrcc.app.module.layer.request.PublishShpRequest;
import cn.gov.yrcc.app.module.layer.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layer.service.PublishService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "发布图层")
@RestController
@RequestMapping("/v1")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "分页查询图层", method = "GET")
    @PostMapping("/layer/publish/tif")
    public BaseResult<Void> publishTiffDelivery(@ModelAttribute PublishTifRequest request) {
        publishService.publishTif(request);
        return BaseResult.success();
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "分页查询图层", method = "GET")
	@PostMapping("/layer/publish/shp")
	public BaseResult<Void> publishShpDelivery(@ModelAttribute PublishShpRequest request) {
		publishService.publishShp(request);
		return BaseResult.success();
	}
}
