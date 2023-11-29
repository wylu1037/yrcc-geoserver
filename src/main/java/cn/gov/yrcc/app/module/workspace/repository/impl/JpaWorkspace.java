package cn.gov.yrcc.app.module.workspace.repository.impl;

import cn.gov.yrcc.app.database.schema.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWorkspace extends PagingAndSortingRepository<Workspace, Long>, JpaRepository<Workspace, Long> {

    Workspace findTop1ByName(String workspaceName);
}
