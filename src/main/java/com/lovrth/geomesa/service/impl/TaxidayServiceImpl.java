package com.lovrth.geomesa.service.impl;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import com.lovrth.geomesa.repository.IGeomesaRepository;
import com.lovrth.geomesa.service.ITaxidayService;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
public class TaxidayServiceImpl implements ITaxidayService{

    private static final Logger logger = LoggerFactory.getLogger(TaxidayServiceImpl.class);
    private final IGeomesaRepository geomesaRepository;
    private final IGeoMesaDataConfig iGeoMesaDataConfig;
    private String dataTypeName;

    @Autowired
    public TaxidayServiceImpl(IGeomesaRepository geomesaRepository, @Qualifier("taxiDayDataConfig")IGeoMesaDataConfig iGeoMesaDataConfig){
        this.geomesaRepository=geomesaRepository;
        this.iGeoMesaDataConfig=iGeoMesaDataConfig;
        this.dataTypeName=iGeoMesaDataConfig.getTypeName();
    }

    /**
     * @Description: 获取数据源
     * @Date 9:25 2018/5/28
     * @Param [params, data, medallion]
     * @return java.lang.String
     **/
    @Override
    public String attributeQuery(Map<String, String> params, String medallion) {
        try{
            //获得数据源
            DataStore dataStore= geomesaRepository.createDataStore(params);
            //基本查询语句
            Query query=new Query(dataTypeName, ECQL.toFilter("medallion="+"'"+medallion+"'"
                    +" OR "+"medallion=''"));

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
     * @Description: 总方法：创建数据源并写入数据
     * @Date 17:47 2018/5/21
     * @Param [params, data]
     * @return void
     **/
    @Override
    public Boolean insertTaxidayData(Map<String, String> params) {
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
    public Boolean deleteTaxidayDatastore(Map<String, String> params){
        try{
            DataStore dataStore= geomesaRepository.createDataStore(params);
            geomesaRepository.cleanUp(dataStore,dataTypeName);
            return true;
        }catch (Exception e){
            logger.error("删除数据失败：",e);
            throw new RuntimeException("删除数据失败：",e);
        }
    }
}
