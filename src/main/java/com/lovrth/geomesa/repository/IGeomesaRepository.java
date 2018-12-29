package com.lovrth.geomesa.repository;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description: 公共数据接口
 */
public interface IGeomesaRepository {
    DataStore createDataStore(Map<String, String> params) throws IOException;
    SimpleFeatureType getSimpleFeatureType(IGeoMesaDataConfig data);
    void createSchema(DataStore datastore, SimpleFeatureType sft) throws IOException;
    List<SimpleFeature> getFeatures(IGeoMesaDataConfig data);
    void writeFeatures(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features) throws IOException;
    List<Query> getQueries(IGeoMesaDataConfig data);
    void queryFeatures(DataStore datastore, List<Query> queries) throws IOException;
    void cleanUp(DataStore datastore, String typeName);
}
