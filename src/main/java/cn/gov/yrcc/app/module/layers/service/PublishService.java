package cn.gov.yrcc.app.module.layers.service;

import cn.gov.yrcc.app.module.layers.request.PublishTifRequest;

public interface PublishService {

    boolean publishTif(PublishTifRequest request);
}