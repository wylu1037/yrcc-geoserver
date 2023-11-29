package cn.gov.yrcc.app.module.layer.service.impl;

import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.module.layer.repository.LayerRepository;
import cn.gov.yrcc.app.module.layer.service.LayerService;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class LayerServiceImpl implements LayerService {

    private final GeoServerRESTReader geoServerRESTReader;

    private final GeoServerRESTManager geoServerRESTManager;
	private final LayerRepository layerRepository;

    public LayerServiceImpl(GeoServerRESTReader geoServerRESTReader, GeoServerRESTManager geoServerRESTManager, LayerRepository layerRepository) {
        this.geoServerRESTReader = geoServerRESTReader;
        this.geoServerRESTManager = geoServerRESTManager;
		this.layerRepository = layerRepository;
	}

    @Override
    public Object list() {
        RESTLayerList layers = geoServerRESTReader.getLayers();
        return layers.getNames();
    }

    @Override
    public Object details(String workspace, String layerName) {
        // http://192.168.1.115:8080/geoserver/ne/wms?
        // service=WMS
        // &version=1.1.0
        // &request=GetMap
        // &layers=ne%3AtestRESTStoreGeotiff
        // &bbox=590010.0%2C4914020.0%2C609000.0%2C4928000.0
        // &width=768
        // &height=565
        // &srs=EPSG%3A26713
        // &styles=
        // &format=application/openlayers
        return geoServerRESTReader.getLayer(workspace, layerName);
    }

	@Override
	public Page<Layer> pages(Integer page, Integer size, boolean filter) {
		return layerRepository.pages(page, size, filter);
	}
}
