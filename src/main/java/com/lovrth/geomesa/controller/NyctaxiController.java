package com.lovrth.geomesa.controller;


import com.lovrth.geomesa.config.impl.NycTaxiDataConfig;
import com.lovrth.geomesa.pojo.dto.GeoMesaDTO;
import com.lovrth.geomesa.service.INyctaxiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Author: ChenLei
 * @Description: Nyctaxi控制层
 * @Date: Created in 14:41 2018/5/21
 */

@RestController
@CrossOrigin    //解决跨域
@RequestMapping(value = "/nyctaxi")
@Api("nyctaxi")
public class NyctaxiController {

    private final static Logger logger = LoggerFactory.getLogger(NyctaxiController.class);

    @Autowired
    private INyctaxiService iNyctaxiService;

    @Autowired
    private NycTaxiDataConfig nycTaxiDataConfig;

    /* 测试 */
    @GetMapping(value = "/hello/{id}")
    public String hello(@PathVariable("id")int id,@RequestParam("name")String name,@RequestParam("age")int age){
        logger.info("测试数据");
        return "ID: "+id+"姓名： "+name+",年龄："+age;
    }
    @GetMapping(value = "/hello")
    public String hello1(){
        logger.info("测试数据");
        return "hello";
    }

    /**
    * @Description: 插入Nyctaxi数据
    * @Date 15:05 2018/5/21
    * @Param [catalogName]
    * @return void
    **/
    @ApiOperation(value="写入数据", notes="写入HBase数据源")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/insert/{catalogName}")
    public String insertNyctaxi(@PathVariable("catalogName") String catalogName){
        logger.info(catalogName+" 插入数据...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        Boolean status=iNyctaxiService.insertNyctaxiData(geoMesaDTO.getParams());
        if(status==true){
            return "成功";
        }else{
            return "失败";
        }
    }

    /**
    * @Description: 删除nyctaxi数据
    * @Date 15:15 2018/5/21
    * @Param [catalogName]
    * @return void
    **/
    @ApiOperation(value="删除数据", notes="删除HBase数据源")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/delete/{catalogName}")
    public String deleteNyctaxi(@PathVariable("catalogName")String catalogName){
        logger.info(catalogName+" 删除数据...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        Boolean status=iNyctaxiService.deleteNyctaxiDatastore(geoMesaDTO.getParams());
        if(status==true){
            return "成功";
        }else {
            return "失败";
        }
    }

    /**
    * @Description: 时空查询
    * @Date 9:21 2018/5/22
    * @Param [catalogName]
    * @return java.util.List<com.lovrth.geomesa.entity.Nyctaxi>
    **/
    @ApiOperation(value="时空数据", notes="时空查询")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/query/SpatiotemporalQuery/{catalogName}")
    public String spatiotemporalQuery(@PathVariable("catalogName")String catalogName){
        logger.info(catalogName+" 时空查询...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJson=iNyctaxiService.spatiotemporalQuery(geoMesaDTO.getParams());
        if(nyctaxiGeoJson!=null){
            return nyctaxiGeoJson;
        }else {
            return "错误";
        }
    }

    /**
    * @Description: 时空查询带字段
    * @Date 15:10 2018/5/22
    * @Param [catalogName]
    * @return java.lang.String
    **/
    @ApiOperation(value="时空数据带字段", notes="时空查询带字段")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/query/SpatiotemporalQueryWithField/{catalogName}")
    public String spatiotemporalWithAttribute(@PathVariable("catalogName")String catalogName) {
        logger.info(catalogName+" 时空查询带字段...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJsonWithAttribute=iNyctaxiService.spatiotemporalQueryWithField(geoMesaDTO.getParams());
        if(nyctaxiGeoJsonWithAttribute!=null){
            return nyctaxiGeoJsonWithAttribute;
        }else {
            return "错误";
        }
    }

    /**
    * @Description: 简单属性查询
    * @Date 15:21 2018/5/22
    * @Param [catalogName]
    * @return java.lang.String
    **/
    @ApiOperation(value="简单数据查询", notes="简单数据查询")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/query/AttributeQuery/{catalogName}")
    public String queryAttribute(@PathVariable("catalogName")String catalogName){
        logger.info(catalogName+" 简单属性查询...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJson=iNyctaxiService.attributeQuery(geoMesaDTO.getParams());
        if(nyctaxiGeoJson!=null){
            return nyctaxiGeoJson;
        }else {
            return "错误";
        }
    }

    /*
    * @Description: 时空及属性带字段查询
    * @Date 15:55 2018/5/22
    * @Param [catalogName]
    * @return java.lang.String
    **/
    @ApiOperation(value="时空及属性带字段查询", notes="时空及属性带字段查询")
    @ApiImplicitParam(name = "catalogName", value = "HBase数据源", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/query/SpatiotemporalAttributeWithField/{catalogName}")
    public String spatiotemporalAttributeWithField(@PathVariable("catalogName")String catalogName){
        logger.info(catalogName+" 时空及属性带字段查询...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJson=iNyctaxiService.spatiotemporalAttributeWithField(geoMesaDTO.getParams());
        if(nyctaxiGeoJson!=null){
            return nyctaxiGeoJson;
        }else {
            return "错误";
        }
    }

    /**
    * @description: 时空及属性交互查询
    * @params [catalog, space, time, attribute]
    * @return java.lang.String
    **/
    @ApiOperation(value="时空及属性交互设置", notes="时空及属性交互设置")
    @GetMapping("/query/SpatiotemporalAttributeParam")
    public String spatiotemporalAttributeParam(@RequestParam("catalog")String catalog,@RequestParam("space")String space,
                                     @RequestParam("start_timedate")String start_timedate,@RequestParam("end_timedate")String end_timedate,
                                                   @RequestParam("attribute")String attribute)
    {
        logger.info(catalog+" 时空及属性交互设置...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalog);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJson=iNyctaxiService.spatiotemporalAttributeParam(geoMesaDTO.getParams(),space,
                start_timedate,end_timedate,attribute);
        if(nyctaxiGeoJson!=null){
            return nyctaxiGeoJson;
        }else {
            return "错误";
        }
    }

    /**
     * @description: 时空交互查询
     * @params [catalog, space, time, attribute]
     * @return java.lang.String
     **/
    @ApiOperation(value="时空交互", notes="时空交互")
    @GetMapping("/query/SpatiotemporalParam")
    public String spatiotemporalParam(@RequestParam("catalog")String catalog,@RequestParam("space")String space,
                                               @RequestParam("start_timedate")String start_timedate,@RequestParam("end_timedate")String end_timedate)
    {
        logger.info(catalog+" 时空交互设置...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalog);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String nyctaxiGeoJson=iNyctaxiService.spatiotemporalParam(geoMesaDTO.getParams(),space,
                start_timedate,end_timedate);
        if(nyctaxiGeoJson!=null){
            return nyctaxiGeoJson;
        }else {
            return "错误";
        }
    }

    /**
    * @Description: 测试查询
    * @Date 15:31 2018/5/21
    * @Param [catalogName]
    * @return void
    **/
    @PostMapping(value = "/queryNyctaxi/all/{catalogName}")
    public void queryAllNyctaxi(@PathVariable("catalogName")String catalogName){
        logger.info(catalogName+" 正在查询...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        iNyctaxiService.queryNyctaxiData(geoMesaDTO.getParams());
    }
}
