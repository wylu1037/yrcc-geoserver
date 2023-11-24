package cn.gov.yrcc.app.module.layers.service;

public interface LayerService {

    Object list();

    Object details(String workspace, String layerName);
}
