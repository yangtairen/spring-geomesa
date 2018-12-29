package com.lovrth.geomesa.service;

import com.lovrth.geomesa.config.impl.NycTaxiDataConfig;
import com.lovrth.geomesa.pojo.dto.GeoMesaDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class INyctaxiServiceTest {

    @Autowired
    private INyctaxiService iNyctaxiService;

    @Autowired
    private NycTaxiDataConfig nycTaxiDataConfig;

    @Test
    public void insertNyctaxiData() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_500k");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.insertNyctaxiData(config.getParams());
    }

    @Test
    public void deleteNyctaxiDatastore() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_1mk");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.deleteNyctaxiDatastore(config.getParams());
    }

    @Test
    public void spatiotemporalQuery() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.spatiotemporalQuery(config.getParams());
    }

    @Test
    public void spatiotemporalQueryWithField() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.spatiotemporalQueryWithField(config.getParams());
    }

    @Test
    public void spatiotemporalAttributeWithField() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.spatiotemporalAttributeWithField(config.getParams());
    }

    @Test
    public void spatiotemporalParam() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        String space = "-73.98231125087479 40.76533145759276,-73.96940231323244 40.76033870593762," +
                "-73.96899032173678 40.735681504432286,-73.98258590488696 40.728084828975135,-74.00867843418382 40.73484903507686," +
                "-74.01046371669509 40.74598245808501,-73.99796676007101 40.75898643496983,-73.98231125087479 40.76533145759276";
        String start_timedate = "2013-01-01 00:00:00";
        String end_timedate = "2013-01-10 00:00:00";
        iNyctaxiService.spatiotemporalParam(config.getParams(),space,start_timedate,end_timedate);
    }

    @Test
    public void spatiotemporalAttributeParam() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        String space = "-73.98231125087479 40.76533145759276,-73.96940231323244 40.76033870593762," +
                "-73.96899032173678 40.735681504432286,-73.98258590488696 40.728084828975135,-74.00867843418382 40.73484903507686," +
                "-74.01046371669509 40.74598245808501,-73.99796676007101 40.75898643496983,-73.98231125087479 40.76533145759276";
        String start_timedate = "2013-01-01 00:00:00";
        String end_timedate = "2013-01-10 00:00:00";
        String attribute = "passenger_count=4";
        iNyctaxiService.spatiotemporalAttributeParam(config.getParams(),space,start_timedate,end_timedate,attribute);
    }

    @Test
    public void queryNyctaxiData() {
        GeoMesaDTO config=new GeoMesaDTO();
        config.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", "nyctaxi201301_10k");
            }
        });
        config.setData(nycTaxiDataConfig);
        iNyctaxiService.queryNyctaxiData(config.getParams());
    }
}