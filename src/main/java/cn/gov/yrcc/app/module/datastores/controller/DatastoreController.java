package cn.gov.yrcc.app.module.datastores.controller;

import cn.gov.yrcc.app.module.datastores.service.DatastoreService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class DatastoreController {

    private final DatastoreService datastoreService;

    public DatastoreController(DatastoreService datastoreService) {
        this.datastoreService = datastoreService;
    }

    @GetMapping("datastore/exists")
    public BaseResult<Boolean> existsDelivery(
            @RequestParam("workspace") String workspace,
            @RequestParam("datastoreName") String datastoreName) {
        return BaseResult.success(datastoreService.exists(workspace, datastoreName));
    }

    @GetMapping("datastore/list")
    public BaseResult<Object> listDelivery(@RequestParam("workspace") String workspace) {
        return BaseResult.success(datastoreService.list(workspace));
    }
}
