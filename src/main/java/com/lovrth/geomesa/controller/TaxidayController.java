package com.lovrth.geomesa.controller;

import com.lovrth.geomesa.config.impl.NycTaxiDataConfig;
import com.lovrth.geomesa.pojo.dto.GeoMesaDTO;
import com.lovrth.geomesa.service.ITaxidayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Author: ChenLei
 * @Description:
 * @Date: Created in 17:46 2018/5/22
 */
@RestController
@CrossOrigin    //解决跨域
@RequestMapping(value = "/taxiday")
@Api("taxiday")
public class TaxidayController {

    private final static Logger logger = LoggerFactory.getLogger(NyctaxiController.class);

    @Autowired
    private ITaxidayService iTaxidayService;

    @Autowired
    private NycTaxiDataConfig nycTaxiDataConfig;

    /**
    * @Description: 添加taxiday数据源
    * @Date 17:49 2018/5/22
    * @Param [catalogName]
    * @return java.lang.String
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
        Boolean status=iTaxidayService.insertTaxidayData(geoMesaDTO.getParams());
        if(status==true){
            return "成功";
        }else{
            return "失败";
        }
    }

    /**
    * @Description: 删除taxiday数据源
    * @Date 17:50 2018/5/22
    * @Param [catalogName]
    * @return java.lang.String
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
        Boolean status=iTaxidayService.deleteTaxidayDatastore(geoMesaDTO.getParams());
        if(status==true){
            return "成功";
        }else {
            return "失败";
        }
    }

    /**
    * @Description: 轨迹路线查询
    * @Date 21:53 2018/5/22
    * @Param [catalogName, medallion]
    * @return java.lang.String
    **/
    @ApiOperation(value="查询数据", notes="查询轨迹路线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "catalogName", value = "Hbase数据源", required = true, dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "medallion", value = "出租车UUID", required = true, dataType = "String",paramType = "path")
    })
    @GetMapping(value = "/query/AttributeQuery/{catalogName}&{medallion}")
    public String attributeQuery(@PathVariable("catalogName")String catalogName,@PathVariable("medallion")String medallion){
        logger.info(catalogName+" 时空查询...");
        GeoMesaDTO geoMesaDTO =new GeoMesaDTO();
        geoMesaDTO.setParams(new HashMap<String, String>(){
            {
                put("hbase.catalog", catalogName);
            }
        });
        geoMesaDTO.setData(nycTaxiDataConfig);
        String TaxidayGeoJson=iTaxidayService.attributeQuery(geoMesaDTO.getParams(), medallion);
        if(TaxidayGeoJson!=null){
            return TaxidayGeoJson;
        }else {
            return "错误";
        }
    }
}
