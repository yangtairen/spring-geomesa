dataUrl="http://192.168.7.236:8080/geoserver/hbase/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=hbase:nyctaxi01&maxFeatures=10000&outputFormat=application%2Fjson";
mapboxgl.accessToken = 'pk.eyJ1IjoibG92cnRoIiwiYSI6ImNqaDhyZ2h5azA0eWozOHFwZm9uemlldHUifQ.i9j7LapAQTxDWVVhd38c5g';
var map = new mapboxgl.Map({
    container: 'mapDiv',
    center: [-73.973529,40.741304],   //lng lat
    zoom: 11,
    style: 'mapbox://styles/mapbox/dark-v9'
});
// map.addControl(new mapboxgl.NavigationControl()); //增加地图缩放和旋转控件
// map.addControl(new mapboxgl.FullscreenControl()); //增加全屏地图
map.addControl(new MapboxLanguage({
    defaultLanguage: 'zh'
}));

//属性查询
function attributeQuery() {
                $("#attribute_button").click(function () {
                    //再次查询先删除第一次查询
                    //mapboxgl.Marker.remove();
                    //获取taxiNumber
                    var taxiNumber = $("#attribute_id").val();
                    //ajax大规模使用标记
                    $.ajax({
                        url: dataUrl + "&cql_filter=TaxiID=" + taxiNumber,
                        cache: false,
                        async: true,
                        success: function (data) {
                            if (data.features.length == 0) {
                                alert("查询出租车ID错误，请重新输入");
                            }
                            data.features.forEach(function (event) {
                                //获取taxiNumber
                                var taxiNumber = $("#attribute_id").val();
                                //创建一个类名为marker的div
                                var el = document.createElement('div');
                                el.className = 'marker';
                                //创建一个标记并设置其坐标，添加到地图中
                                new mapboxgl.Marker(el)
                                    .setLngLat(event.geometry.coordinates)
                                    //mapboxgl.Popup方法声明弹出窗口 offset弹出窗口偏移
                                    .setPopup(new mapboxgl.Popup({offset: 25}).setHTML('<h6>' + "查询的出租车ID为：" + event.properties.TaxiID + '</h6>'))
                                    .addTo(map);
                            });
                        },
                        error: function (data) {
                            console.log("error");
                        }
                    })
                });
    }

//0. 测试用

//1. 海量渲染
$("#visualization").click(function () {
    $.ajax({
        url: dataUrl,
        cache: false,
        async: false,
        type: "get",
        success: function(data1) {
            map.addLayer({
                "id": "taxiPoints",
                "type": "circle",
                "source": {
                    "type": "geojson",
                    "data": data1
                },
                "layout": {
                    // "icon-image": "{icon}-15",
                    // "text-field": "{title}",
                    // "text-font": ["Open Sans Semibold", "Arial Unicode MS Bold"],
                    // "text-offset": [0, 0.6],
                    // "text-anchor": "top"
                },
                "paint": {
                    "circle-radius": 3,
                    //"circle-color": "#007cbf"
                    //数据决定样式(乘客载客状态)
                    "circle-color": [
                        'match', ['get', 'passenger_count'],
                        3, '#e55e5e', //red
                        2,  "#F1F100",//yellow
                        1, '#3bb2d0', //blue
                        /* other */
                        '#ccc'
                    ]
                }
            });

            //点击事件 taxiPoints（ID）
            map.on('click', 'taxiPoints', function(e) {
                new mapboxgl.Popup()
                    .setLngLat(e.lngLat)    //渲染上车点和下车点两个
                    .setHTML('<p>' + '出租车上车时间： ' + e.features[0].properties.pickup_dtg + '</p><p>' + '出租车下车时间： ' + e.features[0].properties.dropoff_dtg + '</p><p>' + '出租车当前乘客: ' + e.features[0].properties.passenger_count +
                        '</p><p>' + '出租车行驶时间： ' + e.features[0].properties.trip_time_in_secs+'</p><p>'+'出租车行驶距离: '+e.features[0].properties.trip_distance)
                    .addTo(map);
            });

            // 当移动到当前点时，修改鼠标的样式
            map.on('mouseenter', 'taxiPoints', function() {
                map.getCanvas().style.cursor = 'pointer';
            });

            // 当离开到当前点时，还原到当前样式
            map.on('mouseleave', 'taxiPoints', function() {
                map.getCanvas().style.cursor = '';
            });
        }
    });
});

