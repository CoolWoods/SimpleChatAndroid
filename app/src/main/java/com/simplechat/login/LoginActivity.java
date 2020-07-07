package com.simplechat.login;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.MainActivity;
import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.mainui.MainUiActivity;
import com.simplechat.register.RegisterActivity;
import com.simplechat.utils.FileUtils;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button bnLogin;
    private Button bnGotoRegister;

    private String username;
    private String password;

    private static final int OK = 200;
    private static final String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";

    //mHandler用于实现登录信息回调主线程
    private Handler mHandler;
    private User loginUser;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("用户登录");
        }

        init();
        setClickListener();
        //尝试自动登录
        if (username != null && password !=null)
        try {
            bnLogin.callOnClick();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void init() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        bnLogin = findViewById(R.id.bn_login);
        bnGotoRegister = findViewById(R.id.bn_goto_register);


        //从文件中获取登录的用户信息,并填充到输入框
        try {
            FileInputStream fis = LoginActivity.this.openFileInput("saveUser" + ".dat");
            String readTextFile = FileUtils.readTextFile(fis);
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(readTextFile, User.class);

            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());
            username = user.getUsername();
            password = user.getPassword();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            //获取注册成功的用户信息
            Bundle userBundle = getIntent().getBundleExtra("userBundle");
            assert userBundle != null;
            User user = (User) userBundle.getSerializable("user");

            assert user != null;
            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());
            username = user.getUsername();
            password = user.getPassword();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setClickListener() {

        //设置登录按钮监听事件
        bnLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                //将获取到的登录用户名和密码封装到user对象中
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    //使用handler处理请求返回的消息
                    mHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                                //获取运行在子线程中的OkHttp访问得到的数据
                                String result = (String) msg.getData().getSerializable("responseData");
                                boolean isSuccess = formatResult(result);
                                if (isSuccess){
                                    try {
                                        FileOutputStream fos = LoginActivity.this.openFileOutput("user"+ user.getUsername() + ".dat", Context.MODE_PRIVATE);
                                        fos.write(result.getBytes());
                                        fos.close();
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    turnToMainUiActivity();
                                } else {
                                    Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
                                    //清空密码框内容
                                    etPassword.setText("");
                                }
                            }
                        }
                    };

                    sendLoginRequest(user);
                }
            }
        });

        //设置注册按钮监听事件
        bnGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击“没有账号？去注册”跳转至注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    //初始化一个请求
    private Request initRequest(User user) {
        //定义访问的api
        String url = BASE_URL + "user/login";
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = null;
        try {
            userJson = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return RequestUtils.buildRequestForPostByJson(url, userJson);
    }

    //向服务器发送一个请求
    private void sendLoginRequest(User user){
        Request request = initRequest(user);
        //OkHttp start
        //获取一个OkHttpClient实例
        OkHttpClient client = OkHttpUtils.getInstance();
        //获取一个request对象
        //使用OkHttpClient实例执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(LoginActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
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
            loginUser = objectMapper.readValue(result, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //将已经登录的用户存到本地，以便下次启动时读取
        if (loginUser !=null){
            try {

                //保存登录的账号密码信息
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                String saveUser = objectMapper.writeValueAsString(user);
                FileOutputStream fos = this.openFileOutput("saveUser" + ".dat", Context.MODE_PRIVATE);
                fos.write(saveUser.getBytes());
                fos.close();

            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }else {
            return false;
        }
    }

    private void turnToMainUiActivity() {
        //将消息存到本地，以便无网时使用
        //用户已登录成功，向MainActivity跳转，并携带用户信息
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        Bundle userBundle = new Bundle();
        userBundle.putSerializable("user", loginUser);
        intent.putExtra("userBundle", userBundle);

        startActivity(intent);
    }
}

