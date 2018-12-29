package com.lovrth.geomesa.repository.impl;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import com.lovrth.geomesa.repository.IGeomesaRepository;
import org.geotools.data.*;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.Hints;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.index.geotools.GeoMesaDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
@Repository
public class GeomesaRepository implements IGeomesaRepository{
    /**
     * @Description: 获取datastore
     * @Date 23:44 2018/5/20
     * @Param [params]
     * @return org.geotools.data.DataStore
     **/
    @Override
    public  DataStore createDataStore(Map<String, String> params) throws IOException {
        System.out.println(params);
        // 通过参数，获得数据存储
        DataStore datastore = DataStoreFinder.getDataStore(params);
        if (datastore == null) {
            throw new RuntimeException("这些参数获得不了数据库储存Instance");
        }
        System.out.println();
        return datastore;
    }

    /**
     * @Description: 获取SimpleFeatureType
     * @Date 23:47 2018/5/20
     * @Param [data]
     * @return org.opengis.feature.simple.SimpleFeatureType
     **/
    @Override
    public  SimpleFeatureType getSimpleFeatureType(IGeoMesaDataConfig data) {
        return data.getSimpleFeatureType();
    }

    /**
     * @Description: datastore添加SimpleFeatureType
     * @Date 23:49 2018/5/20
     * @Param [datastore, sft]
     * @return void
     **/
    @Override
    public  void createSchema(DataStore datastore, SimpleFeatureType sft) throws IOException {
        System.out.println("正在创建数据结构: " + DataUtilities.encodeType(sft));
        // 数据源添加Sft结构
        datastore.createSchema(sft);
        System.out.println();
    }

    /**
     * @Description: 获取features
     * @Date 23:50 2018/5/20
     * @Param [data]
     * @return java.util.List<org.opengis.feature.simple.SimpleFeature>
     **/
    @Override
    public  List<SimpleFeature> getFeatures(IGeoMesaDataConfig data) {
        System.out.println("获得csv文件转换的数据");
        List<SimpleFeature> features = data.getData();
        System.out.println();
        return features;
    }

    /**
     * @Description: 插入批量数据
     * @Date 23:52 2018/5/20
     * @Param [datastore, sft, features]
     * @return void
     **/
    @Override
    public  void writeFeatures(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features) throws IOException {
        if (features.size() > 0) {
            System.out.println("正在插入数据");
            // 使用资源尝试确保写入器关闭
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT)) {
                for (SimpleFeature feature : features) {

                    SimpleFeature toWrite = writer.next();

                    toWrite.setAttributes(feature.getAttributes());

                    ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
                    toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);

                    toWrite.getUserData().putAll(feature.getUserData());

                    writer.write();
                }
            }
            System.out.println("插入了 " + features.size() + " 数据");
            System.out.println();
        }
    }

    /**
     * @Description: 获取Queries信息
     * @Date 23:53 2018/5/20
     * @Param [data]
     * @return java.util.List<org.geotools.data.Query>
     **/
    @Override
    public  List<Query> getQueries(IGeoMesaDataConfig data) {
        return data.getQueries();
    }

    /**
     * @Description: 执行批量查询
     * @Date 23:55 2018/5/20
     * @Param [datastore, queries]
     * @return void
     **/
    @Override
    public  void queryFeatures(DataStore datastore, List<Query> queries) throws IOException {
        for (Query query : queries) {
            System.out.println("正在查询： " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                System.out.println("返回属性： " + Arrays.asList(query.getPropertyNames()));
            }
            if (query.getSortBy() != null) {
                SortBy sort = query.getSortBy()[0];
                System.out.println("排序： " + sort.getPropertyName() + " " + sort.getSortOrder());
            }

            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    if (n++ < 10) {
                        System.out.println(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    } else if (n == 10) {
                        System.out.println("...");
                    }
                }
                System.out.println();
                System.out.println("返回 " + n + " 条数据");
                System.out.println();
            }
        }
    }

    /**
     * @Description: 删除数据源
     * @Date 11:40 2018/5/21
     * @Param [datastore, typeName]
     * @return void
     **/
    @Override
    public  void cleanUp(DataStore datastore, String typeName) {
        if (datastore != null) {
            try {
                System.out.println("开始删除数据源");
                //判断datastore是否是GeoMesaDataStore的实例
                if (datastore instanceof GeoMesaDataStore) {
                    ((GeoMesaDataStore) datastore).delete();
                } else {
                    ((SimpleFeatureStore) datastore.getFeatureSource(typeName)).removeFeatures(Filter.INCLUDE);
                    datastore.removeSchema(typeName);
                }
            } catch (Exception e) {
                System.err.println("删除数据源发生错误 " + e.toString());
            } finally {
                datastore.dispose();
            }
        }
    }
}