//3. 地址匹配
var uadbLoop;
$('#uadbAnlysis').click(function () {
     $('#uadbModal').modal();  //打开显示
    $("#startUadbAnlysis").click(function () {
        $.ajax({
            url: "http://192.168.7.236:8082/uacloud.app/uacloud/ga/uadb/batches/postLivyBatcheTest",
            cache: false,
            async: true,
            type: "get",
            success: function(data) {
                // $.each(data.features,function(i,item){
                // })
                var startJsonDate=JSON.parse(data);
                batcheId =startJsonDate.id;
                console.log(batcheId)
                if(startJsonDate.state=='starting'){
                    if($("#successPNG").length>0){
                        $("#successPNG").remove();
                    }
                    if($("#failPNG").length>0){
                        $("#failPNG").remove();
                    }
                    if($("#loadingGIF").length>0){
                        $("#loadingGIF").remove();
                    }
                    $("#startUadbAnlysis").attr("disabled","disabled");
                    if(typeof($("#checkUadbAnlysis").attr("disabled"))=="undefined"){
                        $("#checkUadbAnlysis").attr("disabled","disabled");
                    }
                    console.log("BatcheId为"+batcheId);
                    $("#checkUadbAnlysis").after("<image src='image/loading.gif' style='margin-left: 5px' id='loadingGIF'/>");
                    uadbLoop=window.setInterval(function(){
                        getUadbBatches(batcheId)
                    },5000);
                }else if(startJsonDate.state=="dead"){
                    console.log("开始失败");
                }else if(startJsonDate.state=="success") {
                    console.log("任务成功，查看结果");
                }else{
                    console.log("未知错误")
                }
            }
        })
    });
    function getUadbBatches(batcheId){
        $.ajax({
            url: "http://192.168.7.236:8082/uacloud.app/uacloud/ga/uadb/batches/getLivyBatcheById/"+batcheId,
            cache: false,
            async: true,
            type: "get",
            success: function(data) {
                var batchJsonData=JSON.parse(data);
                if(batchJsonData.state=="running"||batchJsonData.state=="starting"){
                    console.log("请求");
                } else if(batchJsonData.state=="dead"){
                    console.log("任务失败");
                    window.clearInterval(uadbLoop);
                    $("#loadingGIF").remove();
                    $("#checkUadbAnlysis").after("<image src='image/fail.png' style='margin-left: 5px' id='failPNG'/>");
                    $("#startSparkAnalysis").removeAttr("disabled");
                }
                else if(batchJsonData.state=="success")
                {
                    console.log("任务成功");
                    window.clearInterval(uadbLoop);
                    $("#loadingGIF").remove();
                    $("#checkUadbAnlysis").after("<image src='image/success.png' style='margin-left: 5px' id='successPNG'/>");
                    $("#startUadbAnlysis").removeAttr("disabled");
                    $("#checkUadbAnlysis").removeAttr("disabled");
                }else {
                    console.log("未知错误")
                }
            }
        })
    }
    $("#checkUadbAnlysis").click(function () {
        $("#successPNG").remove();
        $("#failPNG").remove();
        $("#uadbtable").bootstrapTable('refresh',{url: 'http://192.168.7.236:8082/uacloud.app/uacloud/business/uadb/getAllAddressNode'});
        $("#uadbtable").bootstrapTable({
            url: 'http://192.168.7.236:8082/uacloud.app/uacloud/business/uadb/getAllAddressNode',
            method: 'get',
            dataType: "json",
            striped: true,//设置为 true 会有隔行变色效果
            undefinedText: "空",//当数据为 undefined 时显示的字符
            pagination: true, //分页
            // paginationLoop:true,//设置为 true 启用分页条无限循环的功能。
            showToggle: "false",//是否显示 切换试图（table/card）按钮
            showColumns: "true",//是否显示 内容列下拉框
            pageNumber: 1,//如果设置了分页，首页页码
            // showPaginationSwitch:true,//是否显示 数据条数选择框
            pageSize: 5,//如果设置了分页，页面数据条数
            pageList: [5, 10, 15, 20],  //如果设置了分页，设置可供选择的页面数据条数。设置为All 则显示所有记录。
            paginationPreText: '‹',//指定分页条中上一页按钮的图标或文字,这里是<
            paginationNextText: '›',//指定分页条中下一页按钮的图标或文字,这里是>
            // singleSelect: false,//设置True 将禁止多选
            search: true, //显示搜索框
            data_local: "zh-US",//表格汉化
            sidePagination: "client", //客户端处理分页
            queryParams: function (params) {//自定义参数，这里的参数是传给后台的，我这是是分页用的
                return {//这里的params是table提供的
                    cp: params.offset,//从数据库第几条记录开始
                    ps: params.limit//找多少条
                };
            },
            idField: "id",//指定主键列
            columns: [
                {
                    title: '待匹配ID',
                    field: 'matchingaddrguid',
                    align: 'center'
                },
                {
                    title: '匹配级别',
                    field: 'ruleabbr',//可以直接取到属性里面的属性
                    align: 'center'
                },
                {
                    title: '匹配地址',
                    field: 'adtext',
                    align: 'center'
                },
                {
                    title: '匹配ID',
                    field: 'guid',
                    align: 'center'
                }
                // {
                //     title: '操作',
                //     field: 'id',
                //     align: 'center',
                //     formatter: function (value, row, index) {//自定义显示可以写标签
                //         return '<a href="#"  onclick="edit(\'' + row.id + '\')">操作</a> ';
                //     }
                // }
            ]
        });
    });
});

