package cn.gov.yrcc.app.module.datastores.service;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.internal.geoserver.entity.GSDatastore;
import org.springframework.data.domain.Page;

public interface DatastoreService {

    Boolean exists(String workspace, String datastoreName);

    Object list(String workspace);

    GSDatastore.Datastore details(String workspace, String datastoreName);

    Page<Datastore> pages(Integer page, Integer size);

    void delete(Long id);

    void enable(Long id);

    void disable(Long id);
}
