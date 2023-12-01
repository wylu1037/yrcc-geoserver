package cn.gov.yrcc.app.database;

import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.internal.properties.PostGisProperties;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class PostGisConfig {

	private PostGisProperties properties;

	public PostGisConfig(PostGisProperties properties) {
		this.properties = properties;
	}

	@Bean
	public DataStore dataStore() {
		try {
			return DataStoreFinder.getDataStore(properties.toMap());
		} catch (IOException e) {
			log.error("[PostGisConfig] dataStore() called, Error message = {}", Throwables.getStackTraceAsString(e));
			throw new BusinessException(String.format("连接空间数据库%s异常", properties.getDatabase()));
		}
	}
}
