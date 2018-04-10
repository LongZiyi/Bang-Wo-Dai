package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.b.From.e;

public class ForgetkeyActivity extends AppCompatActivity {
    private Button button_send = null;
    private EditText edittext = null;

    private Button register = null;
    private EditText code = null;
    private EditText key;
    private EditText surekey;
    private String objectId,value,id;
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
        setContentView(R.layout.activity_forgetkey);
        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");
        BmobSMS.initialize(this,"5aa037f5fd4cb1bc3650dd581920c8ab");
        button_send = (Button) findViewById(R.id.forgetkey_setcode);
        edittext = (EditText) findViewById(R.id.forgetkey_phonenum);
        value = edittext.getText().toString();
        register = (Button) findViewById(R.id.forgetkey_nextfoot);
        key = (EditText) findViewById(R.id.forgetkey_pwd);
        surekey = (EditText) findViewById(R.id.forgetkey_pwdsure);
        code = (EditText) findViewById(R.id.forgetkey_code);
        button_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
            Toast.makeText(ForgetkeyActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
        }else{
            BmobSMS.requestSMSCode(this, number, "yanzhengma", new RequestSMSCodeListener(){
                @Override
                public void done(Integer smsId, BmobException ex){
                    // TODO Auto-generated method stub
                    if (ex == null){
                        Toast.makeText(ForgetkeyActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        Log.i("bmob", "短信id：" + smsId);// 用于查询本次短信发送详情
                    }
                }
            });
        }
    }
    private void Yanzhengma(){

        if(key.getText().toString().trim().equals(surekey.getText().toString().trim())) {
            String phonecode = code.getText().toString();
            String pwd = key.getText().toString();
            BmobUser.resetPasswordBySMSCode(phonecode, pwd, new UpdateListener() {
                @Override
                public void done(cn.bmob.v3.exception.BmobException e) {
                    if(e==null){
                        Log.i("smile", "密码重置成功");
                        Toast.makeText(ForgetkeyActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetkeyActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ForgetkeyActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        Log.i("smile", "重置失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                    }
                }
            });
        }else{
            Toast.makeText(ForgetkeyActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
        }
    }

}

