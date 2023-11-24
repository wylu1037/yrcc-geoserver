package cn.gov.yrcc.app.module.wms.service.impl;

import cn.gov.yrcc.app.module.wms.service.WmsLayersService;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.springframework.stereotype.Service;

@Service
public class WmsLayersServiceImpl implements WmsLayersService {

    private final GeoServerRESTPublisher geoServerRESTPublisher;
    private final GeoServerRESTManager geoServerRESTManager;
    private final GeoServerRESTReader geoServerRESTReader;

    public WmsLayersServiceImpl(GeoServerRESTPublisher geoServerRESTPublisher, GeoServerRESTManager geoServerRESTManager, GeoServerRESTReader geoServerRESTReader) {
        this.geoServerRESTPublisher = geoServerRESTPublisher;
        this.geoServerRESTManager = geoServerRESTManager;
        this.geoServerRESTReader = geoServerRESTReader;
    }

    @Override
    public Object getWmsLayers() {
        geoServerRESTReader.getWms("ne", "");
        return null;
    }
}
