package com.example.administrator.myone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private EditText account, password;
    private Button btn_login;
    private String userNameValue,passwordValue;
    private SharedPreferences sp;
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
        setContentView(R.layout.activity_login);

        // 初始化BmobSDK
        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);



        //申请运行时的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        } else {
            Toast.makeText(this, "已申请所有权限", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_login);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        account = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPassword);
        btn_login = (Button) findViewById(R.id.login);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        account.setText(sp.getString("USER_NAME", ""));
        password.setText(sp.getString("PASSWORD", ""));
        //登陆按钮事件

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                EditText editTextUserName = (EditText) findViewById(R.id.userName);
                EditText editTextUserPassword = (EditText) findViewById(R.id.userPassword);
                String userName = editTextUserName.getText().toString();
                String userPassword = editTextUserPassword.getText().toString();
                user.setUsername(userName);
                user.setPassword(userPassword);
                userNameValue = account.getText().toString();
                passwordValue = password.getText().toString();
                final SharedPreferences.Editor editor =sp.edit();
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(e==null){
                            Toast.makeText(LoginActivity.this, "登陆成功",Toast.LENGTH_SHORT).show();
                            editor.putString("USER_NAME", userNameValue);
                            editor.putString("PASSWORD",passwordValue);
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);

                        }else{
                            Toast.makeText(LoginActivity.this, "用户名或密码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        //注册按钮事件
        Button buttonRegister = (Button) findViewById(R.id.register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Button buttonForfetkey = (Button) findViewById(R.id.forgetpwd);
        buttonForfetkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetkeyActivity.class);
                startActivity(intent);
            }
        });
        if(TextUtils.isEmpty(account.getText())&TextUtils.isEmpty(password.getText())){
            return ;
        }else{
            jiancha();
        }

    }
    private void jiancha(){
        User user = new User();
        String userName = account.getText().toString();
        String userPassword = password.getText().toString();
        user.setUsername(userName);
        user.setPassword(userPassword);
        user.login(new SaveListener<User>(){
            @Override
            public void done(User user, BmobException e){
                if(e==null) {
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "请重新填写用户名和密码",Toast.LENGTH_SHORT).show();
                    account.setText("");
                    password.setText("");
                }
            }
        });
    }
}
