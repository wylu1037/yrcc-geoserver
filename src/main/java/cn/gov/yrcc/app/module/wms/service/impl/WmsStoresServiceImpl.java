package cn.gov.yrcc.app.module.wms.service.impl;

import cn.gov.yrcc.app.module.wms.service.WmsStoresService;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTWmsStoreList;
import org.springframework.stereotype.Service;

@Service
public class WmsStoresServiceImpl implements WmsStoresService {

    private final GeoServerRESTReader geoServerRESTReader;

    public WmsStoresServiceImpl(GeoServerRESTReader geoServerRESTReader) {
        this.geoServerRESTReader = geoServerRESTReader;
    }

    @Override
    public RESTWmsStoreList getWmsStoresDelivery(String workspace) {
        boolean exists = geoServerRESTReader.existsWorkspace(workspace);
        if (!exists) {
            throw new RuntimeException(String.format("工作空间%s不存在", workspace));
        }
        return geoServerRESTReader.getWmsStores(workspace);
    }
}
