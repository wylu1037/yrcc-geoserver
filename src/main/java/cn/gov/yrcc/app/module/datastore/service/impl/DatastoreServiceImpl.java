package cn.gov.yrcc.app.module.datastore.service.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.module.datastore.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.datastore.service.DatastoreService;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.internal.geoserver.entity.GSDatastore;
import cn.gov.yrcc.utils.http.HttpUtils;
import cn.gov.yrcc.utils.json.JsonUtils;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatastoreServiceImpl implements DatastoreService {

    private final GeoServerRESTReader geoServerRESTReader;
    private final HttpUtils httpUtils;
    private final GeoServerURLManager geoServerURLManager;
    private final DatastoreRepository datastoreRepository;

    public DatastoreServiceImpl(GeoServerRESTReader geoServerRESTReader, HttpUtils httpUtils, GeoServerURLManager geoServerURLManager, DatastoreRepository datastoreRepository) {
        this.geoServerRESTReader = geoServerRESTReader;
        this.httpUtils = httpUtils;
        this.geoServerURLManager = geoServerURLManager;
        this.datastoreRepository = datastoreRepository;
    }

    @Override
    public Boolean exists(String workspace, String datastoreName) {
        return geoServerRESTReader.existsDatastore(workspace, datastoreName);
    }

    @Override
    public List<String> list(String workspace) {
		List<Datastore> datastoreList = datastoreRepository.findByWorkspace(workspace);
		return datastoreList.stream().map(Datastore::getName).collect(Collectors.toList());
	}

    @Override
    public GSDatastore.Datastore details(String workspace, String datastoreName) {
        boolean exists = geoServerRESTReader.existsWorkspace(workspace);
        if (!exists) {
            throw new BusinessException(GSErrorMessage.Workspace.NOT_EXISTS);
        }
        exists = geoServerRESTReader.existsDatastore(workspace, datastoreName);
        if (!exists) {
            throw new BusinessException(GSErrorMessage.Datastore.NOT_EXISTS);
        }
        String url = geoServerURLManager.retrieveDatastore(workspace, datastoreName);
        String response = httpUtils.get(url, geoServerURLManager.getBasicAuth());
        GSDatastore bean = JsonUtils.toBean(response, GSDatastore.class);
        if (bean == null) {
            throw new BusinessException("查询结果为空");
        }
        return bean.getDataStore();
    }

    @Override
    public Page<Datastore> pages(Integer page, Integer size) {
        return datastoreRepository.pages(page, size);
    }

    @Override
    public void delete(Long id) {
        Datastore datastore = datastoreRepository.findById(id);
        String url = geoServerURLManager.deleteDatastore(datastore.getWorkspace(), datastore.getName());
        httpUtils.delete(url, geoServerURLManager.getBasicAuth(), HttpStatus.SC_OK);
        datastoreRepository.delete(datastore);
    }

    @Override
    public void enable(Long id) {
        Datastore datastore = datastoreRepository.findById(id);
        datastore.setEnabled(true);
        datastoreRepository.save(datastore);
    }

    @Override
    public void disable(Long id) {
        Datastore datastore = datastoreRepository.findById(id);
        datastore.setEnabled(false);
        datastoreRepository.save(datastore);
    }
}
