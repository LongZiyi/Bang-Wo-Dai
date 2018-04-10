package com.example.administrator.myone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ActionMenuView mActionMenuView;
    private DrawerLayout mDrawerLayout;
    private LocationClient mLocationClient;

    private EditText goodsName;
    private EditText goodsAddress;
    private EditText goodsprice;
    private EditText goodsDestination;
    private EditText goodsRemark;
    private Button goodsRequest;
    private TextView position;

    String name = null;
    String address = null;
    String price = null;
    String destination = null;
    String remark = null;
    String city = null;
    private int i = 1;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017070607664056";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088612378463275";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "894757679@qq.com";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "QMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCp2tU9QBKwEDCye+avkxowTAIxlUswZaMjlTPr/1iGbq0ODzKYO85OQSIgzT4KsRPTb3qvkcfewrYtMLjiBRU0ckaCwnTzzzEIwh7lXl9Zmo4rDCCEkH2pZK3QJ+Z7oCWbR4OkP71ubZeAn05xxH04u0Ifr9744GTv63aSuxPmbbLAAMA7YzYNwzwm5mPZbQJhG6Dw3xZJ2EmsrILPIUmID48nmfvucuR7vhYjwOapnio2SEtF1gSspTL0sIWpHXiY8p8OnfzNdIwlCsd/2evkCusY7CGaFWF60bwvYaIuPGLNRgITa18seWSe22uxdU0IpMIFaZnpRfgkkDeJIhtrAgMBAAECggEANdFFcUPE6A4pL9Q8MnM8gAMFSuqPyUTa3OJgci0Uwnt8z5CXKk8AviC7DIObXc3fh0WyLMDXAEjXahge7FZJ8rUmjDXK0plaYvkGywwM0Bo9Y3xTLU2O00Y4IFUUHSWP5NRS7I7ZmYyVzUYsl7V3K6uBdwLFdju7H52Ok009L+wauUH3kuUd/fLozD/vwfTXFAOMOj00LC0TB1Et++yCeb2Bi/qEv3lxzn+FV39Eh9OaIx26tK839fIaMTff2M7MFnMV5wt/xrYa5E6sKeKTlWMwObTXNndYmv3tce3cFQhB6XMnN5U2v+gFmgCl9Z4I42CzTyjQbEEI7nB6zIGEWQKBgQDuiYqPp0ioo8ouiJGLLvsrGQNeLjoVpuW6mC9CIPMwBA+gDQj0///zWt2VskyZro/2QPQHJbu+4v3n2eEQK2Rz6RrXD+TAFrdhcsxeEmkD7Zfmi8USfvlLpWqIU7GVb4DLFh3iKaO7kMiXLTcTgIXvHe6XPFOmxs5bEW0AKIbzbwKBgQC2ShrNFvlQ+qkeZUEI0m5HELt8fz1aLFtyXL5hTNdKS9G8XcSc1T9OlCTQyZIJW6jToJwbKkyW3qw18fcn3OPsLMby1bCJFf1qSACwmuMJeMnQk3ObdhyA6gv0VNwyobUbyyo8p29pppRXm8C5m3DCiVWXCxDmdc1E7tNDcnQpxQKBgBf3t/VxMIrJUlJUpJMgmFofPEhlSq7MQz75dZ4mY2kKr2s8hA7h/hy8n5EgBjRW5t4ubpadQv0OH8cBDG0sfv5qiKjdTvXCEpD3EPPO08ByShX/K0ENgw0HxOmHx0sllgJR6ZTE7E+3X4BZKbSK8GozTrrhc+JJDYj2+kXOzKh/AoGAEoncyejBODwVwPKZN5V3A6EvAA1MpwQF1M6byfdvma22ScrbePuy8YHdcKJfSLZaqBMYbSbTHTDOILpX4WT0hQtUvFOBHZ/v4sQp1cwatNbk52JninLrmSy0RIfw0PFjIs15oSuCCCOZyIMdiYLGZ/JhkvfcIyPt2UnUkHJuX+UCgYBbdFxzCQNb7/fRb3O1gYrwQ7TaJxS/o6njAUHx1yOkzLifHwMWdmt8w7KnnNyIGbwExarYL/pCRulxlHrscW7Ckn7k4FM5QfwmmkGcqfTeEme2WyB8Ydkw6SufGKLfLqNyQNnptPch48ip6W597xYDRdaqQe0QL6v8h+zL+sbjWw==";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DelayActivity.class);
                        intent.putExtra("orderName",name);
                        intent.putExtra("orderPrice",price);
                        intent.putExtra("orderAddress",address);
                        intent.putExtra("orderDestination",destination);
                        intent.putExtra("orderRemark",remark);
                        intent.putExtra("city",city);
                        startActivity(intent);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(MainActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MainActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        };
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,  mActionMenuView.getMenu()); //将menu关联
        return super.onCreateOptionsMenu(menu);
    }

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

        SDKInitializer.initialize(getApplicationContext());//初始化百度地图SDK

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "5aa037f5fd4cb1bc3650dd581920c8ab");

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航栏按钮
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }



        //侧滑菜单按钮事件
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_news);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_news:
                        Toast.makeText(MainActivity.this,"news",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_wallet:
                        Toast.makeText(MainActivity.this,"wallet",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_credit:
                        searchCredit();//查询用户信用值；
                        break;
                    case R.id.nav_history:
                        Toast.makeText(MainActivity.this,"history",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_skin:
                        Toast.makeText(MainActivity.this,"skin",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                return true;
            }
        });

        //侧滑菜单信息
//        String intentdata = getIntent().getStringExtra("intentData");
//        TextView textView = (TextView) findViewById(R.id.username);
//        textView.setText(intentdata);


        //标题栏按钮事件
        mActionMenuView = (ActionMenuView) findViewById(R.id.actionMenuView);
        mActionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        break;
                    case R.id.order:
                        Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
                        startActivity(orderIntent);
                        break;
                    case R.id.share:
                        Intent shareIntent = new Intent(MainActivity.this, ShareActivity.class);
                        startActivity(shareIntent);
                        break;
                    default:
                }
                return true;
            }
        });

        //发布订单按钮
        goodsName = (EditText) findViewById(R.id.goodsName);
        goodsAddress = (EditText) findViewById(R.id.goodAddress);
        goodsprice = (EditText) findViewById(R.id.goodsprice);
        goodsDestination = (EditText) findViewById(R.id.goodsDestination);
        goodsRemark = (EditText) findViewById(R.id.goodsRemark);



        goodsRequest = (Button) findViewById(R.id.goodsRequest);
        goodsRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = goodsName.getText().toString().trim();
                address = goodsAddress.getText().toString().trim();
                price = goodsprice.getText().toString().trim();
                destination = goodsDestination.getText().toString().trim();
                remark = goodsRemark.getText().toString().trim();

                boolean rsa2 = (RSA2_PRIVATE.length() > 0);
                Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(name, address, price, APPID, rsa2);
                String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

                String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
                String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
                final String orderInfo = orderParam + "&" + sign;

                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(MainActivity.this);
                        Map<String, String> result = alipay.payV2(orderInfo, true);
                        Log.i("msp", result.toString());

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        });

        position = (TextView) findViewById(R.id.position);
        requestLocation();//获取地址

    }


    //获取地址
    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(6000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder stringBuilder = new StringBuilder();
            if(location.getCity()!=null){
                stringBuilder.append(location.getCity());
                Message msg = new Message();
                msg.what=1;
                msg.obj=stringBuilder;
                handler.sendMessage(msg);
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    StringBuilder str = (StringBuilder)msg.obj;
                    position.setText(str);
                    city = str.toString();
                    break;
            }
        }
    };

    //导航按钮事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    //查询用户信用值
    private void searchCredit(){
        Toast.makeText(MainActivity.this,"start",Toast.LENGTH_SHORT).show();
        String bql ="select userCreditValue from _User where username ='" + User.getCurrentUser().getUsername() + "'";
        new BmobQuery<User>().doSQLQuery(bql, new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                if(e ==null){
                    List<User> list = (List<User>) bmobQueryResult.getResults();
                    if(list!=null && list.size()>0){
                        Log.i("smile成功", "获取到信用值"+list.size()+list.get(0).getUserCreditValue());
                        if(list.get(0).getUserCreditValue() == 0 ){
                            Intent intent = new Intent(MainActivity.this,ApplyCreditActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(MainActivity.this,CreditActivity.class);
                            intent.putExtra("creditValues",list.get(0).getUserCreditValue());
                            startActivity(intent);
                        }
                    }else{
                        Log.i("smile成功", "没获取到信用值"+list.size());
                        Intent intent = new Intent(MainActivity.this,ApplyCreditActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Log.i("smile错误", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }




}
