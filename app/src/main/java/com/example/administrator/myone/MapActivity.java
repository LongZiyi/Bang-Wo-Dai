package com.example.administrator.myone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.platform.comapi.map.B;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.LatLng;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.baidu.trace.model.TransportMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity {


    private MapView mapView;
    private BaiduMap mBaiduMap;
    private  LBSTraceClient mTraceClient = null;

    private com.baidu.mapapi.model.LatLng latLng1;
    private com.baidu.mapapi.model.LatLng orderAdderss;
    private com.baidu.mapapi.model.LatLng orderDestination;

    private GeoCoder mSearch;
    private GeoCoder mySearch;

    private BitmapDescriptor orderAddressBitmap;
    private MarkerOptions optionsOrderAddress;
    private BitmapDescriptor orderDestinationBitmap;
    private MarkerOptions optionsOrderDestination;
    private BitmapDescriptor orderAddresserBitmap;

    private RealTimeHandler realTimeHandler = new RealTimeHandler();
    private RealTimeLocRunnable realTimeLocRunnable = null;

    private Button button;
    private Button message;
    private boolean firstLocate = true;
    private boolean firstShow = true;
    private boolean firstZoom =true;
    // 轨迹服务ID
    private long serviceId = 146052;
    // 设备标识
    private String entityName = User.getCurrentUser().getUsername();

    private String objectId;
    private String orderAddresser;
    private String orderRecipient;


    //private LocationClient mLocationClient;

    private List<com.baidu.mapapi.model.LatLng> pointss = new ArrayList<com.baidu.mapapi.model.LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mLocationClient = new LocationClient(getApplicationContext());
        //mLocationClient.registerLocationListener(new MyLocationListener());
        //SDKInitializer.initialize(getApplicationContext());//初始化

        //隐藏状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        setContentView(R.layout.activity_map);


        mapView = (MapView)findViewById(R.id.map);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        //进入聊天界面按钮
        message = (Button)findViewById(R.id.bun_message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this,ChatActivity.class);
                intent.putExtra("objectId",objectId);
                intent.putExtra("orderAddresser",orderAddresser);
                intent.putExtra("orderRecipient",orderRecipient);
                startActivity(intent);
            }
        });

        //确认送达按钮
        button = (Button)findViewById(R.id.bun_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
                dialog.setTitle("");
                dialog.setMessage("是否确认送达？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //确定送达
                        Intent intent = new Intent(MapActivity.this,OrderDetailedActivity.class);
                        intent.putExtra("objectId",objectId);
                        intent.putExtra("orderStatus","已送达");
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //接受传来的参数，获取购物点和送达点的经纬度Latlng
        Intent intent = getIntent();
        String orderaddress = intent.getStringExtra("orderAddress");
        String orderdestination = intent.getStringExtra("orderDestination");
        orderAddresser = intent.getStringExtra("orderAddresser");
        orderRecipient = intent.getStringExtra("orderRecipient");

        objectId = intent.getStringExtra("objectId");
        String city = intent.getStringExtra("city");
        Log.i("自定义","MapActivity中接收到的参数"+orderaddress+","+orderdestination+","+city);


        //将购物点和送达点转换成Latlng
        //第一步，创建地理编码检索实例；
        mSearch = GeoCoder.newInstance();
        //第二步，创建地理编码检索监听者；
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Log.i("自定义", "地理编码没有获取到结果" + geoCodeResult.error + "");
                } else {
                    //获取地理编码结果
                    orderAdderss = new com.baidu.mapapi.model.LatLng(geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
                    //设置地图的购物点的参数
                    orderAddressBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_orderaddress144);
                    optionsOrderAddress = new MarkerOptions();
                    optionsOrderAddress.position(orderAdderss);
                    optionsOrderAddress.icon(orderAddressBitmap);
                    mBaiduMap.addOverlay(optionsOrderAddress);
                    Log.i("自定义", "地址转换成功" + orderAdderss);
                }
            }
        };
        //第三步，设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(listener);
        //第四步，发起地理编码检索；
        mSearch.geocode(new GeoCodeOption()
                .city(city)
                .address( orderaddress)
        );

        //第一步，创建地理编码检索实例；
        mySearch = GeoCoder.newInstance();
        //第二步，创建地理编码检索监听者；
        OnGetGeoCoderResultListener mlistener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Log.i("自定义", "地理编码没有获取到结果" + geoCodeResult.error + "");
                } else {
                    //获取地理编码结果
                    orderDestination = new com.baidu.mapapi.model.LatLng(geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
                    //设置地图的购物点的参数
                    orderDestinationBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_orderdestination144);
                    optionsOrderDestination = new MarkerOptions();
                    optionsOrderDestination.position(orderDestination);
                    optionsOrderDestination.icon(orderDestinationBitmap);
                    mBaiduMap.addOverlay(optionsOrderDestination);
                    Log.i("自定义", "地址转换成功" + orderDestination);
                }
            }
        };
        //第三步，设置地理编码检索监听者；
        mySearch.setOnGetGeoCodeResultListener(mlistener);
        //第四步，发起地理编码检索；
        mySearch.geocode(new GeoCodeOption()
                .city(city)
                .address( orderdestination)
        );
        Log.i("自定义","转换完成");


        //设置地图的发件人的参数
        orderAddresserBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_orderaddresser144);



        //第四步：初始化轨迹服务
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        com.baidu.trace.Trace mTrace = new com.baidu.trace.Trace(serviceId, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
         mTraceClient = new LBSTraceClient(getApplicationContext());


        //第五步：设置定位和回传周期
        // 定位周期(单位:秒)
        int gatherInterval = 5;
        // 打包回传周期(单位:秒)
        int packInterval = 10;
        // 设置定位和打包周期
            mTraceClient.setInterval(gatherInterval, packInterval);


        //第七步：开启轨迹追踪
        // 1. 开启鹰眼服务，启动鹰眼 service
        mTraceClient.startTrace(mTrace, mTraceListener);
        //2. 开启轨迹采集，启动轨迹追踪。至此，正式开启轨迹追踪。
        mTraceClient.startGather(mTraceListener);
        //记录开始采集时间    startTime = System.currentTimeMillis()/1000;



        LocRequest locRequest = new LocRequest(serviceId);
        //时时定位设备当前位置，定位信息不会存储在轨迹服务端，即不会形成轨迹信息,只用于在MapView显示当前位置
        mTraceClient.queryRealTimeLoc(locRequest, entityListener);//这里只会一次定位,多次定位使Handler.postDelayed(Runnable, interval)实现;



        //开始获取纠偏后的位置，并绘制路线
        realTimeLocRunnable = new RealTimeLocRunnable(5);
        realTimeHandler.post(realTimeLocRunnable);


    }


    //Entity监听器(第一次发件人位置的定位)
    private OnEntityListener entityListener = new OnEntityListener() {
        @Override
        public void onReceiveLocation(TraceLocation location) {
            //将回调的当前位置location显示在地图MapView上
            //这里位置点的返回间隔时间为Handler.postDelayed的延时时间
            com.baidu.mapapi.model.LatLng latLng = new com.baidu.mapapi.model.LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions optionsOrderAddresser = new MarkerOptions();
            optionsOrderAddresser.position(latLng);
            optionsOrderAddresser.icon(orderAddresserBitmap);
            mBaiduMap.addOverlay(optionsOrderAddresser);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(18f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    };




    //轨迹监听器(用于接收纠偏后实时位置回调)
    private OnTrackListener trackListener = new OnTrackListener() {
        @Override
        public void onLatestPointCallback(LatestPointResponse response) {
            //将纠偏后实时位置显示在地图MapView上(位置点的返回间隔时间为数据打包上传的频率；数据发送到服务端，才会更新最新的纠偏位置)
            try {
                if(firstShow){
                    firstShow = false;
                    Toast.makeText(MapActivity.this,"起点获取中，请稍后...",Toast.LENGTH_LONG).show();
                }
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    return;
                }
                LatestPoint point = response.getLatestPoint();
                if (null == point || isZeroPoint(point.getLocation().getLatitude(), point.getLocation().getLongitude())) {
                    Log.i("自定义","非洲" + point + "");
                    return;
                }
                com.baidu.mapapi.model.LatLng currentLatLng = convertTrace2Map(point.getLocation());
                if (null == currentLatLng) {
                    return;
                }
                if(firstLocate){
                    firstLocate = false;
                    return;
                }
                if (pointss == null) {
                    return;
                }
                if(firstZoom){
                    firstZoom = false;
                    MarkerOptions optionsOrderAddresser = new MarkerOptions();
                    optionsOrderAddresser.position(pointss.get(0));
                    optionsOrderAddresser.icon(orderAddresserBitmap);
                    mBaiduMap.addOverlay(optionsOrderAddresser);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(pointss.get(0)).zoom(18f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
                pointss.add(currentLatLng);
                Log.i("自定义",pointss.size()+"");

                // 绘制新覆盖物前，清空之前的覆盖物
                mBaiduMap.clear();
                //设置购物点
                mBaiduMap.addOverlay(optionsOrderAddress);
                //设置送达点
                mBaiduMap.addOverlay(optionsOrderDestination);

                OverlayOptions overlayOptions = new PolylineOptions().width(13).color(0xAAFF0000).points(pointss);
                mBaiduMap.addOverlay(overlayOptions);
                Log.i("自定义","快递员的页面，设置路线");

                //设置发件人的位置
                MarkerOptions optionsOrderAddresser = new MarkerOptions();
                optionsOrderAddresser.position(pointss.get(pointss.size()-1));
                optionsOrderAddresser.icon(orderAddresserBitmap);
                mBaiduMap.addOverlay(optionsOrderAddresser);

            } catch (Exception x) {

            }

        }
    };





    class RealTimeLocRunnable implements Runnable {

        private int interval = 0;

        public RealTimeLocRunnable(int interval) {
            this.interval = interval;
        }

        @Override
        public void run() {

            //获取查询纠偏后的点
            LatestPointRequest request = new LatestPointRequest(1, serviceId, entityName);
            ProcessOption processOption = new ProcessOption();//创建纠偏选项实例
            processOption.setNeedDenoise(true);//去噪处理
            processOption.setNeedVacuate(true);// 设置需要抽稀
            processOption.setNeedMapMatch(true);//绑路处理
            processOption.setRadiusThreshold(20);//设置精度过滤，精度大于50米的位置点过滤掉（0为不需要）
            processOption.setTransportMode(TransportMode.walking);// 设置交通方式为走路
            request.setProcessOption(processOption);//设置参数
            mTraceClient.queryLatestPoint(request, trackListener);//请求纠偏后的最新点

            realTimeHandler.postDelayed(this, interval * 1000);
        }
    }
    static class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }



    public static boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }
    public static boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }
    public static com.baidu.mapapi.model.LatLng convertTrace2Map(com.baidu.trace.model.LatLng traceLatLng) {
        return new com.baidu.mapapi.model.LatLng(traceLatLng.latitude, traceLatLng.longitude);
    }


    //第六步：初始化监听器
    // 初始化轨迹服务监听器
    private OnTraceListener mTraceListener = new OnTraceListener() {
        //绑定com.baidu.trace.LBSTraceService服务回调接口
        @Override
        public void onBindServiceCallback(int i, String s) {

        }
        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {

        }
        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {

        }
        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            Log.i("自定义","MapActivity中开始采集快递员的位置点");
        }
        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {

        }
        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
