package cn.gov.yrcc.app.module.datastores.service.impl;

import cn.gov.yrcc.app.module.datastores.service.DatastoreService;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.springframework.stereotype.Service;

@Service
public class DatastoreServiceImpl implements DatastoreService {

    private final GeoServerRESTReader geoServerRESTReader;

    public DatastoreServiceImpl(GeoServerRESTReader geoServerRESTReader) {
        this.geoServerRESTReader = geoServerRESTReader;
    }

    @Override
    public Boolean exists(String workspace, String datastoreName) {
        return geoServerRESTReader.existsDatastore(workspace, datastoreName);
    }

    @Override
    public Object list(String workspace) {
        return geoServerRESTReader.getDatastores(workspace).getNames();
    }
}
