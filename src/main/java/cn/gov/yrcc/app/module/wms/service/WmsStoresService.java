package cn.gov.yrcc.app.module.wms.service;

import it.geosolutions.geoserver.rest.decoder.RESTWmsStoreList;

public interface WmsStoresService {

    RESTWmsStoreList getWmsStoresDelivery(String workspace);
}
