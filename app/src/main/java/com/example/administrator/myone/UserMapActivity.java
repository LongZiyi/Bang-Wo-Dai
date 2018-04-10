package com.example.administrator.myone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;

import java.util.ArrayList;
import java.util.List;

public class UserMapActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private  LBSTraceClient mTraceClient = null;

    private GeoCoder mSearch;

    private boolean isfirstLocation = true;
    private boolean isfirstloc =true;

    private BitmapDescriptor orderDestinationBitmap;
    private BitmapDescriptor orderAddressBitmap;
    private BitmapDescriptor orderAddresserBitmap;
    private MarkerOptions optionsOrderDestination;
    private MarkerOptions optionsOrderAddress;
    private com.baidu.mapapi.model.LatLng orderDestination;
    private com.baidu.mapapi.model.LatLng orderAdderss;

    private RequestHandler requestHandler = new RequestHandler();
    private RequestRunnable requestRunnable = null;

    // 轨迹服务ID
    private long serviceId = 146052;
    // 设备标识
    private String entityName;

    private String orderAddresser;
    private String orderRecipient;
    private double AddressLatitude;
    private double Addresslongitude;
    private String orderdestination;
    private String city;
    private String objectId;

    private Button button;
    private Button message;

    private List<LatLng> trackPoints = new ArrayList<>();//轨迹点集合

    private long startTime;
    private long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_user_map);


        mapView = (MapView) findViewById(R.id.userMap);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);


        //获取Intent信息
        Intent intent = getIntent();
        orderAddresser = intent.getStringExtra("orderAddresser");
        orderRecipient = intent.getStringExtra("orderRecipient");
        entityName = intent.getStringExtra("orderAddresser");
        AddressLatitude =intent.getDoubleExtra("AddressLatitude",0);
        Addresslongitude =intent.getDoubleExtra("Addresslongitude",0);
        orderdestination =intent.getStringExtra("orderDestination");
        city =intent.getStringExtra("city");
        objectId = intent.getStringExtra("objectId");


        //进入聊天界面按钮
        message = (Button)findViewById(R.id.bun_userMessage);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMapActivity.this,ChatActivity.class);
                intent.putExtra("orderAddresser",orderAddresser);
                intent.putExtra("orderRecipient",orderRecipient);
                startActivity(intent);
            }
        });

        //确认收货按钮事件
        button = (Button)findViewById(R.id.bun_userSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserMapActivity.this);
                dialog.setTitle("");
                dialog.setMessage("是否确认收货？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //确定送达
                        Intent intent = new Intent(UserMapActivity.this,OrderDetailedActivity.class);
                        intent.putExtra("objectId",objectId);
                        intent.putExtra("orderStatus","已收货");
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


        //将  送达点   的地址转换为经纬度，并显示
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
                    Log.i("自定义", "UserMapActivity中,将送达点的地址转换为经纬度失败" + geoCodeResult.error + "");
                } else {
                    //获取地理编码结果
                    orderDestination = new com.baidu.mapapi.model.LatLng(geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
                    //设置地图的购物点的参数
                    orderDestinationBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_orderdestination144);
                    optionsOrderDestination = new MarkerOptions();
                    optionsOrderDestination.position(orderDestination);
                    optionsOrderDestination.icon(orderDestinationBitmap);
                    mBaiduMap.addOverlay(optionsOrderDestination);
                    Log.i("自定义", "UserMapActivity中,已将送达点在地图上显示" + orderDestination);
                }
            }
        };
        //第三步，设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(listener);
        //第四步，发起地理编码检索；
        mSearch.geocode(new GeoCodeOption()
                .city(city)
                .address(orderdestination)
        );

        //显示   购买点
        orderAdderss = new LatLng(AddressLatitude,Addresslongitude);
        orderAddressBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_orderaddress144);
        optionsOrderAddress = new MarkerOptions();
        optionsOrderAddress.position(orderAdderss);
        optionsOrderAddress.icon(orderAddressBitmap);
        mBaiduMap.addOverlay(optionsOrderAddress);

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
        //记录开始采集时间
        startTime = System.currentTimeMillis()/1000;


        //循环询问快递员位置
        requestRunnable = new RequestRunnable();
        requestHandler.post(requestRunnable);
    }





    class RequestRunnable implements Runnable{
        @Override
        public void run() {

            //查询快递员轨迹,历史轨迹请求
            HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest();
            ProcessOption processOption = new ProcessOption();//纠偏选项
            processOption.setNeedDenoise(true);//去噪处理，默认为false，不处理
            processOption.setNeedVacuate(true);//设置抽稀，仅在查询历史轨迹时有效，默认需要false
            processOption.setNeedMapMatch(true);//绑路处理，将点移到路径上，默认不需要false
            processOption.setRadiusThreshold(20);//精度过滤
            processOption.setTransportMode(TransportMode.walking);//交通方式，默认为驾车
            historyTrackRequest.setProcessOption(processOption);//设置参数
            historyTrackRequest.setSupplementMode(SupplementMode.no_supplement);//设置里程补偿方式
            historyTrackRequest.setSortType(SortType.asc);//设置返回结果的排序规则，默认升序排序；升序：集合中index=0代表起始点；降序：集合中index=0代表终点。
            historyTrackRequest.setCoordTypeOutput(CoordType.bd09ll);//设置返回结果的坐标类型，默认为百度经纬度
            historyTrackRequest.setProcessed(true);//设置是否返回纠偏后轨迹，true：打开轨迹纠偏，返回纠偏后轨迹;
            historyTrackRequest.setTag(2);//设置请求标识，用于唯一标记本次请求，在响应结果中会返回该标识           //修改过
            historyTrackRequest.setServiceId(serviceId);//设置轨迹服务id，Trace中的id
            historyTrackRequest.setEntityName(entityName);//Trace中的entityName

            historyTrackRequest.setStartTime(startTime);
            endTime = System.currentTimeMillis()/1000;
            historyTrackRequest.setEndTime(endTime);

            mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);




            requestHandler.postDelayed(this, 5000);

        }
    }

    static class RequestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }



    /**
     * 轨迹监听器（用于接收历史轨迹回调）
     */
    private OnTrackListener mTrackListener = new OnTrackListener() {

        @Override
        public void onHistoryTrackCallback(HistoryTrackResponse response) {
            //如果觉得轨迹点可能过多，可以多次分页查询，详细代码参见源码
            int i = 1 ;
            try {

                int total = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    Log.i("自定义","UserMapActivity中，response错误："+response.getMessage());
                } else if (0 == total) {
                    Log.i("自定义","UserMapActivity中，total = 0");
                } else {
                    List<TrackPoint> points = response.getTrackPoints();//获取轨迹点
                    if (null != points) {
                        for (TrackPoint trackPoint : points) {
                            if (!isZeroPoint(trackPoint.getLocation().getLatitude(), trackPoint.getLocation().getLongitude())) {
                                //自己改的
                                if(isfirstloc){
                                    trackPoints.add(convertTrace2Map(trackPoint.getLocation()));
                                    isfirstloc=false;
                                }else{
                                    if(trackPoint.equals(trackPoints.get(0))){
                                        i = i+1;
                                        Log.i("自定义","第一个点");
                                    }else{
                                        i = i+1;
                                        //将轨迹点转化为地图画图层的LatLng类
                                        trackPoints.add(convertTrace2Map(trackPoint.getLocation()));
                                        Log.i("自定义","第几个点"+i+"latitude="+trackPoint.getLocation().getLatitude()+"longitude="+trackPoint.getLocation().getLongitude()+"");
                                    }
                                }
                            }
                        }
                    }
                    //将轨迹点画在地图上
                    mBaiduMap.clear();
                    //设置购物点
                    mBaiduMap.addOverlay(optionsOrderAddress);
                    //设置送达点
                    mBaiduMap.addOverlay(optionsOrderDestination);

                    OverlayOptions overlayOptions = new PolylineOptions().width(13).color(0xAAFF0000).points(trackPoints);
                    mBaiduMap.addOverlay(overlayOptions);
                    Log.i("自定义","用户的页面，设置路线");

                    //设置发件人的位置
                    MarkerOptions optionsOrderAddresser = new MarkerOptions();
                    optionsOrderAddresser.position(trackPoints.get(trackPoints.size()-1));
                    optionsOrderAddresser.icon(orderAddresserBitmap);
                    mBaiduMap.addOverlay(optionsOrderAddresser);

                    //如果是第一次定位，则缩放地图，改变地图中心点
                    if(isfirstLocation) {
                        isfirstLocation = false;
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(trackPoints.get(trackPoints.size() - 1)).zoom(18f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }
            } catch (Exception e) {

            }
        }
    };

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
