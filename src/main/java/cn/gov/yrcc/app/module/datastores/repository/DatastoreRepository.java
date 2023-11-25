package cn.gov.yrcc.app.module.datastores.repository;

import cn.gov.yrcc.app.database.schema.Datastore;

public interface DatastoreRepository {

    Long save(Datastore datastore);
}
