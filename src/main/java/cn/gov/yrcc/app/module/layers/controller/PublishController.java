package cn.gov.yrcc.app.module.layers.controller;

import cn.gov.yrcc.app.module.layers.request.PublishTifRequest;
import cn.gov.yrcc.app.module.layers.service.PublishService;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "发布图层")
@RestController
@RequestMapping("/v1/layers")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/publish/tif")
    public BaseResult<Void> publishTiffDelivery(@ModelAttribute PublishTifRequest request) {
        publishService.publishTif(request);
        return BaseResult.success();
    }
}
