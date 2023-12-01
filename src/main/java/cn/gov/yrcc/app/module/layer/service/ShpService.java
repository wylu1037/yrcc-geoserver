package cn.gov.yrcc.app.module.layer.service;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.jdbc.JDBCDataStore;

import java.io.File;

public interface ShpService {

	SimpleFeatureSource readFile(File shpFile);

	JDBCDataStore createTable(JDBCDataStore ds, SimpleFeatureSource featureSource, String spatialName);

	void write2db(JDBCDataStore ds, SimpleFeatureSource featureSource);
}
