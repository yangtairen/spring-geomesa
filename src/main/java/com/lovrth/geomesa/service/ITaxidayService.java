package com.lovrth.geomesa.service;

import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
public interface ITaxidayService {
    String  attributeQuery(Map<String, String> params, String medallion);
    Boolean insertTaxidayData(Map<String, String> params);
    Boolean deleteTaxidayDatastore(Map<String, String> params);
}
