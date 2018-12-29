package com.lovrth.geomesa.service;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;

import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
public interface INyctaxiService {
    String spatiotemporalQuery(Map<String, String> params);
    String spatiotemporalQueryWithField(Map<String, String> params);
    String attributeQuery(Map<String,String> params);
    String spatiotemporalAttributeWithField(Map<String,String> params);
    String spatiotemporalParam(Map<String,String> params,  String space, String start_timedate, String end_timedate);
    String spatiotemporalAttributeParam(Map<String,String> params, String space, String start_timedate, String end_timedate, String attribute);
    Boolean insertNyctaxiData(Map<String, String> params);
    Boolean deleteNyctaxiDatastore(Map<String, String> params);
    void queryNyctaxiData(Map<String, String> params);
}
