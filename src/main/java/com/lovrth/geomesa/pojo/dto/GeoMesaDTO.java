package com.lovrth.geomesa.pojo.dto;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import lombok.Data;

import java.util.Map;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
//@Data
public class GeoMesaDTO {

    //HBase参数
    private Map<String, String> params;
    //数据
    private IGeoMesaDataConfig data;

    //使用set和get方法
    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public IGeoMesaDataConfig getData() {
        return data;
    }

    public void setData(IGeoMesaDataConfig data) {
        this.data = data;
    }
}
