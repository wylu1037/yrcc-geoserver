package cn.gov.yrcc.app.module.workspaces.service;

import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.workspaces.request.CreateWorkspaceRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WorkspaceService {

    List<String> getWorkspaces();

    Boolean exists(String workspaceName);

    Long create(CreateWorkspaceRequest request);

    void delete(String workspaceName);

    Page<Workspace> pages(int page, int size);

    void enable(String workspaceName);

    void disable(String workspaceName);
}
