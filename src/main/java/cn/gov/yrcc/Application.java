package cn.gov.yrcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ConfigurationPropertiesScan("cn.gov.yrcc.internal.geoserver")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
