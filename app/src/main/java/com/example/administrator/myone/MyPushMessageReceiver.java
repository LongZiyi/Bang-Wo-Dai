package com.example.administrator.myone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2017/7/22.
 */

public class MyPushMessageReceiver extends BroadcastReceiver{

    private Map<String, String> myMap;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("自定义bmob", "接收到消息");
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.i("自定义bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
            //Toast.makeText(context, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();


            myMap = parseJson(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String orderName = myMap.get("orderName");
            String orderPrice = myMap.get("orderPrice");
            String orderAddress = myMap.get("orderAddress");
            String orderDestination = myMap.get("orderDestination");
            String orderRemark = myMap.get("orderRemark");
            String orderTime = myMap.get("orderTime");
            String userName = myMap.get("userName");
            String city = myMap.get("city");




            //创建一个通知
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intentNotice = new Intent( context, PushOrderActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("orderName",orderName);
            bundle.putString("orderPrice",orderPrice);
            bundle.putString("orderAddress",orderAddress);
            bundle.putString("orderDestination",orderDestination);
            bundle.putString("orderRemark",orderRemark);
            bundle.putString("orderTime",orderTime);
            bundle.putString("userName",userName);
            bundle.putString("city",city);
            intentNotice.putExtras(bundle);

            intentNotice.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity( context,0,intentNotice,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder( context)
                    .setContentTitle("您已接收到新的订单")
                    .setContentText("点击查看详情")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_push_small)
                    .setLargeIcon(BitmapFactory.decodeResource( context.getResources(),R.drawable.ic_push_big))
                    .setContentIntent(pendingIntent)
                    .setDefaults(android.support.v4.app.NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setPriority(android.support.v4.app.NotificationCompat.PRIORITY_HIGH)
                    .build();
            manager.notify(2,notification);


        }else{
            Log.i("自定义bmob","接收到消息，但没有运行if里面的逻辑");
        }
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            map.put("orderName", jsonObject.getString("orderName"));
            map.put("orderPrice", jsonObject.getString("orderPrice"));
            map.put("orderAddress", jsonObject.getString("orderAddress"));
            map.put("orderDestination", jsonObject.getString("orderDestination"));
            map.put("orderRemark", jsonObject.getString("orderRemark"));
            map.put("userName", jsonObject.getString("userName"));
            map.put("orderTime", jsonObject.getString("orderTime"));
            map.put("city", jsonObject.getString("city"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

}
