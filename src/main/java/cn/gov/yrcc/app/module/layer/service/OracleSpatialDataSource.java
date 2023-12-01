package cn.gov.yrcc.app.module.layer.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OracleSpatialDataSource {

	private String dbtype;

	private String host;

	private int port;

	private String database;

	private String user;

	private String passwd;

	private String schema;
}
