package com.example.administrator.myone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ApplyCreditActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private DrawerLayout mDrawerLayout;
    private Button button;
    private int userCreditValue;
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

        setContentView(R.layout.activity_apply_credit);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题


        //导航按钮
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        //确认授权按钮事件
        button = (Button) findViewById(R.id.apply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //随机创建芝麻分
                int i = new Random(System.currentTimeMillis()).nextInt(10);
                switch  (i){
                    case 0:
                        userCreditValue = 651;
                        break;
                    case 1:
                        userCreditValue = 653;
                        break;
                    case 2:
                        userCreditValue = 666;
                        break;
                    case 3:
                        userCreditValue = 668;
                        break;
                    case 4:
                        userCreditValue = 673;
                        break;
                    case 5:
                        userCreditValue = 675;
                        break;
                    case 6:
                        userCreditValue = 678;
                        break;
                    case 7:
                        userCreditValue = 682;
                        break;
                    case 8:
                        userCreditValue = 700;
                        break;
                    case 9:
                        userCreditValue = 730;
                        break;
                    default:
                        break;
                }

                //上传芝麻分到服务器
                startUpload();
                final ProgressDialog progressDialog = new ProgressDialog(ApplyCreditActivity.this);
                progressDialog.setTitle("正在查询芝麻分，请稍等");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(2000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(ApplyCreditActivity.this,CreditActivity.class);
                        intent.putExtra("creditValues",userCreditValue);
                        startActivity(intent);
                    }
                }).start();
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


    //上传芝麻分到服务器
    private void startUpload(){
        String i = BmobUser.getCurrentUser().getObjectId();
        User user = new User();
        user.setValue("userCreditValue",userCreditValue);
        user.update(i, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("自定义bmob","更新成功");
                }else{
                    Log.i("自定义bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
