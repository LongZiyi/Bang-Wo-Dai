package com.example.administrator.myone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar;

    private EditText et_send;
    private Button btn_send;
    private Button btn_map;
    private Integer whoSend;
    private Integer whoRecipient;

    private String content;
    private String orderRecipient;
    private String orderAddresser;

    private List<Chat> msgList = new ArrayList<Chat>();
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


    private ChatRunnable chatRunnable = null;
    private ChatHandler chatHandler = new ChatHandler();


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
        setContentView(R.layout.activity_chat);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        //导航按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }





        //得到发件人和收件人
        Intent intent = getIntent();
        orderRecipient = intent.getStringExtra("orderRecipient");
        orderAddresser = intent.getStringExtra("orderAddresser");
        Log.i("自定义","orderRecipient是"+orderRecipient);
        Log.i("自定义","orderAddresser是"+orderAddresser);

        //判断whoSend的值
        if(orderRecipient.equals(User.getCurrentUser().getUsername())){
            whoSend = 0;
            whoRecipient = 1;
            Log.i("自定义","whoSend = " + whoSend);
            Log.i("自定义","whoRecipient = " + whoRecipient);
        }else {
            whoSend = 1;
            whoRecipient = 0;
            Log.i("自定义","whoSend = " + whoSend);
            Log.i("自定义","whoRecipient = " + whoRecipient);
        }

        //实例化控件
        et_send = (EditText) findViewById(R.id.et_send);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_map = (Button) findViewById(R.id.btn_map);

        //聊天框参数设置
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        //发送按钮事件
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_send.getText().toString();
                if(!"".equals(content)){
                    //上传聊天消息
                    final Chat chat = new Chat(orderAddresser,orderRecipient,content,whoSend,0);
                    chat.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                Log.i("自定义",s);
                                msgList.add(chat);
                                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                                et_send.setText(""); // 清空输入框中的内容
                            }else{
                                Log.i("自定义","发送聊天失败"+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }
            }
        });



        //循环接收聊天数据
        chatRunnable = new ChatRunnable();
        chatHandler.post(chatRunnable);




















        //地图按钮事件
        btn_map.setOnClickListener(new View.OnClickListener() {
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


    class ChatRunnable implements Runnable{
        @Override
        public void run() {
            String bql ="select * from Chat where orderAddresser = ? and orderRecipient = ? and whoSend  = ? and isRead = ? ";
            BmobQuery<Chat> query=new BmobQuery<Chat>();
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{orderAddresser,orderRecipient,whoRecipient,0});
            query.doSQLQuery(bql, new SQLQueryListener<Chat>() {
                        @Override
                        public void done(BmobQueryResult<Chat> bmobQueryResult, BmobException e) {
                            if(e == null){
                                List<Chat> list = (List<Chat>) bmobQueryResult.getResults();
                                if(list!=null && list.size()>0){
                                    for(int i = 0; i<list.size(); i++) {
                                        String content1 = list.get(i).getMessage();
                                        Chat chat1 = new Chat(orderAddresser, orderRecipient, content1, whoRecipient, 0);
                                        msgList.add(chat1);
                                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                                        btn_send.setText(""); // 清空输入框中的内容
                                        String objectId = list.get(i).getObjectId();
                                        //更新消息为已读
                                        Chat chat2 = new Chat(orderAddresser, orderRecipient, content1,whoRecipient, 1);
                                        chat2.update(objectId, new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Log.i("自定义","消息标记为已读成功");
                                                }else{
                                                    Log.i("自定义","消息标记为已读失败："+e.getMessage()+","+e.getErrorCode());
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    Log.i("自定义", "查询聊天信息成功，无数据返回");
                                }
                            }else{
                                Log.i("自定义", "查询聊天信息失败"+e.getErrorCode()+"，错误描述："+e.getMessage());
                            }
                        }
                    });




            chatHandler.postDelayed(this, 2000);
        }
    }


    static class ChatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatHandler.removeCallbacks(chatRunnable);
    }
}
