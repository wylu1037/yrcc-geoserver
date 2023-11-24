package cn.gov.yrcc.app.module.workspaces.controller;

import cn.gov.yrcc.app.module.workspaces.service.WorkspaceService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping(value = "workspace")
    public BaseResult<List<String>> getWorkspacesDelivery() {
        return BaseResult.success(workspaceService.getWorkspaces());
    }
}
