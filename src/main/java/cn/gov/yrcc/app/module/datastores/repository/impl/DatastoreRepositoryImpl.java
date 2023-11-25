package cn.gov.yrcc.app.module.datastores.repository.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.module.datastores.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DatastoreRepositoryImpl implements DatastoreRepository {

    private final JpaDatastore jpaDatastore;

    public DatastoreRepositoryImpl(JpaDatastore jpaDatastore) {
        this.jpaDatastore = jpaDatastore;
    }

    @Override
    public Long save(Datastore datastore) {
        Datastore save = this.jpaDatastore.save(datastore);
        return save.getId();
    }
}
