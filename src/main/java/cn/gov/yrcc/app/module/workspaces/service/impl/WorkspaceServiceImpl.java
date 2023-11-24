package cn.gov.yrcc.app.module.workspaces.service.impl;

import cn.gov.yrcc.app.module.workspaces.service.WorkspaceService;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final GeoServerRESTReader geoServerRESTReader;

    public WorkspaceServiceImpl(GeoServerRESTReader geoServerRESTReader) {
        this.geoServerRESTReader = geoServerRESTReader;
    }

    @Override
    public List<String> getWorkspaces() {
         return geoServerRESTReader.getWorkspaceNames();
    }
}
