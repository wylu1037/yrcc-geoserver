package cn.gov.yrcc.internal.geoserver;

import cn.gov.yrcc.internal.constant.DownloadLayerFormatEnum;
import cn.gov.yrcc.internal.geoserver.entity.GSNativeBoundingBox;
import cn.gov.yrcc.internal.geoserver.properties.GSProperties;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class GeoServerURLManager {

    private final GSProperties gsProperties;

    public GeoServerURLManager(GSProperties gsProperties) {
        this.gsProperties = gsProperties;
    }

    public Pair<String, String> getBasicAuth() {
        return Pair.of(gsProperties.getUsername(), gsProperties.getPassword());
    }

    public String getLegendGraphic(String workspace, String layerName) {
        return String.format("%s/%s/wms?service=WMS&version=1.1.1&request=GetLegendGraphic&layer=%s&format=image/png", gsProperties.getUrl(), workspace, layerName);
    }

    public String retrieveDatastore(String workspace, String datastoreName) {
        return String.format("%s/rest/workspaces/%s/datastores/%s", gsProperties.getUrl(), workspace, datastoreName);
    }

    public String createWorkspace() {
        return String.format("%s/rest/workspaces", gsProperties.getUrl());
    }

    public String deleteWorkspace(String workspaceName) {
        return String.format("%s/rest/workspaces/%s", gsProperties.getUrl(), workspaceName);
    }

    public String deleteDatastore(String workspaceName, String datastoreName) {
        return String.format("%s/rest/workspaces/%s/datastores/%s", gsProperties.getUrl(), workspaceName, datastoreName);
    }

	public String downloadTif(String workspace, String layer,GSNativeBoundingBox boundingBox, String srs,
							  DownloadLayerFormatEnum format, Integer width, Integer height) {
		if (format == DownloadLayerFormatEnum.TIF) {
			return String.format("%s/%s/wcs?service=WCS&version=2.0.1&request=GetCoverage&CoverageId=%s:%s&format=%s", gsProperties.getUrl(), workspace, workspace, layer, format.getFormat());
		}
		return String.format("%s/%s/wms?service=WMS&version=1.1.0&request=GetMap" +
				"&layers=%s%%3A%s" +
				"&bbox=%s%%2C%s%%2C%s%%2C%s" +
				"&width=%d&height=%d" +
				"&srs=%s" +
				"&styles=&format=%s",
			gsProperties.getUrl(), workspace, workspace, layer,
			boundingBox.getMinx(),boundingBox.getMiny(), boundingBox.getMaxx(), boundingBox.getMaxy(),
			width, height, srs, format.getFormat());
	}

	public String downloadShp(String workspace, String layer, GSNativeBoundingBox boundingBox, String srs,
							  DownloadLayerFormatEnum format, Integer width, Integer height) {
		if (format == DownloadLayerFormatEnum.SHP) {
			return String.format("%s/%s/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=%s%%3A%s" +
					"&maxFeatures=50&outputFormat=%s",
				gsProperties.getUrl(), workspace, workspace, layer, format.getFormat());
		}
		double ratio = boundingBox.calculate();
		if (width == null || height == null) {
			width = 1000;
			height = (int) (ratio * 1000);
        }
		return String.format("%s/%s/wms?service=WMS&version=1.1.0" +
				"&request=GetMap&layers=%s%%3A%s" +
				"&bbox=%s%%2C%s%%2C%s%%2C%s" +
				"&width=%d&height=%s&srs=%s&styles=&format=%s",
			gsProperties.getUrl(), workspace, workspace, layer,
			boundingBox.getMinx(),boundingBox.getMiny(), boundingBox.getMaxx(), boundingBox.getMaxy(),
			width, height, srs, format.getFormat());
	}
}
