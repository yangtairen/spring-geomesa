package com.lovrth.geomesa.config.impl;

import com.lovrth.geomesa.config.IGeoMesaDataConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: chenlei
 * @verson: 1.0 2018/9/26
 * @description:
 */
@Component
public class NycTaxiDataConfig implements IGeoMesaDataConfig {
    private static final Logger logger = LoggerFactory.getLogger(NycTaxiDataConfig.class);

    private SimpleFeatureType sft = null;
    private List<SimpleFeature> features = null;
    private List<Query> queries = null;
    private Filter subsetFilter = null;

    /*
     * @Description: 获取TypeName
     * @Date 22:49 2018/5/20
     * @Param []
     * @return java.lang.String
     **/
    @Override
    public  String getTypeName() {
        return "nyctaxi";
    }

    /*
     * @Description: 获取SimpleFeatureType
     * @Date 22:49 2018/5/20
     * @Param []
     * @return org.opengis.feature.simple.SimpleFeatureType
     **/
    @Override
    public SimpleFeatureType getSimpleFeatureType() {
        if(sft==null){
            //构造SimpleFeatureType(schema)类型
            StringBuilder attribute=new StringBuilder();
            attribute.append("medallion:String:index=true,");//index=true标记此属性用于索引
            attribute.append("hack_license:String,");
            attribute.append("vendor_id:String,");
            attribute.append("rate_code:Integer,");
            attribute.append("store_and_fwd_flag:String,");
            attribute.append("pickup_dtg:Date,");
            attribute.append("dropoff_dtg:Date,");
            attribute.append("passenger_count:Integer,");
            attribute.append("trip_time_in_secs:Integer,");
            attribute.append("trip_distance:Double,");
            attribute.append("*geom:MultiPoint:srid=4326");//*表示几何结构，且用于索引

            //使用GeoMesa API的SimpleFeatureTyp方法创建简单的特征类型
            sft= SimpleFeatureTypes.createType(getTypeName(),attribute.toString());

            //指定用于主索引的哪个日期字段
            //1. 如果未指定，则将使用第一日期属性（如果有的话）
            //2. 也可以在属性规范字符串中使用":default=true"
            sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY,"pickup_dtg");
        }
        return sft;
    }

    /*
     * @Description: 读取csv文件，获取features数据集合
     * @Date 22:51 2018/5/20
     * @Param []
     * @return java.util.List<org.opengis.feature.simple.SimpleFeature>
     **/
    @Override
    public List<SimpleFeature> getData() {
        if (features==null){
            //SimpleFeature集合
            List<SimpleFeature> features=new ArrayList<>();

            //读取nyctaxi.csv文件
            URL input = getClass().getClassLoader().getResource("nyctaxi/nyctaxi201301_500k.CSV");
            if(input==null){
                throw new RuntimeException("无法加载nyctaxi.csv文件");
            }

            //CSV格式相对应的日期解析器 美国本土
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

            //根据sft创建SimpleFeatureBuilder
            SimpleFeatureBuilder builder=new SimpleFeatureBuilder(getSimpleFeatureType());

            //使用apache commons-csv解析csv文件
            try(CSVParser parser=CSVParser.parse(input, StandardCharsets.UTF_8, CSVFormat.DEFAULT)){
                for(CSVRecord record:parser){
                    try{
                        //取出对应字段的相应数据
                        builder.set("medallion",record.get(0));
                        builder.set("hack_license",record.get(1));
                        builder.set("vendor_id",record.get(2));
                        builder.set("rate_code",record.get(3));
                        builder.set("store_and_fwd_flag",record.get(4));
                        //1. 日期隐式转换,偏移设置为UTC时区
                        //2. 再解析为Date日期
                        builder.set("pickup_dtg", Date.from(LocalDate.parse(record.get(5),dateTimeFormatter)
                                .atStartOfDay(ZoneOffset.UTC).toInstant()));
                        builder.set("dropoff_dtg", Date.from(LocalDate.parse(record.get(6),dateTimeFormatter)
                                .atStartOfDay(ZoneOffset.UTC).toInstant()));

                        builder.set("passenger_count",record.get(7));
                        builder.set("trip_time_in_secs",record.get(8));
                        builder.set("trip_distance",record.get(9));

                        //使用WKT来表示几何图形,经度(lng)优先排序
                        Double pickup_longitude=Double.parseDouble(record.get(10));
                        Double pickup_latitude=Double.parseDouble(record.get(11));
                        Double dropoff_longitude=Double.parseDouble(record.get(12));
                        Double dropoff_latitude=Double.parseDouble(record.get(13));
                        builder.set("geom", "MULTIPOINT(" + pickup_longitude + " " + pickup_latitude + ","+
                                dropoff_longitude+" "+dropoff_latitude+")");
                        //告诉GeoTools使用自己提供的ID
                        builder.featureUserData(Hints.USE_PROVIDED_FID,Boolean.TRUE);
                        //使用medallion为特征ID(需唯一使用小写UUID)，并构建为feature
                        SimpleFeature feature=builder.buildFeature(UUID.randomUUID().toString().replace("-", "").toLowerCase());

                        features.add(feature);
                    }catch(Exception e){
                        logger.debug("不合法的nyctaxi记录"+e.toString()+" "+record.toString());
                    }
                }
            }catch (IOException e){
                throw  new RuntimeException("错误加载了nyctaxi.csv");
            }
            this.features= Collections.unmodifiableList(features);
        }
        return features;
    }

    /*
     * @Description: 获取queries查询语句集合
     * @Date 22:52 2018/5/20
     * @Param []
     * @return java.util.List<org.geotools.data.Query>
     **/
    @Override
    public List<Query> getQueries() {
        if(queries==null){
            try{
                List<Query> queries=new ArrayList<>();

                //时间和空间范围
                String during="pickup_dtg DURING 2013-01-01T00:00:00.000Z/2018-01-14T00:00:00.000Z";
                String bbox="bbox(geom,-75,40,-73,41.5)";

                //基本时空查询
                queries.add(new Query(getTypeName(), ECQL.toFilter(bbox+" AND "+during)));
                //基本时空查询并投射到属性
                queries.add(new Query(getTypeName(),ECQL.toFilter(bbox+" AND "+during),
                        new String[]{"medallion","pickup_dtg","geom"}));
                //二次索引(index=true)上的查询
                queries.add(new Query(getTypeName(),ECQL.toFilter("medallion='89D227B655E5C82AECF13C3F540D4CF4'")));
                //二次索引查询并投射到属性
                queries.add(new Query(getTypeName(),ECQL.toFilter("passenger_count=4 AND "+during),
                        new String[]{"passenger_count","pickup_dtg","geom"}));

                this.queries=Collections.unmodifiableList(queries);
            }catch (CQLException e){
                throw new RuntimeException("使用查询错误："+e);
            }
        }
        return queries;
    }

    /*
     * @Description: 提供时空查询构造器
     * @Date 22:57 2018/5/20
     * @Param []
     * @return org.opengis.filter.Filter
     **/
    @Override
    public Filter getSubsetFilter() {
        if(subsetFilter==null){
            //获取过滤器构建查询
            FilterFactory2 ff= CommonFactoryFinder.getFilterFactory2();

            //大部分数据来自2013-01-13
            ZonedDateTime dateTime=ZonedDateTime.of(2013,1,13,0,0,0,0,ZoneOffset.UTC);
            Date start= Date.from(dateTime.minusDays(1).toInstant());
            Date end=Date.from(dateTime.plusDays(1).toInstant());

            //date范围
            Filter dateFilter=ff.between(ff.property("pickup_dtg"),ff.literal(start),ff.literal(end));
            //bbox范围
            Filter spatialFilter = ff.bbox("geom",-75,40,-73,41.5,"EPSG:4326");
            //组合时空过滤器
            spatialFilter=ff.and(dateFilter,spatialFilter);
        }
        return subsetFilter;
    }
}
