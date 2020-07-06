package com.simplechat.mainui.friendlist;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.mainui.friendlist.domain.Friend;
import com.simplechat.userinfo.UserInfoActivity;
import com.simplechat.utils.FileUtils;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FriendListFragment extends ListFragment {

    private FriendListAdapter adapter;
    private static List<Friend> friendList;
    private static final   String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    //当前登录的用户
    private static User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ListView的适配器
        //发送请求获取数据，并更新适配器数据
       init();
        sendRequestAndUpdateAdaptor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendlist, null);
        setListAdapter(adapter);
        return view;
    }


    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView nickname = (TextView)v.findViewById(R.id.nickname);
        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
        //用数据捆传递数据
        Friend friend = friendList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("friend", (Serializable) friend);
        //把数据捆设置改意图
        intent.putExtra("friendBundle", bundle);
        startActivity(intent);
    }

    private void init(){
//        从loginActivity中获取user对象
//        Bundle userBundle = this.getActivity().getIntent().getBundleExtra("userBundle");
//        User user = (User) userBundle.getSerializable("user");
        try {
            Intent intent = this.getActivity().getIntent();
            Bundle userBundle = intent.getBundleExtra("userBundle");
            assert userBundle != null;
            user = (User) userBundle.getSerializable("user");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Request initRequest(){
        //定义访问的api
        String url = BASE_URL + "contact/getFriendList";
        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put("username", user.getUsername());
        return RequestUtils.buildRequestForPostByForm(url, reqMap);
    }

    private void sendRequestAndUpdateAdaptor(){
        //从文件中获取联系人列表
        try {
            FileInputStream fis = this.getActivity().openFileInput("friendList"+user.getUsername() + ".dat");
            String readTextFile = FileUtils.readTextFile(fis);
            setAdapter(readTextFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        //放在try catch里面运行，避免程序闪退
        try {
            //handler start
            Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                        //获取运行在子线程中的OkHttp访问得到的数据
                        String result = (String) msg.getData().getSerializable("responseData");
                        System.out.println("Handler:" + result);
                        //利用返回的数据更新适配器
                        try {
                            setAdapter(result);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };
            //handler end

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
                    Message message = new Message();

                    //数据比较大，使用Bundle封装
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("responseData", responseData);

                    //将bundle存入message
                    message.setData(bundle);
                    //设置handler什么时候作用
                    message.what = 1;
                    //向主线程发消息
                    handler.sendMessage(message);
                }
            });
            //OkHttp end
        } catch (Exception e){
            Log.e(TAG, "handler failure", e);
        }
    }

    private void setAdapter(String responseData){
        try {
            //利用Jackson将返回的数据反序列化成Java对象
            ObjectMapper objectMapper = new ObjectMapper();
            Friend[] friends = objectMapper.readValue(responseData, Friend[].class);
            friendList = Arrays.asList(friends);

            //利用请求得到的数据List来实例化一个新的适配器
            adapter = new FriendListAdapter(this.getActivity(), android.R.layout.simple_list_item_1, friendList);

            //调用父类ListFragment的setListAdapter设置适配器
            setListAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //将消息存到本地，以便无网时使用
        try {
            FileOutputStream fos = this.getActivity().openFileOutput("friendList"+ user.getUsername() + ".dat", Context.MODE_PRIVATE);
            fos.write(responseData.getBytes());
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
