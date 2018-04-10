package com.example.administrator.myone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ShareDetailedActivity extends AppCompatActivity {

    public static final String SHARE_Article = "share_name";
    public static final String SHARE_IMAGE_ID = "share_image_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detailed);

        //隐藏状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Intent intent = getIntent();
        String shareArticle = intent.getStringExtra(SHARE_Article);
        int shareImageId = intent.getIntExtra(SHARE_IMAGE_ID,0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView shareImageView = (ImageView) findViewById(R.id.share_image_view);
        TextView shareContentText = (TextView) findViewById(R.id.shareArticle);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //隐藏标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //设置标题
        //collapsingToolbarLayout.setTitle(shareName);
        Glide.with(this).load(shareImageId).into(shareImageView);
        String shareContent = generateShareContent(shareArticle);
        shareContentText.setText(shareContent);

        //点赞按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share_like);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(ShareDetailedActivity.this,"点赞成功",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private String generateShareContent(String shareArticle){
        StringBuilder shareContent = new StringBuilder();
        for (int i = 0;i < 60 ; i++){
            shareContent.append(shareArticle+i);
        }
        return shareContent.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
