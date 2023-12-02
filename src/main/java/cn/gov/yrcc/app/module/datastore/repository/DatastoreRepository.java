package cn.gov.yrcc.app.module.datastore.repository;

import cn.gov.yrcc.app.database.schema.Datastore;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DatastoreRepository {

    Long save(Datastore datastore);

    Page<Datastore> pages(Integer page, Integer size);

    Datastore findById(Long id);

    void delete(Datastore datastore);

	List<Datastore> findAll();

	List<Datastore> findByWorkspace(String workspace);

	Datastore findByWorkspaceAndName(String workspace, String name);
}
