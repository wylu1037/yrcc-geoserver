package cn.gov.yrcc.app.module.statistics.service.impl;

import cn.gov.yrcc.app.module.datastore.repository.DatastoreRepository;
import cn.gov.yrcc.app.module.layer.repository.LayerRepository;
import cn.gov.yrcc.app.module.statistics.service.GSStatisticsService;
import cn.gov.yrcc.app.module.workspace.repository.WorkspaceRepository;
import cn.gov.yrcc.internal.constant.PublishLayerStatusEnum;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GSStatisticsServiceImpl implements GSStatisticsService {

	private final WorkspaceRepository workspaceRepository;
	private final DatastoreRepository datastoreRepository;
	private final LayerRepository layerRepository;

	public GSStatisticsServiceImpl(WorkspaceRepository workspaceRepository, DatastoreRepository datastoreRepository, LayerRepository layerRepository) {
		this.workspaceRepository = workspaceRepository;
		this.datastoreRepository = datastoreRepository;
		this.layerRepository = layerRepository;
	}

	@Override
	public Map<String, Long> statisticsOverview() {
		long workspaceCount = workspaceRepository.count();
		long datastoreCount = datastoreRepository.count();
		long layerCount = layerRepository.count(PublishLayerStatusEnum.success.name());

		return ImmutableMap.of(
			"workspace", workspaceCount,
			"datastore", datastoreCount,
			"layer", layerCount
		);
	}
}
