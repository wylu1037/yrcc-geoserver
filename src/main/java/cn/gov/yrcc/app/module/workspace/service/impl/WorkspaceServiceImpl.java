package cn.gov.yrcc.app.module.workspace.service.impl;

import cn.gov.yrcc.app.database.schema.Workspace;
import cn.gov.yrcc.app.module.workspace.repository.WorkspaceRepository;
import cn.gov.yrcc.app.module.workspace.request.CreateWorkspaceRequest;
import cn.gov.yrcc.app.module.workspace.service.WorkspaceService;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.http.HttpUtils;
import cn.gov.yrcc.utils.json.JsonUtils;
import com.google.common.collect.ImmutableMap;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import jakarta.transaction.Transactional;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private final GeoServerRESTReader geoServerRESTReader;
    private final GeoServerURLManager geoServerURLManager;
    private final HttpUtils httpUtils;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(GeoServerRESTReader geoServerRESTReader, GeoServerURLManager geoServerURLManager, HttpUtils httpUtils, WorkspaceRepository workspaceRepository) {
        this.geoServerRESTReader = geoServerRESTReader;
        this.geoServerURLManager = geoServerURLManager;
        this.httpUtils = httpUtils;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public List<String> getWorkspaces() {
		List<Workspace> workspace = workspaceRepository.findAll();
		return workspace.stream().map(Workspace::getName).collect(Collectors.toList());
	}

    @Override
    public Boolean exists(String workspaceName) {
        return geoServerRESTReader.existsWorkspace(workspaceName);
    }

    @Override
    public Long create(CreateWorkspaceRequest request) {
        Boolean exists = this.exists(request.getName());
        if (exists) {
            throw new BusinessException(GSErrorMessage.Workspace.ALREADY_EXISTS);
        }
        String body = JsonUtils.toJsonString(ImmutableMap.of("workspace", ImmutableMap.of("name", request.getName())));
        httpUtils.post(geoServerURLManager.createWorkspace(), body, geoServerURLManager.getBasicAuth(), HttpStatus.SC_CREATED);

        return workspaceRepository.save(Workspace.builder()
                .name(request.getName())
                .enable(request.isEnable())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build());
    }

    @Override
    public void delete(String workspaceName) {
        Boolean exists = this.exists(workspaceName);
        if (!exists) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }
        httpUtils.delete(geoServerURLManager.deleteWorkspace(workspaceName), geoServerURLManager.getBasicAuth(), HttpStatus.SC_OK);
        Workspace workspace = workspaceRepository.findByName(workspaceName);
        workspaceRepository.delete(workspace);
    }

    @Override
    public Page<Workspace> pages(int page, int size) {
        return workspaceRepository.pages(page, size);
    }

    @Override
    public void enable(String workspaceName) {
        Boolean exists = this.exists(workspaceName);
        if (!exists) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }
        Workspace workspace = workspaceRepository.findByName(workspaceName);
        workspace.setEnable(true);
        workspaceRepository.save(workspace);
    }

    @Override
    public void disable(String workspaceName) {
        Boolean exists = this.exists(workspaceName);
        if (!exists) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }
        Workspace workspace = workspaceRepository.findByName(workspaceName);
        workspace.setEnable(false);
        workspaceRepository.save(workspace);
    }
}
