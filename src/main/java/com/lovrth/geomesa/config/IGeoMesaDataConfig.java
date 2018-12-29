package com.lovrth.geomesa.config;

import org.geotools.data.Query;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.util.List;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description: 通用geomesa数据接口
 */
public interface IGeoMesaDataConfig {
    String getTypeName();
    SimpleFeatureType getSimpleFeatureType();
    List<SimpleFeature> getData();
    List<Query> getQueries();
    Filter getSubsetFilter();
}
