package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ActionMenuView mActionMenuView;
    private DrawerLayout mDrawerLayout;

    private List<Order1> orderList = new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,  mActionMenuView.getMenu()); //将menu关联
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //隐藏状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }



        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
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
                        Toast.makeText(OrderActivity.this,"news",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_wallet:
                        Toast.makeText(OrderActivity.this,"wallet",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_credit:
                        Toast.makeText(OrderActivity.this,"credit",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_history:
                        Toast.makeText(OrderActivity.this,"history",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_skin:
                        Toast.makeText(OrderActivity.this,"skin",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                return true;
            }
        });

        //标题栏按钮事件
        mActionMenuView = (ActionMenuView) findViewById(R.id.actionMenuView);
        mActionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent homeIntent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.order:
                        break;
                    case R.id.share:
                        Intent shareIntent = new Intent(OrderActivity.this, ShareActivity.class);
                        startActivity(shareIntent);
                        break;
                    default:
                }
                return true;
            }
        });

        //订单列表
        initOrders();
        OrderAdapter adapter = new OrderAdapter(OrderActivity.this, R.layout.order_item, orderList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

    }

    //订单初始化
    private void initOrders(){
        Order1 order1 = new Order1("尼雅红酒","南京市玄武区秋雅酒业",R.drawable.order_1);
        orderList.add(order1);
        Order1 order2 = new Order1("凌士男士沐浴露","上海市松江区寒梅业",R.drawable.order_2);
        orderList.add(order2);
        Order1 order3 = new Order1("友臣肉松饼","南京市秦淮区中华路西华大厦 ",R.drawable.order_3);
        orderList.add(order3);
        Order1 order4 = new Order1("休闲零食椰丝麻薯 ","南京市江宁区淳化街道西城社区",R.drawable.order_4);
        orderList.add(order4);
        Order1 order5 = new Order1("农心辛拉面","南京市江宁区将军路",R.drawable.order_5);
        orderList.add(order5);
        Order1 order6 = new Order1("康师傅劲爽拉面","南京百里秦淮源头溧水县食品工业园",R.drawable.order_6);
        orderList.add(order6);
        Order1 order7 = new Order1("清风卷纸","南京市江宁区禄口街道华商路",R.drawable.order_7);
        orderList.add(order7);
        Order1 order8 = new Order1("美国柠檬","南京市江宁区淳化街道西城社区",R.drawable.order_8);
        orderList.add(order8);
        Order1 order9 = new Order1("海南芒果 大青芒","南京溧水和凤工业园凤翔路",R.drawable.order_9);
        orderList.add(order9);
        Order1 order10 = new Order1("山东红富士","南京溧水区白马镇工集中区",R.drawable.order_10);
        orderList.add(order10);
        Order1 order11 = new Order1("尼雅红酒","南京市秦淮区秋雅酒业",R.drawable.order_1);
        orderList.add(order11);
        Order1 order12 = new Order1("德芙巧克力","南京市建邺区大润发超市",R.drawable.order_12);
        orderList.add(order12);
        Order1 order13 = new Order1("维达抽纸","南京市溧水区第二便利超市",R.drawable.order_13);
        orderList.add(order13);
        Order1 order14 = new Order1("卫康眼镜护理液","上海市黄浦区盛凡眼镜行",R.drawable.order_14);
        orderList.add(order14);
        Order1 order15 = new Order1("贝多健身器","苏州市吴中区友达健身馆",R.drawable.order_15);
        orderList.add(order15);
        Order1 order16 = new Order1("加厚 笔记本","南京市建邺区福园街",R.drawable.order_16);
        orderList.add(order16);
        Order1 order17 = new Order1("A4 文件夹","南京市卫岗童卫路",R.drawable.order_17);
        orderList.add(order17);
        Order1 order18 = new Order1("晨光 文具笔","南京江宁滨江开发区闻莺路",R.drawable.order_18);
        orderList.add(order18);
        Order1 order19 = new Order1("光明 纯牛奶","南京市江宁区麒麟镇",R.drawable.order_19);
        orderList.add(order19);
        Order1 order20 = new Order1("蒙牛 特仑苏","南京和燕路356号馨怡园",R.drawable.order_20);
        orderList.add(order20);
        Order1 order21 = new Order1("北冰洋 汽水","南京莎莉文食品有限公司",R.drawable.order_21);
        orderList.add(order21);
        Order1 order22= new Order1("百事可乐","南京市江宁经济技术开发区",R.drawable.order_22);
        orderList.add(order22);
    }
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



}
