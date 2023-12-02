package cn.gov.yrcc.internal.geoserver;

import cn.gov.yrcc.internal.properties.PostGisProperties;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import org.springframework.stereotype.Component;

@Component
public class GeoServerBuilder {

	private final PostGisProperties properties;

	public GeoServerBuilder(PostGisProperties properties) {
		this.properties = properties;
	}

	public GSPostGISDatastoreEncoder buildGSPostGISDatastoreEncoder(String storeName) {
		GSPostGISDatastoreEncoder store = new GSPostGISDatastoreEncoder(storeName);
		store.setHost(properties.getHost());
		store.setPort(properties.getPort());
		store.setUser(properties.getUser());
		store.setPassword(properties.getPasswd());
		store.setDatabase(properties.getDatabase());
		store.setSchema(properties.getSchema());
		store.setConnectionTimeout(20);
		store.setMaxConnections(20);
		store.setMinConnections(1);
		store.setExposePrimaryKeys(true);
		return store;
	}
}
