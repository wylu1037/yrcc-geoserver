package cn.gov.yrcc.internal.geoserver;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class GeoServerURLManager {

    private final GSProperties gsProperties;

    public GeoServerURLManager(GSProperties gsProperties) {
        this.gsProperties = gsProperties;
    }

    public String getLegendGraphic(String workspace, String layerName) {
        return String.format("%s/%s/wms?service=WMS&version=1.1.1&request=GetLegendGraphic&layer=%s&format=image/png", gsProperties.getUrl(), workspace, layerName);
    }

    public Pair<String, String> getBasicAuth() {
        return Pair.of(gsProperties.getUsername(), gsProperties.getPassword());
    }
}
