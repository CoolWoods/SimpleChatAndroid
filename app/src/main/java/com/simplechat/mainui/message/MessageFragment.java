package com.simplechat.mainui.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.R;
import com.simplechat.chat.ChatActivity;
import com.simplechat.domain.Contact;
import com.simplechat.domain.User;
import com.simplechat.mainui.message.domain.MessageListItem;
import com.simplechat.utils.FileUtils;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MessageFragment extends ListFragment {
    private static MessageAdapter adapter;
    private static List<MessageListItem> messageList;
    private static final   String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    private static User user;
    private static Contact contact;
    private static String result;

    //mHandler用于实现轮询
    private Handler mHandler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //使用轮询的方式获取数据，更新视图
        sendRequestInterval();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        //用数据捆传递数据
        MessageListItem messageListItem = messageList.get(position);
        Contact contact = new Contact();
        contact.setUsername(messageListItem.getUsername());
        contact.setFUsername(messageListItem.getFUsername());
        contact.setRemark(messageListItem.getNickname());
        //封装一个contact对象传给给消息页面
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtra("contactBundle", bundle);
        startActivity(intent);
    }

    private void init(){
        try {
            Intent intent = this.getActivity().getIntent();
            Bundle userBundle = intent.getBundleExtra("userBundle");
            assert userBundle != null;
            user = (User) userBundle.getSerializable("user");
        }catch (Exception e){
            e.printStackTrace();
        }

        //从文件中获取消息列表
        try {
            FileInputStream fis = this.getActivity().openFileInput("messageList"+user.getUsername() + ".dat");
            String readTextFile = FileUtils.readTextFile(fis);
            setAdapter(readTextFile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setAdapter(String responseData){

        try {
            //利用Jackson将返回的数据反序列化成Java对象
            ObjectMapper objectMapper = new ObjectMapper();
            MessageListItem[] messageListItems = objectMapper.readValue(responseData, MessageListItem[].class);
            messageList = new ArrayList<MessageListItem>(Arrays.asList(messageListItems));

            //利用请求得到的数据List来实例化一个新的适配器
            adapter = new MessageAdapter(this.getActivity(), android.R.layout.simple_list_item_1, messageList);

            //调用父类ListFragment的setListAdapter设置适配器
            setListAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将消息存到本地，以便无网时使用
        try {
            FileOutputStream fos = this.getActivity().openFileOutput("messageList"+ user.getUsername() + ".dat", Context.MODE_PRIVATE);
            fos.write(responseData.getBytes());
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 初始化Request
     * @return
     */
    private Request initRequest(){
        //定义访问的api
        String url = BASE_URL + "message/getUiMessageItemList";
        Map<String, String> reqMap = new HashMap<String, String>();
        try {
            reqMap.put("username", user.getUsername());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return RequestUtils.buildRequestForPostByForm(url, reqMap);
    }


    @SuppressLint("HandlerLeak")
    private void sendRequestInterval(){
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                    //获取运行在子线程中的OkHttp访问得到的数据
                    result = (String) msg.getData().getSerializable("responseData");
                    //利用返回的数据更新适配器
                    try {
                        setAdapter(result);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };

        System.out.println("sendRequestInterval");
        Runnable mTimeCounterRunnable = new Runnable() {
            @Override
            public void run() {//在此添加需轮寻的接口
                sendRequestAndUpdateAdaptor();;//getUnreadCount()执行的任务
                //当用户为null的时间阻塞线程
                if (user == null) {
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

    private void sendRequestAndUpdateAdaptor(){

        //OkHttp start
        //获取一个OkHttpClient实例
        OkHttpClient client = OkHttpUtils.getInstance();
        //获取一个request对象
        Request request = initRequest();
        //使用OkHttpClient实例执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure:", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                //System.out.println(responseData);
                //利用Message向主线程传送数据
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //String date = sdf.format(new Date());
                //System.out.println("时间：" + date);
                //System.out.println("查询结果：" + responseData);
                Message message = new Message();

                //数据比较大，使用Bundle封装
                Bundle bundle = new Bundle();
                bundle.putSerializable("responseData", responseData);

                //将bundle存入message
                message.setData(bundle);
                //设置handler什么时候作用
                message.what = 1;
                //向主线程发消息
                mHandler.sendMessage(message);
            }
        });
        //OkHttp end
    }
}
