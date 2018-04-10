package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ActionMenuView mActionMenuView;
    private DrawerLayout mDrawerLayout;

    private List<Share> shareList = new ArrayList<>();

    private Uri imageUri;
    public static final int TAKE_PHOTO =1;

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

        setContentView(R.layout.activity_share);




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
                        Toast.makeText(ShareActivity.this,"news",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_wallet:
                        Toast.makeText(ShareActivity.this,"wallet",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_credit:
                        Toast.makeText(ShareActivity.this,"credit",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_history:
                        Toast.makeText(ShareActivity.this,"history",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_skin:
                        Toast.makeText(ShareActivity.this,"skin",Toast.LENGTH_SHORT).show();
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
                        Intent homeIntent = new Intent(ShareActivity.this, MainActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.order:
                        Intent orderIntent = new Intent(ShareActivity.this, OrderActivity.class);
                        startActivity(orderIntent);
                        break;
                    case R.id.share:
                        break;
                    default:
                }
                return true;
            }
        });

        //share事件
        initShares();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ShareAdapter adapter = new ShareAdapter(shareList);
        recyclerView.setAdapter(adapter);

        //发表按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this,EditActivity.class);
                startActivity(intent);

            }
        });



    }

    //初始化
    private void initShares(){
        String Article1 = "第一次用这个软件，很愉快的一次体验，送货速度快，热心的送货小哥长得很帅！";
        String Article2 = "生活不止眼前的苟且，还有各种APP的快捷！";
        String Article3 = "刚刚拿到，还是热乎的，猜猜看这是啥~(*^__^*) 嘻嘻";
        String Article4 = "最怕空气突然安静，最怕下雨天出去买东西，还好有顺路的邻居，用软件call到了";
        String Article5 = "刚才旁边的小哥还问我怎么带这么多东西，还以为我搬家呢。不过我只想说，顺路多带了几单！还有谁？";
        String Article6 = "感觉饿死在路上。。";
        String Article7 = "接了个买清洁剂的单，超市人太多了！找个清洁剂找了半天，很伤！";
        String Article8 = "刚刚送货的小哥竟是我的同乡！自己一个人刚来这个城市，只能说这是软件给与的友谊~";
        String Article9 = "哈哈，下单的人还等着这一袋大米煮饭呢";
        String Article10 = "一个人出差在外，人生地不熟的，还好有app和热心的市民，解决了我的问题！";
        String Article11 = "没时间去拿快递，用app找了个好心人帮忙，真是很方便，太开心了";
        String Article12 = "隔壁的奶奶的孙女在平台下单找人带东西，刚好看到，顺便带来了";
        Share share1 = new Share(Article1,R.drawable.share_1);
        Share share2 = new Share(Article2,R.drawable.share_2);
        Share share3 = new Share(Article3,R.drawable.share_3);
        Share share4 = new Share(Article4,R.drawable.share_4);
        Share share5 = new Share(Article5,R.drawable.share_5);
        Share share6 = new Share(Article6,R.drawable.share_6);
        Share share7 = new Share(Article7,R.drawable.share_7);
        Share share8 = new Share(Article8,R.drawable.share_8);
        Share share9 = new Share(Article9,R.drawable.share_9);
        Share share10 = new Share(Article10,R.drawable.share_10);
        Share share11 = new Share(Article11,R.drawable.share_11);
        Share share12 = new Share(Article12,R.drawable.share_12);
        shareList.add(share1);
        shareList.add(share2);
        shareList.add(share3);
        shareList.add(share4);
        shareList.add(share5);
        shareList.add(share6);
        shareList.add(share7);
        shareList.add(share8);
        shareList.add(share9);
        shareList.add(share10);
        shareList.add(share11);
        shareList.add(share12);


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