//4. 热力显示
$("#heatmap").click(function () {
    alert("开始热力");
    $.ajax({
        url: "http://localhost:8080/nyctaxi/query/SpatiotemporalQuery/nyctaxi201301_10k",
        cache: false,
        async: true,
        type: "get",
        success: function(data) {
            // 添加数据源
            map.addSource('taxi', {
                "type": "geojson",
                "data": JSON.parse(data)
            });
            //添加热力图层
            map.addLayer({
                "id": "taxi-heat",
                "type": "heatmap",
                "source": "taxi",
                "maxzoom": 0,       //最小等级渲染
                "paint": {
                    // 热力重量->热力颜色
                    "heatmap-weight": [
                        "interpolate",
                        ["linear"],
                        ["get", "trip_distance"],
                        0, 0,
                        25, 1
                    ],
                    //热力强度
                    "heatmap-intensity": [
                        "interpolate",
                        ["linear"],
                        ["zoom"],
                        0, 1,
                        15, 3
                    ],
                    // 热力颜色
                    "heatmap-color": [
                        "interpolate",
                        ["linear"],
                        ["heatmap-density"],
                        0, "rgba(33,102,172,0)",
                        0.2, "rgb(103,169,207)",
                        0.4, "rgb(209,229,240)",
                        0.6, "rgb(253,219,199)",
                        0.8, "rgb(239,138,98)",
                        1, "rgb(178,24,43)"
                    ],
                    // zoom->热力范围
                    "heatmap-radius": [
                        "interpolate",
                        ["linear"],
                        ["zoom"],
                        0, 5,
                        15, 20
                    ],
                    // 透明度
                    "heatmap-opacity": [
                        "interpolate",
                        ["linear"],
                        ["zoom"],
                        7, 0,
                        8, 1    //zoom8之前不透明
                    ]
                }
            }, 'waterway-label');

            //添加点图层
            map.addLayer({
                "id": "taxi-point",
                "type": "circle",
                "source": "taxi",
                "minzoom": 7,
                "paint": {
                    // zoom线性变换半径大小（映射：里程数据范围->圆的半径）
                    "circle-radius": [
                        "interpolate",
                        ["linear"],
                        ["zoom"],
                        7, [
                            "interpolate",
                            ["linear"],
                            ["get", "trip_distance"],
                            0, 4,
                            25,8
                        ],
                        13, [
                            "interpolate",
                            ["linear"],
                            ["get", "trip_distance"],
                            0, 1,
                            25,3
                        ]
                    ],
                    // 数据范围->圆的颜色
                    "circle-color": [
                        "interpolate",
                        ["linear"],
                        ["get", "trip_distance"],
                        0, "rgba(33,102,172,0)",    //里程颜色
                        5, "rgb(103,169,207)",
                        10, "rgb(209,229,240)",
                        15, "rgb(253,219,199)",
                        20, "rgb(239,138,98)",
                        25, "rgb(178,24,43)"
                    ],
                    "circle-stroke-color": "white",     //圆外围线的颜色
                    "circle-stroke-width": 1,
                    // 范围->圆的透明度
                    "circle-opacity": [
                        "interpolate",
                        ["linear"],
                        ["zoom"],
                        7, 0,
                        8, 1
                    ]
                }
            }, 'waterway-label');
        }
    });
});

