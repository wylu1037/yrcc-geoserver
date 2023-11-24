package cn.gov.yrcc.app.module.layers.controller;

import cn.gov.yrcc.app.module.layers.service.LayerService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
