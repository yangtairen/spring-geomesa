package com.lovrth.geomesa.service;

import com.lovrth.geomesa.config.impl.TaxiDayDataConfig;
import com.lovrth.geomesa.pojo.dto.GeoMesaDTO;
import com.lovrth.geomesa.service.impl.TaxidayServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/30
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ITaxidayServiceTest {

    @Autowired
    private ITaxidayService iTaxidayService;

    @Autowired
    private TaxiDayDataConfig taxiDayDataConfig;

    @Test
    public void insertTaxidayData() {
        GeoMesaDTO config = new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "taxiday01");
            }
        });
        config.setData(taxiDayDataConfig);
        iTaxidayService.insertTaxidayData(config.getParams());
    }

    @Test
    public void deleteTaxidayDatastore() {
        GeoMesaDTO config = new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog","taxiday01");
            }
        });
        config.setData(taxiDayDataConfig);
        iTaxidayService.deleteTaxidayDatastore(config.getParams());
    }

    @Test
    public void attributeQuery() {
        GeoMesaDTO config = new GeoMesaDTO();
        config.setParams(new HashMap<String,String>(){
            {
                put("hbase.catalog","taxiday01");
            }
        });
        config.setData(taxiDayDataConfig);
        iTaxidayService.attributeQuery(config.getParams(),"21B98CAC5B31414B9446D381D38EEC7F");
    }
}