//5. 轨迹统计
var routeLoop;
$('#routeCount').click(function () {
    $('#routeCountModal').modal();  //打开显示
    $("#startRouteCount").click(function () {
        $.ajax({
            url: "http://192.168.7.236:8082/uacloud.app/uacloud/ga/geomesa/batches/postLivyBatche",
            cache: false,
            async: true,
            type: "get",
            success: function(data) {
                // $.each(data.features,function(i,item){
                // })
                var startJsonDate=JSON.parse(data);
                batcheId =startJsonDate.id;
                console.log(batcheId)
                if(startJsonDate.state=='starting'){
                    if($("#successPNG").length>0){
                        $("#successPNG").remove();
                    }
                    if($("#failPNG").length>0){
                        $("#failPNG").remove();
                    }
                    if($("#loadingGIF").length>0){
                        $("#loadingGIF").remove();
                    }
                    $("#startRouteCount").attr("disabled","disabled");
                    if(typeof($("#checkRouteCount").attr("disabled"))=="undefined"){
                        $("#checkRouteCount").attr("disabled","disabled");
                    }
                    console.log("BatcheId为"+batcheId);
                    $("#checkRouteCount").after("<image src='image/loading.gif' style='margin-left: 5px' id='loadingGIF'/>");
                    routeLoop=window.setInterval(function(){
                        getRouteBatches(batcheId)
                    },5000);
                }else if(startJsonDate.state=="dead"){
                    console.log("开始失败");
                }else if(startJsonDate.state=="success") {
                    console.log("任务成功，查看结果");
                }else{
                    console.log("未知错误")
                }
            }
        })
    });
    function getRouteBatches(batcheId){
        $.ajax({
            url: "http://192.168.7.236:8082/uacloud.app/uacloud/ga/uadb/batches/getLivyBatcheById/"+batcheId,
            cache: false,
            async: true,
            type: "get",
            success: function(data) {
                var batchJsonData=JSON.parse(data);
                if(batchJsonData.state=="running"||batchJsonData.state=="starting"){
                    console.log("请求");
                } else if(batchJsonData.state=="dead"){
                    console.log("任务失败");
                    window.clearInterval(routeLoop);
                    $("#loadingGIF").remove();
                    $("#checkRouteCount").after("<image src='image/fail.png' style='margin-left: 5px' id='failPNG'/>");
                    $("#startRouteCount").removeAttr("disabled");
                }
                else if(batchJsonData.state=="success")
                {
                    console.log("任务成功");
                    window.clearInterval(routeLoop);
                    $("#loadingGIF").remove();
                    $("#checkRouteCount").after("<image src='image/success.png' style='margin-left: 5px' id='successPNG'/>");
                    $("#startRouteCount").removeAttr("disabled");
                    $("#checkRouteCount").removeAttr("disabled");
                }
            }
        })
    }
    $("#checkRouteCount").click(function () {
        $("#successPNG").remove();
        $("#failPNG").remove();
        $("#routeCountTable").bootstrapTable('refresh',{url: 'http://192.168.7.236:8082/uacloud.app/uacloud/business/geomesa/getAllCountNYCTaxi'});
        $("#routeCountTable").bootstrapTable({
            url: 'http://192.168.7.236:8082/uacloud.app/uacloud/business/geomesa/getAllCountNYCTaxi',
            method: 'get',
            dataType: "json",
            striped: true,//设置为 true 会有隔行变色效果
            undefinedText: "空",//当数据为 undefined 时显示的字符
            pagination: true, //分页
            // paginationLoop:true,//设置为 true 启用分页条无限循环的功能。
            showToggle: "false",//是否显示 切换试图（table/card）按钮
            showColumns: "true",//是否显示 内容列下拉框
            pageNumber: 1,//如果设置了分页，首页页码
            // showPaginationSwitch:true,//是否显示 数据条数选择框
            pageSize: 5,//如果设置了分页，页面数据条数
            pageList: [5, 10, 15, 20],  //如果设置了分页，设置可供选择的页面数据条数。设置为All 则显示所有记录。
            paginationPreText: '‹',//指定分页条中上一页按钮的图标或文字,这里是<
            paginationNextText: '›',//指定分页条中下一页按钮的图标或文字,这里是>
            // singleSelect: false,//设置True 将禁止多选
            search: true, //显示搜索框
            data_local: "zh-US",//表格汉化
            sidePagination: "client", //客户端处理分页
            queryParams: function (params) {//自定义参数，这里的参数是传给后台的，我这是是分页用的
                return {//这里的params是table提供的
                    cp: params.offset,//从数据库第几条记录开始
                    ps: params.limit//找多少条
                };
            },
            idField: "pickup_dtg",//指定主键列
            columns: [
                {
                    title: '上车点时间',
                    field: 'pickupDtg',
                    align: 'center'
                },
                {
                    title: '个数',
                    field: 'count',//可以直接取到属性里面的属性
                    align: 'center'
                }
                // {
                //     title: '操作',
                //     field: 'id',
                //     align: 'center',
                //     formatter: function (value, row, index) {//自定义显示可以写标签
                //         return '<a href="#"  onclick="edit(\'' + row.id + '\')">操作</a> ';
                //     }
                // }
            ]
        });
    });
});

//6. 清除
$("#clearAll").click(function () {
    //清除海量渲染点
    if (map.getLayer("taxiPoints")) {
        map.removeLayer("taxiPoints");
        map.removeSource("taxiPoints")
    }
    //清除热力图层
    if(map.getLayer("taxi-point")){
        map.removeLayer("taxi-point");
        map.removeLayer("taxi-heat");
        map.removeSource("taxi");
    }
});

