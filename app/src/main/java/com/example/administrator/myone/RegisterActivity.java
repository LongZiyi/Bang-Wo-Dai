package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {
    private Button button_send ;
    private EditText edittext ;
    private Button register ;
    private EditText code ;
    private EditText account;
    private EditText key;
    private EditText surekey;
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
        setContentView(R.layout.activity_register);

        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");
        BmobSMS.initialize(this,"5aa037f5fd4cb1bc3650dd581920c8ab");
        Button buttonRegister = (Button) findViewById(R.id.register);
        button_send = (Button) findViewById(R.id.setcode);
        edittext = (EditText) findViewById(R.id.phonenum);
        register = (Button) findViewById(R.id.register_register);
        account = (EditText) findViewById(R.id.register_name);
        key = (EditText) findViewById(R.id.register_pwd);
        surekey = (EditText) findViewById(R.id.register_surepw);
        code = (EditText) findViewById(R.id.code);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util();
            }
        });
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Yanzhengma();
            }
        });
    }

    private void Util(){
        String number = edittext.getText().toString();
        if (number.equals("") || number.length() != 11){
            Toast.makeText(RegisterActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
        }else{
            BmobSMS.requestSMSCode(this, number, "yanzhengma", new RequestSMSCodeListener(){
                @Override
                public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                    // TODO Auto-generated method stub
                    if (e == null){
                        Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void Yanzhengma() {
        if(key.getText().toString().trim().equals(surekey.getText().toString().trim())) {
            String phonecode = code.getText().toString();
            String number = edittext.getText().toString();
            BmobSMS.verifySmsCode(this, number, phonecode, new VerifySMSCodeListener() {
                @Override
                public void done(cn.bmob.sms.exception.BmobException e) {
                    // TODO Auto-generated method stub
                    if (e == null) {//短信验证码已验证成功
                        Toast.makeText(RegisterActivity.this, "手机验证成功", Toast.LENGTH_SHORT).show();
                        Register();
                        Log.i("bmob", "验证通过");
                    } else {
                        Toast.makeText(RegisterActivity.this, "手机验证失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        }
    }

    private void Register(){
        EditText editTextUserName = (EditText) findViewById(R.id.register_name);
        EditText editTextUserPassword = (EditText) findViewById(R.id.register_pwd);
        EditText editTextUserPhone = (EditText) findViewById(R.id.phonenum);
        String userName = editTextUserName.getText().toString();
        String userPassword = editTextUserPassword.getText().toString();
        String userPhone = editTextUserPhone.getText().toString();
        User user = new User();
        user.setUsername(userName);
        user.setPassword(userPassword);
        user.setMobilePhoneNumber(userPhone);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null){
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    Log.i("自定义",e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
