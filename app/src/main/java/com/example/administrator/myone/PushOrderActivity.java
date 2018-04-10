package com.example.administrator.myone;

import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PushOrderActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

    private TextView tv_orderName;
    private TextView tv_orderPrice;
    private TextView tv_orderAddress;
    private TextView tv_orderDestination;
    private TextView tv_orderRemark;
    private TextView tv_orderTime;

    private Button btn_confirm;
    private Button btn_cancel;

    private String objectId;
    private String orderRecipient;
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

        setContentView(R.layout.activity_push_order);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String orderName = bundle.getString("orderName");
        String orderPrice = bundle.getString("orderPrice");
        final String orderAddress = bundle.getString("orderAddress");
        final String orderDestination = bundle.getString("orderDestination");
        String orderRemark = bundle.getString("orderRemark");
        final String orderTime = bundle.getString("orderTime");
        final String city =bundle.getString("city");
        NotificationManager nm = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("自定义","PushOrderActivity中，接收到数据："+orderName +orderPrice+orderAddress+orderDestination+orderRemark+orderTime);

        tv_orderName = (TextView) findViewById(R.id.tv_orderName);
        tv_orderPrice = (TextView) findViewById(R.id.tv_orderPrice);
        tv_orderAddress = (TextView) findViewById(R.id.tv_orderAddress);
        tv_orderDestination = (TextView) findViewById(R.id.tv_orderDestination);
        tv_orderRemark = (TextView) findViewById(R.id.tv_orderRemark);
        tv_orderTime = (TextView) findViewById(R.id.tv_orderTime);

        tv_orderName.setText(orderName);
        tv_orderPrice.setText(orderPrice);
        tv_orderAddress.setText(orderAddress);
        tv_orderDestination.setText(orderDestination);
        tv_orderRemark.setText(orderRemark);
        tv_orderTime.setText(orderTime);
        Log.i("自定义","PushOrderActivity中，设置了文本");



        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查找订单ObjectId
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
                                objectId = list.get(0).getObjectId();
                                orderRecipient = list.get(0).getOrderRecipient();
                                Log.i("自定义", "PushOrderActivity中，查找订单的ObjectId成功，" + objectId);
                                //ObjectId可查，查找是否有发件人
                                if(list.get(0).getOrderAddresser() == null){
                                    //如果没有发件人，更新数据
                                    Order order = new Order();
                                    order.setOrderAddresser(User.getCurrentUser().getUsername());
                                    order.update(objectId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(PushOrderActivity.this, "您已抢到订单，正在建立连接...", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(PushOrderActivity.this,MapActivity.class);
                                                intent.putExtra("orderAddresser",User.getCurrentUser().getUsername());
                                                intent.putExtra("orderRecipient",orderRecipient);
                                                intent.putExtra("orderAddress",orderAddress);
                                                intent.putExtra("orderDestination",orderDestination);
                                                intent.putExtra("city",city);
                                                intent.putExtra("objectId",objectId);
                                                startActivity(intent);
                                            }else{
                                                Log.i("自定义","PushOrderActivity中，发件人更新失败："+e.getMessage()+","+e.getErrorCode());
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(PushOrderActivity.this, "抱歉，您没抢到订单。", Toast.LENGTH_LONG).show();
                                }

                            }else{
                                Toast.makeText(PushOrderActivity.this, "抱歉，您没抢到订单。", Toast.LENGTH_LONG).show();
                                Log.i("自定义", "PushOrderActivity中，查找ObjectId成功，无数据返回");
                            }
                        }else{
                                Log.i("自定义", "PushOrderActivity中，查找ObjectId失败。错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                        }
                    }
                });

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent    );
    }
}
