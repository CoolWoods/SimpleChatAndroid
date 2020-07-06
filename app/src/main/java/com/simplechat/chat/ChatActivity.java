package com.simplechat.chat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.R;
import com.simplechat.domain.Contact;
import com.simplechat.domain.User;
import com.simplechat.utils.FileUtils;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter msgAdapter;
    private static List<Msg> msgList = new ArrayList<Msg>();
    public static String USERNAME;
    private Contact contact;
    private Handler mHandler;
    private static final String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        init();
        initData();
        initView();

        //设置标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(contact.getRemark());
        }
    }

    private void init() {
       /* userName = getIntent().getStringExtra(ChatActivity.USERNAME);
        fUserName = getIntent().getStringExtra(ChatActivity.FUSERNAME);
        nickName=getIntent().getStringExtra(ChatActivity.NICKNAME);*/
        inputText=(EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgListView=(ListView)findViewById(R.id.msg_list_view);
       Intent intent = this.getIntent();
        Bundle contactBundle = intent.getBundleExtra("contactBundle");
        assert contactBundle != null;
        contact = (Contact) contactBundle.getSerializable("contact");
        assert contact != null;
        USERNAME= contact.getUsername();
    }

    private void initData() {
        //从文件中获取消息列表
        try {
            FileInputStream fis = this.openFileInput("msgList"+ contact.getUsername() + ".dat");
            String readTextFile = FileUtils.readTextFile(fis);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Msg[] msgs = objectMapper.readValue(readTextFile, Msg[].class);
                msgList = new ArrayList<Msg>(Arrays.asList(msgs));
            } catch (Exception e) {
                e.printStackTrace();
            }
            msgAdapter=new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
            msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
        }catch (Exception e){
            e.printStackTrace();
        }
        //msgList = (List<Msg>)getIntent().getSerializableExtra("msgList");//通过key来获取你传输的list集合数据，并强转为List<Object>格式，Object就是前面红色字体部分说的，要实现Serializable接口。


        //向服务器请求消息列表
        sendRequestInterval();
    }


    private void initView() {
        msgAdapter=new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
        msgListView.setAdapter(msgAdapter);//给主页面的ListView设置适配器
        msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg=new Msg(contact.getUsername(), contact.getFUsername(), content, Msg.TYPE_SEND);
                    msgList.add(msg);
                    msgAdapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示  
                    msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
                    inputText.setText("");//清空输入框的内容  
                    SaveMsgService.msgSave(msg);
                }
            }
        });

    }

    /**
     * 使用actionBar时的事件监听
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMsgListLocal(String responseData){
        //将消息存到本地，以便无网时使用
        try {
            FileOutputStream fos = this.openFileOutput("msgList"+ contact.getUsername()+ ".dat", Context.MODE_PRIVATE);
            fos.write(responseData.getBytes());
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendRequestMessageList(){

        OkHttpClient client = OkHttpUtils.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        Request request = initRequest(contact);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure:", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Bundle bundle = new Bundle();
                bundle.putSerializable("responseData", responseData);
                System.out.println("responseData:"+responseData);
                Message message = new Message();
                message.setData(bundle);
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
    }

    //初始化一个请求
    private Request initRequest(Contact contact) {
        //定义访问的api
        String url = BASE_URL +  "message/selectMsg";
        ObjectMapper objectMapper = new ObjectMapper();
        String contactJson = null;
        try {
            contactJson = objectMapper.writeValueAsString(contact);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return RequestUtils.buildRequestForPostByJson(url, contactJson);
    }

    @SuppressLint("HandlerLeak")
    private void sendRequestInterval(){
        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                    {
                        Bundle bundle = msg.getData();
                        String responseData = (String)bundle.getSerializable("responseData");
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            Msg[] msgs = objectMapper.readValue(responseData, Msg[].class);
                            //注意，直接使用Arrays.asList()方法得到的List不可以使用add方法
                            msgList = new ArrayList<Msg>(Arrays.asList(msgs));
                            msgAdapter=new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
                            msgListView.setAdapter(msgAdapter);
                            msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        saveMsgListLocal(responseData);
                    }
                    break;

                    default:
                        break;
                }
            };
        };

        System.out.println("sendRequestInterval");
        Runnable mTimeCounterRunnable = new Runnable() {
            @Override
            public void run() {//在此添加需轮寻的接口
                sendRequestMessageList();;//getUnreadCount()执行的任务
                //当用户为null的时间阻塞线程
                if (contact == null) {
                    try {
                        Thread.sleep(10*24*60*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //4秒一次
                mHandler.postDelayed(this, 10 * 1000);
            }
        };
        mTimeCounterRunnable.run();
    }
}
