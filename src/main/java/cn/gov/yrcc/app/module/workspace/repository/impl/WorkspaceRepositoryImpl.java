package cn.gov.yrcc.app.module.workspace.repository.impl;

import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.workspace.repository.WorkspaceRepository;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkspaceRepositoryImpl implements WorkspaceRepository {

    private final JpaWorkspace jpaWorkspace;

    public WorkspaceRepositoryImpl(JpaWorkspace jpaWorkspace) {
        this.jpaWorkspace = jpaWorkspace;
    }

    @Override
    public Long save(Workspace workspace) {
        workspace = jpaWorkspace.save(workspace);
        return workspace.getId();
    }

    @Override
    public void delete(Workspace workspace) {
        jpaWorkspace.delete(workspace);
    }

    @Override
    public Workspace findByName(String workspaceName) {
        Workspace workspace = jpaWorkspace.findTop1ByName(workspaceName);
        if (workspace == null) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }
        return workspace;
    }

    @Override
    public Page<Workspace> pages(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return jpaWorkspace.findAll(pageRequest);
    }

	@Override
	public List<Workspace> findAll() {
		return jpaWorkspace.findAll();
	}

	@Override
	public long count() {
		return jpaWorkspace.count();
	}
}
