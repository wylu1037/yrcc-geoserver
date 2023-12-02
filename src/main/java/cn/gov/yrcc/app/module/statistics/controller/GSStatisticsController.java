package cn.gov.yrcc.app.module.statistics.controller;

import cn.gov.yrcc.app.module.statistics.service.GSStatisticsService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "GeoServer统计")
@RestController
@RequestMapping("/v1/statistics")
public class GSStatisticsController {

	private final GSStatisticsService gsStatisticsService;

	public GSStatisticsController(GSStatisticsService gsStatisticsService) {
		this.gsStatisticsService = gsStatisticsService;
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "gs总览统计", method = "GET")
	@GetMapping("/gs/overview/count")
	public BaseResult<Map<String, Long>> statisticsGSOverviewDelivery() {
		return BaseResult.success(gsStatisticsService.statisticsOverview());
	}
}
