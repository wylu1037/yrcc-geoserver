package cn.gov.yrcc.internal.properties;

import lombok.Data;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.postgis")
public class PostGisProperties {

	private String dbtype;
	private String host;
	private int port;
	private String database;
	private String schema;
	private String user;
	private String passwd;

	public Map<String, Object> toMap() {
		Map<String, Object> params = new HashMap<>(7);
		params.put(PostgisNGDataStoreFactory.DBTYPE.key, this.dbtype);
		params.put(PostgisNGDataStoreFactory.HOST.key, this.host);
		params.put(PostgisNGDataStoreFactory.PORT.key, this.port);
		params.put(PostgisNGDataStoreFactory.DATABASE.key, this.database);
		params.put(PostgisNGDataStoreFactory.SCHEMA.key, this.schema);
		params.put(PostgisNGDataStoreFactory.USER.key, this.user);
		params.put(PostgisNGDataStoreFactory.PASSWD.key, this.passwd);
		return params;
	}
}
