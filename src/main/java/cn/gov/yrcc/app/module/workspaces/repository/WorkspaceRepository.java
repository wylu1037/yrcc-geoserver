package cn.gov.yrcc.app.module.workspaces.repository;

import cn.gov.yrcc.app.database.schema.Workspace;
import org.springframework.data.domain.Page;

public interface WorkspaceRepository {

    Long create(Workspace workspace);

    void delete(Workspace workspace);

    Workspace findByName(String workspaceName);

    Page<Workspace> pages(int page, int size);
}
