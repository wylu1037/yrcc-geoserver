package cn.gov.yrcc.internal.geoserver.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "geoserver")
public class GSProperties {

    private String url;

    private String username;

    private String password;
}
