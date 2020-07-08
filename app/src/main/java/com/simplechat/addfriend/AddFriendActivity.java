package com.simplechat.addfriend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.IOException;
import java.util.ArrayList;
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

public class AddFriendActivity extends AppCompatActivity {
    private static Button searchButton;
    private static EditText userSearchInput;
    private static ActionBar actionBar;
    private static ListView userResult;
    private static List<User> result;
    private static User user;
    private static final   String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        init();
        searchUser();
        setActionBar();
    }

    private void init(){
        searchButton = this.findViewById(R.id.user_search_button);
        userSearchInput = this.findViewById(R.id.user_search_input);
        userResult = this.findViewById(R.id.user_result);
        actionBar = getSupportActionBar();
        try {
            Intent intent = getIntent();
            Bundle userBundle = intent.getBundleExtra("userBundle");
            assert userBundle != null;
            user = (User) userBundle.getSerializable("user");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setAdapter(String responseData){
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> usernameList = new ArrayList<String>();
        try {
            User[] users = objectMapper.readValue(responseData, User[].class);
            result = new ArrayList<User>(Arrays.asList(users));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        userAdapter = new UserAdapter(this, android.R.layout.simple_list_item_1, result);
        userResult.setAdapter(userAdapter);
        /*Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);*/
    }
    private void setActionBar(){
        try {
            if (actionBar != null){
                actionBar.setTitle("添加好友");
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void searchUser(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = userSearchInput.getText().toString();
                if (input.equals(user.getUsername())){
                    showDialog();
                } else {
                    //handler start -+
                    Handler handler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                                //获取运行在子线程中的OkHttp访问得到的数据
                                String responseData = (String) msg.getData().getSerializable("responseData");
                                //利用返回的数据更新适配器
                                setAdapter(responseData);
                                resultItemOnClick();
                            }
                        }
                    };
                    //OkHttp start
                    //获取一个OkHttpClient实例
                    OkHttpClient client = OkHttpUtils.getInstance();
                    //获取一个request对象
                    Request request = initRequest(input);
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
                }
             }
        });
    }
    private Request initRequest(String username){
        //定义访问的api
        String url = BASE_URL + "user/searchUser";
        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put("username", username);
        return RequestUtils.buildRequestForPostByForm(url, reqMap);
    }

    private void resultItemOnClick(){
        userResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User searchResult = result.get(position);
                Intent intent = new Intent(AddFriendActivity.this, UserActivity.class);

                Bundle resultBundle = new Bundle();
                resultBundle.putSerializable("result", searchResult);
                intent.putExtra("resultBundle", resultBundle);

                Bundle userBundle = new Bundle();
                userBundle.putSerializable("user", user);
                intent.putExtra("userBundle", userBundle);

                startActivity(intent);
            }
        });
    }

    //设置确认提示框
    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("不能添加自己为好友！");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认", null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
