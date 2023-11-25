package cn.gov.yrcc.internal;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GeoServer")
                        .description("GeoServer API文档")
                        .version("v1.0.0")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/license/mit/")
                        )
                );
    }
}
