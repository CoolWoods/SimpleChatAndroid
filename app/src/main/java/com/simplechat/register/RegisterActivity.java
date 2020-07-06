package com.simplechat.register;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.login.LoginActivity;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity{
    private EditText etNickname;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRePassword;
    private Button bnRegister;
    private Button bnReset;

    private String nickname;
    private String username;
    private String password;
    private String rePassword;

    private User registerUser;

    private static final String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";

    //mHandler用于实现登录信息回调主线程
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        setClickListener();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("用户注册");
        }
    }

    private void init(){
        etNickname = findViewById(R.id.et_nickname);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_repassword);
        bnRegister = findViewById(R.id.bn_register);
        bnReset = findViewById(R.id.bn_reset);
    }


    private void setClickListener(){
        //设置注册按钮监听事件
        bnRegister.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                nickname = etNickname.getText().toString().trim();
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                rePassword = etRePassword.getText().toString().trim();

                if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
                    Toast.makeText(RegisterActivity.this, "请输入昵称、账号、密码和确认密码！！！", Toast.LENGTH_SHORT).show();
                } else {
                    if (!(password.equals(rePassword))) {
                        Toast.makeText(RegisterActivity.this, "两次输入的密码不一致，请重新输入！！！", Toast.LENGTH_SHORT).show();
                    } else {
                        User register = new User();
                        register.setNickname(nickname);
                        register.setUsername(username);
                        register.setPassword(password);
                        //连接服务器注册
                        //使用handler处理请求返回的消息
                        mHandler = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                                    //获取运行在子线程中的OkHttp访问得到的数据
                                    String result = (String) msg.getData().getSerializable("responseData");
                                    boolean isSuccess = formatResult(result);
                                    if (isSuccess){
                                        turnToLoginActivity();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "用户名已存在！", Toast.LENGTH_SHORT).show();
                                        //清空输入框内容
                                        etUsername.setText("");
                                        etPassword.setText("");
                                        etRePassword.setText("");
                                    }
                                }
                            }
                        };

                        sendRegisterRequest(register);
                    }
                }
            }
        });

        //设置重置按钮监听事件
        bnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText("");
                etPassword.setText("");
                etRePassword.setText("");
            }
        });
    }

    //初始化一个请求
    private Request initRequest(User register) {
        //定义访问的api
        String url = BASE_URL + "user/register";
        ObjectMapper objectMapper = new ObjectMapper();
        String registerJson = null;
        try {
            registerJson = objectMapper.writeValueAsString(register);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return RequestUtils.buildRequestForPostByJson(url, registerJson);
    }

    //向服务器发送一个请求
    private void sendRegisterRequest(User register){
        Request request = initRequest(register);
        //OkHttp start
        //获取一个OkHttpClient实例
        OkHttpClient client = OkHttpUtils.getInstance();
        //获取一个request对象
        //使用OkHttpClient实例执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                //System.out.println(responseData);
                //利用Message向主线程传送数据
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                System.out.println("时间：" + date);
                System.out.println("查询结果：" + responseData);
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
    }

    //解析请求得到的结果,如果失败，说明没有登录成功
    private boolean formatResult(String result){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            registerUser = objectMapper.readValue(result, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //将已经登录的用户存到本地，以便下次启动时读取
        if (registerUser !=null){
            return true;
        }else {
            return false;
        }
    }

    //向LoginActivity跳转
    private void turnToLoginActivity() {
        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
        //用户注册成功，向LoginActivity跳转
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

        Bundle userBundle = new Bundle();
        userBundle.putSerializable("user", registerUser);
        intent.putExtra("userBundle", userBundle);

        startActivity(intent);
    }
}
