package cn.gov.yrcc.app.module.layer.service;

import cn.gov.yrcc.app.module.layer.request.PublishShpRequest;
import cn.gov.yrcc.app.module.layer.request.PublishTifRequest;

public interface PublishService {

    void publishTif(PublishTifRequest request);

	void publishShp(PublishShpRequest request);
}
