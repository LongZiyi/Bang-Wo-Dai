package com.example.administrator.myone;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;
import java.util.Objects;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LocationService extends Service {

    private LocationClient myLocationClient;
    private BmobGeoPoint point;
    private String objectId;
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化BmobSDK
        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);

        Intent intent = new Intent(this, CreditActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("订单推送")
                .setContentText("已开启")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_menu)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_ing_big))
                .setContentIntent(pendingIntent)
                .setDefaults(android.support.v4.app.NotificationCompat.DEFAULT_ALL)
                .build();
        startForeground(1,notification);

        BmobInstallation.getCurrentInstallation().save();

        //开始定位
        myLocationClient = new LocationClient(getApplicationContext());
        myLocationClient.registerLocationListener(new mLocationListener());
        initLocation();
        myLocationClient.start();
    }


    //百度地图定位监听器
    private class mLocationListener implements BDLocationListener{

        private String userName = User.getCurrentUser().getUsername();
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //获得快递员地址经纬度
            double a = bdLocation.getLongitude();
            double b = bdLocation.getLatitude();
            Log.i("自定义", String.valueOf(a));
            Log.i("自定义", String.valueOf(b));
            if(a != 0 && b !=0){
                point = new BmobGeoPoint(bdLocation.getLongitude(), bdLocation.getLatitude());

                //上传快递员地址到MyBmobInstallation表
                updataLocation();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    //上传快递员地址到MyBmobInstallation表
    private void updataLocation(){

        BmobInstallation.getCurrentInstallation().save();
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if(e == null){
                    objectId = list.get(0).getObjectId();
                    Log.i("自定义","MyBmobInstallation表中快递员地址的objectId为"+list.get(0).getObjectId());
                    MyBmobInstallation myBmobInstallation = list.get(0);
                    myBmobInstallation.setLocation(point);
                    myBmobInstallation.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Log.i("自定义","在MyBmobInstallation表中添加快递员地址成功");
                            }else{
                                Log.i("自定义","在MyBmobInstallation表中添加快递员地址失败");
                            }
                        }
                    });
                }else{

                }
            }
        });
    }

    //关闭接受推送时，删除MyBmobInstallation表的快递员信息
    private void delectLocation(){
        MyBmobInstallation myBmobInstallation = new MyBmobInstallation();
        myBmobInstallation.setObjectId(objectId);
        myBmobInstallation.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("自定义","删除快递员信息成功");
                }else{
                    Log.i("自定义","删除快递员信息失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //每隔5分钟定位一次
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(30000);
        myLocationClient.setLocOption(option);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       delectLocation();
        myLocationClient.stop();
        Log.i("自定义","服务onDestroy");

    }
}
