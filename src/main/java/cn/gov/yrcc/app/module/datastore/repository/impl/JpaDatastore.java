package cn.gov.yrcc.app.module.datastore.repository.impl;

import cn.gov.yrcc.app.database.schema.Datastore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDatastore extends JpaRepository<Datastore, Long>, JpaSpecificationExecutor<Datastore> {

	Datastore findByWorkspaceAndName(String workspace, String name);
}
