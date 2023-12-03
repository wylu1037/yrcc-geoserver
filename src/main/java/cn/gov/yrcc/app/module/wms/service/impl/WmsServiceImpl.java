package cn.gov.yrcc.app.module.wms.service.impl;

import cn.gov.yrcc.app.database.schema.Layer;
import cn.gov.yrcc.app.module.coverage.response.CoverageResponse;
import cn.gov.yrcc.app.module.coverage.service.CoverageService;
import cn.gov.yrcc.app.module.featuretype.response.FeatureTypeResponse;
import cn.gov.yrcc.app.module.featuretype.service.FeatureTypeService;
import cn.gov.yrcc.app.module.layer.repository.LayerRepository;
import cn.gov.yrcc.app.module.wms.request.DownloadLayerRequest;
import cn.gov.yrcc.app.module.wms.service.WmsService;
import cn.gov.yrcc.internal.constant.DownloadLayerFormatEnum;
import cn.gov.yrcc.internal.constant.LayerTypeEnum;
import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.error.GSErrorMessage;
import cn.gov.yrcc.internal.geoserver.GeoServerURLManager;
import cn.gov.yrcc.utils.file.FileUtils;
import cn.gov.yrcc.utils.http.HttpUtils;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class WmsServiceImpl implements WmsService {

	private final HttpUtils httpUtils;
	private final GeoServerURLManager geoServerURLManager;
	private final CoverageService coverageService;
	private final FeatureTypeService featureTypeService;
	private final LayerRepository layerRepository;

	public WmsServiceImpl(HttpUtils httpUtils, GeoServerURLManager geoServerURLManager, CoverageService coverageService, FeatureTypeService featureTypeService, LayerRepository layerRepository) {
		this.httpUtils = httpUtils;
		this.geoServerURLManager = geoServerURLManager;
		this.coverageService = coverageService;
		this.featureTypeService = featureTypeService;
		this.layerRepository = layerRepository;
	}

	@Override
	public Object getLegendGraphic(String workspace, String layerName) {
		String response = httpUtils.get(geoServerURLManager.getLegendGraphic(workspace, layerName), geoServerURLManager.getBasicAuth());
		return Base64.getEncoder().encodeToString(response.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public byte[] getMap(HttpServletResponse response, DownloadLayerRequest request) {
		Layer layer = layerRepository.findByNameAndWorkspace(request.getLayerName(), request.getWorkspaceName());
		if (layer == null) {
			throw new BusinessException(GSErrorMessage.Layer.NOT_EXISTS);
		}
		if (!layer.getEnable()) {
			throw new BusinessException(GSErrorMessage.Layer.DISABLED);
		}

		String downloadUrl;
		DownloadLayerFormatEnum format = request.getFormat();
		String fileName = request.getFileName() + format.getSuffix();
		if (layer.getType().equals(LayerTypeEnum.RASTER.name())) {
			CoverageResponse coverage = coverageService.details(request.getWorkspaceName(), request.getLayerName());
			downloadUrl = geoServerURLManager.downloadTif(request.getWorkspaceName(), request.getLayerName(),
				coverage.getCoverage().getNativeBoundingBox(), coverage.getCoverage().getSrs(),
				format, request.getWidth(), request.getHeight());
		} else {
			FeatureTypeResponse feature = featureTypeService.details(request.getWorkspaceName(), request.getLayerName());
			downloadUrl = geoServerURLManager.downloadShp(request.getWorkspaceName(), request.getLayerName(),
				feature.getFeatureType().getNativeBoundingBox(), feature.getFeatureType().getSrs(), format,
				request.getWidth(), request.getHeight());
		}
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			int code = httpConn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				InputStream stream = httpConn.getInputStream();
				byte[] bytes = ByteStreams.toByteArray(stream);
				httpConn.disconnect();
				return bytes;
			} else {
				throw new BusinessException(String.format("下载文件失败：%d", code));
			}
		} catch (Exception e) {
			log.error("[WmsServiceImpl] getMap() called with Params: response = {}, request = {}, Error message = {}",
				response, request, Throwables.getStackTraceAsString(e));
			throw new BusinessException("下载文件异常");
		}
	}

	@Override
	public void getMap2(HttpServletResponse response, DownloadLayerRequest request) {
		Layer layer = layerRepository.findByNameAndWorkspace(request.getLayerName(), request.getWorkspaceName());
		if (layer == null) {
			throw new BusinessException(GSErrorMessage.Layer.NOT_EXISTS);
		}
		if (!layer.getEnable()) {
			throw new BusinessException(GSErrorMessage.Layer.DISABLED);
		}

		String downloadUrl;
		DownloadLayerFormatEnum format = request.getFormat();
		String fileName = request.getFileName() + format.getSuffix();
		if (layer.getType().equals(LayerTypeEnum.RASTER.name())) {
			CoverageResponse coverage = coverageService.details(request.getWorkspaceName(), request.getLayerName());
			downloadUrl = geoServerURLManager.downloadTif(request.getWorkspaceName(), request.getLayerName(),
				coverage.getCoverage().getNativeBoundingBox(), coverage.getCoverage().getSrs(),
				format, request.getWidth(), request.getHeight());
		} else {
			FeatureTypeResponse feature = featureTypeService.details(request.getWorkspaceName(), request.getLayerName());
			downloadUrl = geoServerURLManager.downloadShp(request.getWorkspaceName(), request.getLayerName(),
				feature.getFeatureType().getNativeBoundingBox(), feature.getFeatureType().getSrs(), format,
				request.getWidth(), request.getHeight());
		}
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			int code = httpConn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				InputStream stream = httpConn.getInputStream();
				FileUtils.download(response, stream, fileName);
			} else {
				throw new BusinessException(String.format("下载文件失败：%d", code));
			}
			httpConn.disconnect();
		} catch (Exception e) {
			log.error("[WmsServiceImpl] getMap() called with Params: response = {}, request = {}, Error message = {}",
				response, request, Throwables.getStackTraceAsString(e));
		}
	}
}
