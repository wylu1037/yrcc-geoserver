package cn.gov.yrcc.app.module.datastores.service;

public interface DatastoreService {

    Boolean exists(String workspace, String datastoreName);

    Object list(String workspace);
}
