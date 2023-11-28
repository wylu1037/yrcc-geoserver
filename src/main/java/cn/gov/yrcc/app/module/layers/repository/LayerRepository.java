package cn.gov.yrcc.app.module.layers.repository;

import cn.gov.yrcc.app.database.schema.Layer;
import org.springframework.data.domain.Page;

public interface LayerRepository {

	Long save(Layer layer);

	Layer findByNameAndWorkspace(String name, String workspace);

	Page<Layer> pages(Integer page, Integer size, boolean filter);
}
