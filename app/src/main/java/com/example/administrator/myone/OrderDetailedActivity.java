package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class OrderDetailedActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private SwipeRefreshLayout swipeRefresh;

    private String objectId;
    private String orderStatus;

    private String orderName;
    private String orderPrice;
    private String orderAddress;
    private String orderDestination;
    private String orderRemark;
    private String orderTime;
    private String orderAddresser;     //发件人

    private TextView tv_orderName;
    private TextView tv_orderPrice;
    private TextView tv_orderAddress;
    private TextView tv_orderDestination;
    private TextView tv_orderRemark;
    private TextView tv_orderTime;
    private TextView tv_orderAddresser;
    private ImageView iv_orderStatus;

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

        setContentView(R.layout.activity_order_detailed);


        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //实例化控件
        tv_orderName = (TextView) findViewById(R.id.tv_orderName) ;
        tv_orderPrice = (TextView) findViewById(R.id.tv_orderPrice);
        tv_orderAddress = (TextView) findViewById(R.id.tv_orderAddress);
        tv_orderDestination = (TextView) findViewById(R.id.tv_orderDestination);
        tv_orderRemark = (TextView) findViewById(R.id.tv_orderRemark1);
        tv_orderTime = (TextView) findViewById(R.id.tv_orderTime1);
        tv_orderAddresser = (TextView) findViewById(R.id.tv_orderAddresser1);
        iv_orderStatus = (ImageView) findViewById(R.id.iv_orderStatus);

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swip_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        //完成按钮事件
        Button button = (Button)findViewById(R.id.btn_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailedActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //获取从其他页面得到的数据
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");
        orderStatus = intent.getStringExtra("orderStatus");


        //如果是从用户地图跳过来的，说明用户点了已经收货，需要更新订单状态为：已收货，并且把图片设置为已收货
        if(orderStatus.equals("已收货")){
            Order order = new Order();
            order.setOrderStatus("已收货");
            order.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Log.i("bmob","OrderDetailedActivity中，更新订单状态为：已收货，成功。");
                        //图片设置为已收货
                        iv_orderStatus.setImageResource(R.drawable.ic_process4);

                    }else{
                        Log.i("自定义","OrderDetailedActivity中，更新订单状态为：已收货，失败。"+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }else{
            //图片设置为已送达
            iv_orderStatus.setImageResource(R.drawable.ic_process3);
        }


        //查询订单信息
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.getObject(objectId, new QueryListener<Order>() {
            @Override
            public void done(Order order, BmobException e) {
                if(e==null){
                    Log.i("自定义","OrderDetailedActivity中，查询订单数据成功");
                    orderName = order.getOrderName();
                    orderPrice = order.getOrderPrice();
                    orderAddress = order.getOrderAddress();
                    orderDestination = order.getOrderDestination();
                    orderRemark = order.getOrderRemark();
                    orderTime = order.getOrderTime();
                    orderAddresser = order.getOrderAddresser();

                    tv_orderName.setText(orderName);
                    tv_orderPrice.setText(orderPrice);
                    tv_orderAddress.setText(orderAddress);
                    tv_orderDestination.setText(orderDestination);
                    tv_orderRemark.setText(orderRemark);
                    tv_orderTime.setText(orderTime);
                    tv_orderAddresser.setText(orderAddresser);


                }else{
                    Log.i("自定义","OrderDetailedActivity中，查询订单数据失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });




    }


    //下拉刷新事件
    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                //查询 订单信息以及是否已收货
                BmobQuery<Order> query = new BmobQuery<Order>();
                query.getObject(objectId,  new QueryListener<Order>() {
                    @Override
                    public void done(Order order, BmobException e) {
                        //这里会返回两个e，待解决
                        if(e==null){
                            Log.i("自定义","OrderDetailedActivity中，查询是否已收货成功");
                            orderName = order.getOrderName();
                            orderPrice = order.getOrderPrice();
                            orderAddress = order.getOrderAddress();
                            orderDestination = order.getOrderDestination();
                            orderRemark = order.getOrderRemark();
                            orderTime = order.getOrderTime();
                            orderAddresser = order.getOrderAddresser();

                            tv_orderName.setText(orderName);
                            tv_orderPrice.setText(orderPrice);
                            tv_orderAddress.setText(orderAddress);
                            tv_orderDestination.setText(orderDestination);
                            tv_orderRemark.setText(orderRemark);
                            tv_orderTime.setText(orderTime);
                            tv_orderAddresser.setText(orderAddresser);

                            orderStatus = order.getOrderStatus();
                            if(orderStatus.equals("已收货")){
                                Log.i("自定义","OrderDetailedActivity中，查询是否已收货成功,已收货");
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        iv_orderStatus.setImageResource(R.drawable.ic_process4);
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                            }else{
                                Log.i("自定义","OrderDetailedActivity中，查询是否已收货成功,还没收货");
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                            }
                        }else{
                                Log.i("自定义","OrderDetailedActivity中，查询是否已收货失败");
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                        }
                    }
                });
            }
        }).start();
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

}
