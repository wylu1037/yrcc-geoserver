package cn.gov.yrcc.app.module.layer.service.impl;

import cn.gov.yrcc.app.module.layer.service.ShpService;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShpServiceImpl implements ShpService {

	public static CoordinateReferenceSystem coordinateReferenceSystem = null;

	@Override
	public SimpleFeatureSource readFile(File shpFile) {
		SimpleFeatureSource featureSource = null;
		ShapefileDataStore shpDataStore;
		try {
			shpDataStore = new ShapefileDataStore(shpFile.toURI().toURL());
			Charset charset = Charset.forName("GBK");
			shpDataStore.setCharset(charset);
			String tableName = shpDataStore.getTypeNames()[0];
			featureSource = shpDataStore.getFeatureSource(tableName);
		} catch (Exception e) {
			log.error("[ShpServiceImpl] readFile() called with Params: shpFile = {}, Error message = {}",
				shpFile, Throwables.getStackTraceAsString(e));
		}
		return featureSource;
	}

	@Override
	public JDBCDataStore createTable(JDBCDataStore ds, SimpleFeatureSource featureSource, String spatialName) {
		SimpleFeatureType schema = featureSource.getSchema();
		try {
			String[] allTableNames = ds.getTypeNames();

			//如果存在,则先删除
			//这里根据需求决定是否删除表
			if (allTableNames != null && ArrayUtils.contains(allTableNames, schema.getTypeName())) {
				ds.removeSchema(schema.getTypeName());
				//防止shp文件名大写的问题
			} else if (ArrayUtils.contains(allTableNames, schema.getTypeName().toLowerCase())) {
				ds.removeSchema(schema.getTypeName().toLowerCase());
			} else {
				// ignore
			}

			//由于此类属性内部不可变,所以需要获取旧属性,重新赋值给新建属性.
			//获取旧属性
			List<AttributeDescriptor> oldAttributeDescriptors = schema.getAttributeDescriptors();
			//新属性
			List<AttributeDescriptor> newAttributeDescriptors = new ArrayList<>();

			//新建feature构造器
			SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
			//设置表名
			simpleFeatureTypeBuilder.setName(schema.getName().toString().toLowerCase());
			//设置坐标系
			simpleFeatureTypeBuilder.setCRS(schema.getCoordinateReferenceSystem());

			//获取属性
			for (AttributeDescriptor oldAttributeDescriptor : oldAttributeDescriptors) {

				//属性构造器
				AttributeTypeBuilder build = new AttributeTypeBuilder();

				build.init(oldAttributeDescriptor.getType());
				build.setNillable(true);

				//获取字段名,改为小写
				String name = StringUtils.isNotEmpty(oldAttributeDescriptor.getLocalName()) ? oldAttributeDescriptor.getLocalName().toLowerCase() : oldAttributeDescriptor.getLocalName();
				if (oldAttributeDescriptor instanceof GeometryDescriptor) {

					//修改空间字段名
					name = StringUtils.isNotEmpty(spatialName) ? spatialName : "shape";

					GeometryTypeImpl geometryDescriptor = (GeometryTypeImpl) oldAttributeDescriptor.getType();
					//获取坐标系,用于坐标系转换
					coordinateReferenceSystem = geometryDescriptor.getCoordinateReferenceSystem();

				} else {
					// ignore
				}

				//设置字段名
				build.setName(name);

				//创建新的属性类
				AttributeDescriptor descriptor = build.buildDescriptor(name, oldAttributeDescriptor.getType());

				newAttributeDescriptors.add(descriptor);
			}

			//使用新的属性类
			simpleFeatureTypeBuilder.addAll(newAttributeDescriptors);

			//获取新属性值
			schema = simpleFeatureTypeBuilder.buildFeatureType();
			//创建数据表
			ds.createSchema(schema);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("[ShpServiceImpl] createTable() called with Params: ds = {}, featureSource = {}, spatialName = {}, Error message = {}",
				ds, featureSource, spatialName, Throwables.getStackTraceAsString(e));
		}
		return ds;
	}

	@Override
	public void write2db(JDBCDataStore ds, SimpleFeatureSource featureSource) {
		SimpleFeatureType schema = featureSource.getSchema();
		//开始写入数据
		try {
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(schema.getTypeName().toLowerCase(), Transaction.AUTO_COMMIT);
			SimpleFeatureCollection featureCollection = featureSource.getFeatures();
			SimpleFeatureIterator features = featureCollection.features();

			CoordinateReferenceSystem crs = coordinateReferenceSystem;

			//默认转为wgs84
			CoordinateReferenceSystem worldCRS = DefaultGeographicCRS.WGS84;
			boolean lenient = true; // allow for some error due to different datums
			//定义坐标转换
			MathTransform transform = CRS.findMathTransform(crs, worldCRS, lenient);

			while (features.hasNext()) {
				writer.hasNext();
				SimpleFeature next = writer.next();
				SimpleFeature feature = features.next();
				for (int i = 0; i < feature.getAttributeCount(); i++) {
					next.setAttribute(i, feature.getAttribute(i));
				}

				//坐标系转换
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				Geometry geometry2 = JTS.transform(geometry, transform);
				next.setDefaultGeometry(geometry2);

				//写入数据库
				writer.write();
			}
			writer.close();
			ds.dispose();
		} catch (IOException | FactoryException | TransformException e) {
			log.error("[ShpServiceImpl] write2db() called with Params: ds = {}, featureSource = {}, Error message = {}",
				ds, featureSource, Throwables.getStackTraceAsString(e));
		}
	}
}
