package cn.gov.yrcc.app.module.workspace.repository;

import cn.gov.yrcc.app.database.schema.Workspace;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WorkspaceRepository {

    Long save(Workspace workspace);

    void delete(Workspace workspace);

    Workspace findByName(String workspaceName);

    Page<Workspace> pages(int page, int size);

	List<Workspace> findAll();

	long count();
}
