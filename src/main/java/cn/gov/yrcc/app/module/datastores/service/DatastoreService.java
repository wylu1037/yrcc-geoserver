package cn.gov.yrcc.app.module.datastores.service;

import cn.gov.yrcc.internal.geoserver.entity.GSDatastore;

public interface DatastoreService {

    Boolean exists(String workspace, String datastoreName);

    Object list(String workspace);

    GSDatastore.Datastore details(String workspace, String datastoreName);
}
