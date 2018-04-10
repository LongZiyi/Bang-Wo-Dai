package com.example.administrator.myone;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.LocationClientOption;

public class CreditActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

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

        setContentView(R.layout.activity_credit);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //更换背景图
        final Intent intent = getIntent();
        int userCreditValue =  intent.getIntExtra("creditValues",1);
        Log.i("CreditActivity",userCreditValue+"");
        switch(userCreditValue){
            case 651:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background651);
                break;
            case 653:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background653);
                break;
            case 666:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background666);
                break;
            case 668:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background668);
                break;
            case 673:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background673);
                break;
            case 675:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background675);
                break;
            case 678:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background678);
                break;
            case 682:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background682);
                break;
            case 700:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background700);
                break;
            case 730:
                findViewById(R.id.maxLayout).setBackgroundResource(R.drawable.background730);
                break;
            default:
                break;
        }

        final Button button = (Button) findViewById(R.id.push);

        //初始化按钮文字
        if(isServiceRunning()){
            button.setText("关闭订单推送");
        }else{
            button.setText("开启订单推送");
        }
        //按钮点击事件

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isServiceRunning()){     //服务处于运行的时候，按钮上显示的的“关闭订单推送”，点击按钮，就关闭服务
                    button.setText("开启订单推送");
                    Intent stopIntent = new Intent(CreditActivity.this, LocationService.class);
                    stopService(stopIntent);
                    Toast.makeText(CreditActivity.this,"已关闭服务",Toast.LENGTH_SHORT).show();


                }else {                      //服务处于关闭的时候，按钮上显示的的“开启订单推送”，点击按钮，就开启服务
                    button.setText("关闭订单推送");
                    Intent startIntent = new Intent(CreditActivity.this, LocationService.class);
                    startService(startIntent);


                    Toast.makeText(CreditActivity.this, "已开启服务" , Toast.LENGTH_SHORT).show();
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

    //查看服务是否正在运行
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.administrator.myone.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}
