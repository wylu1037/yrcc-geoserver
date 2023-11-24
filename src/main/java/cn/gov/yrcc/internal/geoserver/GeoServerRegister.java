package cn.gov.yrcc.internal.geoserver;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class GeoServerRegister {

    private final GSProperties gsProperties;

    public GeoServerRegister(GSProperties gsProperties) {
        this.gsProperties = gsProperties;
    }

    @Bean
    public GeoServerRESTPublisher geoServerRESTPublisher() {
        return new GeoServerRESTPublisher(gsProperties.getUrl(), gsProperties.getUsername(), gsProperties.getPassword());
    }

    @Bean
    public GeoServerRESTManager geoServerRESTManager() {
        return new GeoServerRESTManager(buildURL(gsProperties.getUrl()), gsProperties.getUsername(),
                gsProperties.getPassword());
    }

    @Bean
    public GeoServerRESTReader geoServerRESTReader() {
        return new GeoServerRESTReader(buildURL(gsProperties.getUrl()), gsProperties.getUsername(), gsProperties.getPassword());
    }

    private URL buildURL(String url) {
        URL u;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return u;
    }
}
