package com.example.administrator.myone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

public class DelayActivity extends AppCompatActivity {

    public LocationClient mLocationClient;

    private GeoCoder mSearch;
    private Toolbar mtoolbar;

    private double latitude;
    private double longitude;

    private TextView goodsName;
    private TextView goodsAddress;
    private TextView goodsprice;
    private TextView goodsDestination;
    private TextView goodsRemark;

    private String orderName;
    private String orderTime;
    private String userName;

    private String installationId;

    private JSONObject jsonObject = null;

    private SearchRunnable searchRunnable = null;
    private SearchHandler searchHandler = new SearchHandler();

    private String objectId;
    private String orderAddresser;
    private String orderDestination;
    private String city;
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
        setContentView(R.layout.activity_delay);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        // 初始化BmobSDK
        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);




        //获取发布订单页面传来的订单信息
        Intent intent = getIntent();
        orderName = intent.getStringExtra("orderName");
        String orderPrice = intent.getStringExtra("orderPrice");
        String orderAddress = intent.getStringExtra("orderAddress");
        orderDestination = intent.getStringExtra("orderDestination");
        String orderRemark = intent.getStringExtra("orderRemark");
        city = intent.getStringExtra("city");
        userName = User.getCurrentUser().getUsername();

        //实例化控件
        goodsName = (TextView) findViewById(R.id.tv_orderName1);
        goodsAddress = (TextView) findViewById(R.id.tv_orderAddress1);
        goodsprice = (TextView) findViewById(R.id.tv_orderPrice1);
        goodsDestination = (TextView) findViewById(R.id.tv_orderDestination1);
        goodsRemark = (TextView) findViewById(R.id.tv_orderRemark1);


        goodsName.setText(orderName);
        goodsAddress.setText(orderAddress);
        goodsprice.setText(orderPrice);
        goodsDestination.setText(orderDestination);
        goodsRemark.setText(orderRemark);

        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        orderTime    =    formatter.format(curDate);


        //上传订单信息到服务器
        Order order = new Order();
        order.setOrderName(orderName);
        order.setOrderPrice(orderPrice);
        order.setOrderAddress(orderAddress);
        order.setOrderDestination(orderDestination);
        order.setOrderRemark(orderRemark);
        order.setOrderRecipient(userName);
        order.setOrderTime(orderTime);
        order.setCity(city);
        order.save(new SaveListener<String>() {
                       @Override
                       public void done(String s, BmobException e) {
                           if(e == null){
                               Log.i("自定义","DelayActivity中，上传订单信息到服务器，成功");
                               searchObjectId();
                           }else{
                               Log.i("自定义","DelayActivity中，上传订单信息到服务器，失败"+e.getMessage()+e.getErrorCode());
                           }
                       }
                   });


                //构建订单信息
                String pushJsonStr = "{\"orderName\":\"" + orderName + "\",\"orderPrice\":\"" + orderPrice + "\",\"orderAddress\":\"" + orderAddress + "\",\"orderDestination\":\"" + orderDestination + "\",\"orderRemark\":\"" + orderRemark + "\",\"city\":\"" + city + "\",\"userName\":\"" + userName + "\",\"orderTime\":\"" + orderTime + "\"}";
        try {
            jsonObject = new JSONObject(pushJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("自定义","DelayAcitvity中,JSONObject出错");
        }

        //第一步，创建地理编码检索实例；
        mSearch = GeoCoder.newInstance();
        //第二步，创建地理编码检索监听者；
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            }
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if ( geoCodeResult == null ||  geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Log.i("自定义", "DelayActivity中，地理编码没有获取到结果"+geoCodeResult.error+"");
                }else{

                    //获取地理编码结果
                    Log.i("自定义","地理编码纬度latitude为："+geoCodeResult.getLocation().latitude+"");
                    Log.i("自定义","地理编码经度longitude为:"+geoCodeResult.getLocation().longitude+"");
                    latitude = geoCodeResult.getLocation().latitude;
                    longitude = geoCodeResult.getLocation().longitude;
                    //推送订单给最靠近的5个快递员
                    pushOrder();
                }
            }
        };
        //第三步，设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(listener);
        //第四步，发起地理编码检索；
        mSearch.geocode(new GeoCodeOption()
                .city(city)
                .address(orderAddress)
        );


    }


    class SearchRunnable implements Runnable {

        @Override
        public void run() {
            //若objectId 不为空，则根据objectId 查找成功接单的人
            if (objectId == null) {
                return;
            }else {
                BmobQuery<Order> query = new BmobQuery<Order>();
                query.getObject(objectId, new QueryListener<Order>() {
                    @Override
                    public void done(Order order, BmobException e) {
                        if (e == null) {
                            orderAddresser = order.getOrderAddresser();
                            Log.i("自定义", "查询是谁接单成功，接单人是：" + order.getOrderAddresser());
                            if(orderAddresser != null){
                                //如果查找到成功接单的人，则进入用户的地图界面
                                searchHandler.removeCallbacks(searchRunnable);
                                Intent intent = new Intent(DelayActivity.this,UserMapActivity.class);
                                intent.putExtra("orderAddresser",orderAddresser);
                                intent.putExtra("orderRecipient",userName);
                                intent.putExtra("AddressLatitude",latitude);
                                intent.putExtra("Addresslongitude",longitude);
                                intent.putExtra("orderDestination",orderDestination);
                                intent.putExtra("city",city);
                                intent.putExtra("objectId",objectId);
                                startActivity(intent);
                            }
                        } else {
                            Log.i("自定义", "查询是谁接单失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
            searchHandler.postDelayed(this, 5000);
        }
    }

    static class SearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    //推送订单给最靠近的5个快递员
    private void pushOrder() {
        //使用消息推送前，首先需要保存设备信息。
        BmobInstallation.getCurrentInstallation().save();
        BmobQuery<MyBmobInstallation> bmobQuery = new BmobQuery<MyBmobInstallation>();
        bmobQuery.addWhereNear("location", new BmobGeoPoint(longitude,latitude));
        bmobQuery.setLimit(5);    //获取最接近5个快递员
        Log.i("自定义","获取最接近5个快递员");
        bmobQuery.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> object,BmobException e) {
                if(e==null){
                    Log.i("自定义","有几个可以推送的快递员"+object.size()+"");
                    //推送给5个快递员后，查询接受订单的人，并跳转到地图页面。
                    for(int i = 0; i< object.size() ; i++){
                        installationId = object.get(i).getInstallationId();
                        BmobPushManager bmobPush = new BmobPushManager();
                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                        query.addWhereEqualTo("installationId", installationId);
                        bmobPush.setQuery(query);
                        bmobPush.pushMessage(jsonObject, new PushListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i("自定义", "消息推送成功");
                                } else {
                                    Log.i("自定义", "消息推送失败"+e.getMessage()+e.getErrorCode());
                                }
                            }
                        });
                    }
                    Log.i("自定义", "完成消息推送");
                }else{
                    Log.i("自定义", "获取最近的5个快递员地址失败"+e.getMessage()+e.getErrorCode());
                }
            }
        });
    }


    //查询订单ObjectId。
    private void searchObjectId(){
        String bql="select * from Order where orderTime = ? and orderName = ?";
        BmobQuery<Order> query=new BmobQuery<Order>();
        query.setSQL(bql);
        query.setPreparedParams(new Object[]{orderTime,orderName});
        query.doSQLQuery(new SQLQueryListener<Order>() {
            @Override
            public void done(BmobQueryResult<Order> bmobQueryResult, BmobException e) {
                if(e == null){
                    List<Order> list = (List<Order>) bmobQueryResult.getResults();
                    if(list!=null && list.size()>0){
                        Log.i("自定义", "DelayActivity中，查找订单ObjectId成功，" + list.get(0).getObjectId());
                        objectId = list.get(0).getObjectId();
                        searchRunnable = new SearchRunnable();
                        searchHandler.post(searchRunnable);
                    }else{
                        Log.i("自定义","DelayActivity中，查找ObjectId成功，无数据返回");
                    }
                }else{
                    Log.i("自定义","DelayActivity中，查找ObjectId失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //导航按钮事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //第五步，释放地理编码检索实例；
        mSearch.destroy();
    }
}
