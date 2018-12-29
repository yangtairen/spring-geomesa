/* To call the API properly both locally and in production */
var baseUrl = window.location.hostname == "localhost" ?
    "http://localhost:8082" :
    "http://"+window.location.hostname+":8082";

var Layers = {
    openstreetMap: {
        openstreetmap: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
        attrib: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    },
    google: {
        googleChinaMap: 'http://www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}',
        attrib: '&copy; <a href="http://www.google.cn/maps/">Google</a> contributors'
    }
};

var osm = L.tileLayer(Layers.google.googleChinaMap, {maxZoom: 18, attribution: Layers.google.attrib}),
    map = new L.Map('map', {center: new L.LatLng(40.741304, -73.973529), zoom: 13}),
    drawnItems = L.featureGroup().addTo(map);


var baseLayers = {
    '城市图': L.tileLayer(Layers.openstreetMap.openstreetmap, {attribution: Layers.openstreetMap.attrib}),
    "影像图": osm.addTo(map)
};


var customLayers = {"绘制图层": drawnItems};

// , {"绘制图层": drawnItems}, {position: 'topright', collapsed: false}).addTo(map)
map.lc = L.control.layers(baseLayers).addTo(map);
// map.lc = L.control.layers(customLayers).addTo(map);

/**
 * @description 添加绘图控件
 * @author srforever
 * @date 2018-10-02 20:17
 */
map.addControl(new L.Control.Draw({
    edit: {
        featureGroup: drawnItems,
        poly: {
            allowIntersection: false
        }
    },
    draw: {
        polyline: false,
        circle: false,
        marker: false,
        circlemarker:false,
        polygon: {
            allowIntersection: false,
            showArea: true
        }
    }
}));

/**
 * @description 绘制多边形，并获取多边形边界
 * @author srforever
 * @date 2018-10-02 20:18
 */
var arrayst = "";
map.on(L.Draw.Event.CREATED, function (event) {
    if(drawnItems.getLayers().length!=0){
        drawnItems.clearLayers();
        stPointLayer.clearLayers();
        arrayst = "";

        var layer = event.layer;
        drawnItems.addLayer(layer);
        var array = layer.getLatLngs();
        for (var i = 0; i < array.length; i++) {
            for (var j = 0; j < array[i].length; j++) {
                arrayst += array[i][j].lng;
                arrayst += " ";
                arrayst += array[i][j].lat;
                arrayst += ",";
            }
        }
        arrayst += array[0][0].lng;
        arrayst += " ";
        arrayst += array[0][0].lat;
    }else {
        var layer = event.layer;
        drawnItems.addLayer(layer);
        var array = layer.getLatLngs();
        for (var i = 0; i < array.length; i++) {
            for (var j = 0; j < array[i].length; j++) {
                arrayst += array[i][j].lng;
                arrayst += " ";
                arrayst += array[i][j].lat;
                arrayst += ",";
            }
        }
        arrayst += array[0][0].lng;
        arrayst += " ";
        arrayst += array[0][0].lat;
    }
});


/**
 * @description <功能简述>
 * @author srforever
 * @date 2018-10-02 20:21
 */
function showLoad() {

    return layer.msg('正在执行中...', {icon: 16,shade: [0.5, '#f5f5f5'],scrollbar: false,offset: 'auto', time:100000});

}

function closeLoad(index) {
    layer.close(index);
}

function showSuccess() {
    layer.msg('执行成功！',{time: 2000,offset: 'auto'});
}

var start_timedate = "2013-01-01 00:00:00";
var end_timedate = "2013-01-14 00:00:00";
layui.use(['layer', 'laydate'], function () {
    var layer = layui.layer;
    var laydate = layui.laydate;

    //时间选取
    $('#timedate').on('click', function () {
        layer.open({
            // id:'timedate',
            type: 1,
            title: '时空查询',
            skin: 'layui-layer-rim',
            area: ['420px', 'auto'],

            content: '<form class="layui-form" action="" style="margin-top: 30px;margin-left: 30px">\n' +
            '        <div class="layui-form-item">\n' +
            '            <div class="layui-inline">\n' +
            '                <label class="layui-form-label">开始时间</label>\n' +
            '                <div class="layui-input-inline">\n' +
            '                    <input type="text" class="layui-input" id="start_datetime">\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '\n' +
            '        <div class="layui-form-item">\n' +
            '            <div class="layui-inline">\n' +
            '                <label class="layui-form-label">结束时间</label>\n' +
            '                <div class="layui-input-inline">\n' +
            '                    <input type="text" class="layui-input" id="end_datetime">\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '    </form>'
            ,
            btn: ['保存', '取消'],
            btn1: function (index, layero) {
                i=showLoad();
                if(drawnItems.getLayers().length==0){
                    layer.msg('请绘制区域！',{time: 2000,offset: 'auto'});
                }else{
                    $.ajax({
                        url: baseUrl+"/nyctaxi/query/SpatiotemporalParam?" +
                        "catalog=nyctaxi201301_10k&space=" + arrayst + "&start_timedate=" + start_timedate + "&end_timedate=" + end_timedate,
                        cache: false,
                        async: true,
                        success: function (data) {
                            stJson = JSON.parse(data);

                            // L.geoJSON(stJson).addTo(map);
                            function onEachFeature(feature, layer) {
                                var popupContent = "<p>车辆ID： " +
                                    feature.properties.medallion + "</p>" + "<p>乘客人数： " +
                                    feature.properties.passenger_count + "</p>" + "<p>上车时间： " +
                                    feature.properties.pickup_dtg + "</p>" + "<p>下车时间： " +
                                    feature.properties.dropoff_dtg + "</p>";

                                // if (feature.properties && feature.properties.popupContent) {
                                //     popupContent += feature.properties.popupContent;
                                // }

                                layer.bindPopup(popupContent);

                            }

                            stPointLayer = L.geoJSON(stJson, {

                                style: function (feature) {
                                    return feature.properties && feature.properties.style;
                                },

                                onEachFeature: onEachFeature,

                                pointToLayer: function (feature, latlng) {
                                    return L.circleMarker(latlng, {
                                        radius: 4,
                                        fillColor: "#ff7800",
                                        color: "#000",
                                        weight: 1,
                                        opacity: 1,
                                        fillOpacity: 0.8
                                    });
                                }
                            }).addTo(map);
                            console.log(stJson);
                            showSuccess();
                            closeLoad(i);
                            layer.close(index);
                        },
                        error: function (data) {
                            console.log("error");
                        }
                    })
                }
            },
            btn2: function (index, layero) {
                layer.close(index);
            }
        });
    });

    //执行一个laydate实例
    lay('#timedate').on('click', function (e) {
        laydate.render({
            elem: '#start_datetime' //指定元素
            , type: 'datetime'
            , value: start_timedate
            , done: function (value, date, endDate) {
                start_timedate = value;
                console.log(value); //得到日期生成的值，如：2017-08-18
            }
        });

        laydate.render({
            elem: '#end_datetime' //指定元素
            , type: 'datetime'
            , value: end_timedate
            , done: function (value, date, endDate) {
                end_timedate = value;
                console.log(value); //得到日期生成的值，如：2017-08-18
            }
        });
    });
});

/**
 * @description 清除点
 * @author srforever
 * @date 2018-10-02 20:24
 */
$("#clear_point").click(function () {
    drawnItems.clearLayers();
    stPointLayer.clearLayers();
    arrayst = "";
});


