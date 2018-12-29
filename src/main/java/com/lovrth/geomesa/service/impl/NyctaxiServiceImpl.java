package com.lovrth.geomesa.service.impl;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import com.lovrth.geomesa.repository.IGeomesaRepository;
import com.lovrth.geomesa.service.INyctaxiService;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
@Service
public class NyctaxiServiceImpl implements INyctaxiService{

    private static final Logger logger = LoggerFactory.getLogger(NyctaxiServiceImpl.class);
    private final IGeomesaRepository geomesaRepository;
    private final IGeoMesaDataConfig iGeoMesaDataConfig;
    private String dataTypeName;

    @Autowired
    public NyctaxiServiceImpl(IGeomesaRepository geomesaRepository, @Qualifier("nycTaxiDataConfig")IGeoMesaDataConfig iGeoMesaDataConfig){
        this.geomesaRepository=geomesaRepository;
        this.iGeoMesaDataConfig=iGeoMesaDataConfig;
        this.dataTypeName=iGeoMesaDataConfig.getTypeName();
    }

    /**
     * @Description: 时空查询
     * @Date 15:01 2018/5/22
     * @Param [params, data]
     * @return java.lang.String
     **/
    @Override
    public String spatiotemporalQuery(Map<String, String> params) {
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //时间和空间范围
            String during="pickup_dtg DURING 2013-01-01T00:00:00.000Z/2018-01-07T00:00:00.000Z";
            String bbox="bbox(geom,-75,40,-73,41.5)";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter(bbox+" AND "+during));

            logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("基本时空查询错误："+e);
            throw new RuntimeException("基本时空查询错误："+e);
        }
    }

    /**
     * @Description: 时空查询带部分字段
     * @Date 15:07 2018/5/22
     * @Param [params, data]
     * @return java.lang.String
     **/
    @Override
    public String spatiotemporalQueryWithField(Map<String, String> params) {
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //时间和空间范围
            String during="pickup_dtg DURING 2013-01-01T00:00:00.000Z/2018-01-14T00:00:00.000Z";
            String bbox="bbox(geom,-75,40,-73,41.5)";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter(bbox+" AND "+during),
                    new String[]{"medallion","pickup_dtg","geom"});

            logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("时空查询带部分字段错误："+e);
            throw new RuntimeException("时空查询带部分字段错误："+e);
        }
    }

    /**
     * @Description: 简单属性查询
     * @Date 15:18 2018/5/22
     * @Param [params, data]
     * @return java.lang.String
     **/
    @Override
    public String attributeQuery(Map<String,String> params){
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //时间和空间范围
            String during="pickup_dtg DURING 2013-01-01T00:00:00.000Z/2013-01-14T00:00:00.000Z";
            String bbox="bbox(geom,-75,40,-73,41.5)";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter("medallion='89D227B655E5C82AECF13C3F540D4CF4'"));

           logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("属性查询错误："+e);
            throw new RuntimeException("属性查询错误："+e);
        }
    }

    /**
     * @Description: 时空及属性查询带字段
     * @Date 15:35 2018/5/22
     * @Param [params, data]
     * @return java.lang.String
     **/
    @Override
    public String spatiotemporalAttributeWithField(Map<String,String> params){
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //时间和空间范围
            String during="pickup_dtg DURING 2013-01-01T00:00:00.000Z/2018-01-14T00:00:00.000Z";
            String bbox="INTERSECTS(geom,POLYGON((-74.04510498046875 40.78054143186033,-73.8995361328125 40.687407052121316,-74.0478515625 40.635840993386466," +
                    "-74.13299560546875 40.64157252400389,-74.14947509765625 40.703545803451426,-74.11994934082031 40.75037808986467,-74.04510498046875 40.78054143186033)))";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter("passenger_count=4"+" AND "+during+
                    " AND "+bbox),new String[]{"passenger_count","pickup_dtg","geom"});

            logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("时空属性查询带部分字段错误："+e);
            throw new RuntimeException("时空属性查询带部分字段错误："+e);
        }
    }

    /**
     * @description: 时空交互
     * @params [params, data, space, time, attribute]
     * @return java.lang.String
     **/
    @Override
    public String spatiotemporalParam(Map<String,String> params, String space,
                                      String start_timedate, String end_timedate){
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //截取时间和日期
            String start_arrays[]=start_timedate.split(" ");
            String start_date=start_arrays[0];
            String start_time=start_arrays[1];
            String end_arrays[]=end_timedate.split(" ");
            String end_date=end_arrays[0];
            String end_time=end_arrays[1];
            //时间和空间范围
            String during="pickup_dtg DURING "+start_date+"T"+start_time+"Z"+"/"+end_date+"T"+end_time+"Z";
            String bbox="WITHIN(geom,POLYGON(("+space+")))";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter(during+
                    " AND "+bbox));

            logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("时空交互："+e);
            throw new RuntimeException("时空交互："+e);
        }
    }

    /**
     * @description: 时空及属性交互
     * @params [params, data, space, time, attribute]
     * @return java.lang.String
     **/
    @Override
    public String spatiotemporalAttributeParam(Map<String,String> params, String space,
                                               String start_timedate, String end_timedate, String attribute){
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //截取时间和日期
            String start_arrays[]=start_timedate.split(" ");
            String start_date=start_arrays[0];
            String start_time=start_arrays[1];
            String end_arrays[]=end_timedate.split(" ");
            String end_date=end_arrays[0];
            String end_time=end_arrays[1];
            //时间和空间范围
            String during="pickup_dtg DURING "+start_date+"T"+start_time+"Z"+"/"+end_date+"T"+end_time+"Z";
            String bbox="bbox(geom,"+space+")";
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter(attribute+" AND "+during+
                    " AND "+bbox));

            logger.info("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            //获取FeatureSource
            SimpleFeatureSource featureSource=dataStore.getFeatureSource(dataTypeName);
            //获取featureCollection
            SimpleFeatureCollection featureCollection=featureSource.getFeatures(query);
            FeatureJSON fjson = new FeatureJSON();
            StringWriter writer = new StringWriter();
            logger.info("正在写出GeoJson数据： ");
            //写出成GeoJson格式
            fjson.writeFeatureCollection(featureCollection, writer);
            String json = writer.toString();
            return json;
        }catch (Exception e){
            logger.error("时空及属性查询交互查询："+e);
            throw new RuntimeException("时空及属性查询交互查询："+e);
        }
    }

    /**
     * @Description: 总方法：创建数据源并写入数据
     * @Date 17:47 2018/5/21
     * @Param [params, data]
     * @return void
     **/
    @Override
    public  Boolean insertNyctaxiData(Map<String, String> params) {
        try{
            DataStore dataStore = geomesaRepository.createDataStore(params);  //创建数据源
            SimpleFeatureType sft = geomesaRepository.getSimpleFeatureType(iGeoMesaDataConfig); //获取SimpleFeatureType
            geomesaRepository.createSchema(dataStore, sft);       //datastore创建SimpleFeatureType
            List<SimpleFeature> features = geomesaRepository.getFeatures(iGeoMesaDataConfig);   //获取features
            geomesaRepository.writeFeatures(dataStore, sft, features);    //插入批量数据
            return true;
        }catch (Exception e){
            logger.error("插入数据失败：", e);
            throw new RuntimeException("插入数据失败：", e);
        }
    }

    /*
     * @Description: 总方法：删除数据源
     * @Date 17:47 2018/5/21
     * @Param [params, data]
     * @return void
     **/
    @Override
    public Boolean deleteNyctaxiDatastore(Map<String, String> params){
        try{
            DataStore dataStore= geomesaRepository.createDataStore(params);
            geomesaRepository.cleanUp(dataStore,dataTypeName);
            return true;
        }catch (Exception e){
            logger.error("删除数据失败：",e);
            throw new RuntimeException("删除数据失败：",e);
        }
    }

    /**
     * @Description: 总方法：检索数据
     * @Date 22:11 2018/5/21
     * @Param [params, data]
     * @return void
     **/
    @Override
    public void queryNyctaxiData(Map<String, String> params)  {
        try{
            DataStore dataStore = geomesaRepository.createDataStore(params);  //查询数据源
            List<Query> queries = geomesaRepository.getQueries(iGeoMesaDataConfig); //获取queries
            geomesaRepository.queryFeatures(dataStore, queries);  //批量查询
        }catch (Exception e){
            logger.error("检索数据失败",e);
            throw new RuntimeException("检索数据失败",e);
        }
    }

}
