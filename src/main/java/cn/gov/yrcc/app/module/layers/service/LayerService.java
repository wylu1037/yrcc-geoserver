package cn.gov.yrcc.app.module.layers.service;

import cn.gov.yrcc.app.database.schema.Layer;
import org.springframework.data.domain.Page;

public interface LayerService {

    Object list();

    Object details(String workspace, String layerName);

	Page<Layer> pages(Integer page, Integer size);
}
