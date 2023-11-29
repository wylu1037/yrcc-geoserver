package cn.gov.yrcc.app.module.layer.repository.impl;

import cn.gov.yrcc.app.database.schema.Layer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLayer extends JpaRepository<Layer, Long>, JpaSpecificationExecutor<Layer> {

	Layer findByNameAndWorkspace(String name, String workspace);
}
