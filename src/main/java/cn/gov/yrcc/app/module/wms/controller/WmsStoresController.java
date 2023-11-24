package cn.gov.yrcc.app.module.wms.controller;

import cn.gov.yrcc.app.module.wms.service.WmsStoresService;
import cn.gov.yrcc.utils.base.BaseResult;
import it.geosolutions.geoserver.rest.decoder.RESTWmsStoreList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/wms")
public class WmsStoresController {

    private final WmsStoresService wmsStoresService;

    public WmsStoresController(WmsStoresService wmsStoresService) {
        this.wmsStoresService = wmsStoresService;
    }

    @GetMapping("store")
    public BaseResult<RESTWmsStoreList> getWmsStoresDelivery(@RequestParam("workspace") String workspace) {
        return BaseResult.success(wmsStoresService.getWmsStoresDelivery(workspace));
    }
}
