package cn.gov.yrcc.app.module.wms.controller;

import cn.gov.yrcc.app.module.wms.service.WmsService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/wms")
public class WmsController {

    private final WmsService wmsService;

    public WmsController(WmsService wmsService) {
        this.wmsService = wmsService;
    }

    @GetMapping("legend-graphic")
    public BaseResult<Object> getLegendGraphicDelivery(
            @RequestParam("workspace") String workspace,
            @RequestParam("layerName") String layerName) {
        return BaseResult.success(wmsService.getLegendGraphic(workspace, layerName));
    }
}
