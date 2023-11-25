package cn.gov.yrcc.app.module.workspaces.controller;

import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.workspaces.request.CreateWorkspaceRequest;
import cn.gov.yrcc.app.module.workspaces.service.WorkspaceService;
import cn.gov.yrcc.utils.base.BaseResult;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping(value = "workspace")
    public BaseResult<List<String>> getWorkspacesDelivery() {
        return BaseResult.success(workspaceService.getWorkspaces());
    }

    @GetMapping("workspace/exists")
    public BaseResult<Boolean> existsDelivery(@RequestParam("workspaceName") String workspaceName) {
        return BaseResult.success(workspaceService.exists(workspaceName));
    }

    @PostMapping("workspace/create")
    public BaseResult<Long> createDelivery(@RequestBody CreateWorkspaceRequest request) {
        return BaseResult.success(workspaceService.create(request));
    }

    @DeleteMapping("workspace/{workspaceName}")
    public BaseResult<Void> deleteDelivery(@PathVariable("workspaceName") String workspaceName) {
        workspaceService.delete(workspaceName);
        return BaseResult.success();
    }

    @GetMapping("workspace/pages/{page}/{size}")
    public BaseResult<Page<Workspace>> pagesDelivery(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        return BaseResult.success(workspaceService.pages(page, size));
    }
}
