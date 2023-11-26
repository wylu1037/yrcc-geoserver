package cn.gov.yrcc.app.module.datastores.repository;

import cn.gov.yrcc.app.database.schema.Datastore;
import org.springframework.data.domain.Page;

public interface DatastoreRepository {

    Long save(Datastore datastore);

    Page<Datastore> pages(Integer page, Integer size);

    Datastore findById(Long id);

    void delete(Datastore datastore);
}
