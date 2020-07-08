package com.simplechat.addfriend;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.mainui.MainUiActivity;
import com.simplechat.R;
import com.simplechat.domain.Contact;
import com.simplechat.domain.User;
import com.simplechat.utils.OkHttpUtils;
import com.simplechat.utils.RequestUtils;
import com.simplechat.webservices.RequestImage;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserActivity extends AppCompatActivity {

    private ImageView head;
    private TextView nickname;
    private TextView username;
    private TextView signature;
    private TextView sex;
    private TextView birthday;
    private TextView email;
    private User result;
    private User user;
    private static final   String BASE_URL = "http://10.0.2.2:8080/SimpleChat/";
    private Integer flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        //设置标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("添加朋友");
        }
        Button addfriendButton = findViewById(R.id.addfriend_button);
        addFriend(addfriendButton);
    }

    private void init(){
        head = findViewById(R.id.image_head);
        nickname = findViewById(R.id.text_nickname);
        username = findViewById(R.id.text_username);
        signature = findViewById(R.id.text_signature);
        sex = findViewById(R.id.text_sex);
        birthday = findViewById(R.id.text_birthday);
        email = findViewById(R.id.text_email);

        try {
            Intent intent = this.getIntent();

            Bundle resultBundle = intent.getBundleExtra("resultBundle");
            assert resultBundle != null;
            result = (User) resultBundle.getSerializable("result");

            Bundle userBundle = intent.getBundleExtra("userBundle");
            assert userBundle != null;
            user = (User) userBundle.getSerializable("user");

        }catch (Exception e){
            e.printStackTrace();
        }

        //清空布局中预设的内容
        try {
            nickname.setText(null);
            username.setText(null);
            signature.setText(null);
            sex.setText(null);
            this.birthday.setText(null);
            email.setText(null);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            assert result != null;
            nickname.setText(result.getNickname());
            username.setText(result.getUsername());
            signature.setText(result.getSignature());
            sex.setText(result.getSex());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            birthday.setText(sdf.format(result.getBirthday()));
            email.setText(result.getEmail());
            String imageName = result.getHead();
            if (flag == 0){
                RequestImage requestImage = new RequestImage();
                requestImage.sendRequestImage(head, imageName);
                flag = 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addFriend(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handler start
                @SuppressLint("HandlerLeak") Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {//System.out.println("访问成功:\n" + responseData);
                            Integer code = (Integer) msg.obj;
                            if ( code == 200) {
                                showSuccessDialog();
                            } else {
                                showErrorDialog();
                            }
                        }
                    }
                };
                //OkHttp start
                //获取一个OkHttpClient实例
                OkHttpClient client = OkHttpUtils.getInstance();
                //获取一个request对象
                Contact contact = new Contact();

                contact.setUsername(user.getUsername());

                contact.setFUsername(result.getUsername());
                contact.setRemark(result.getNickname());

                Request request = initRequest(contact);
                //使用OkHttpClient实例执行请求
                try {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure:", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Integer code = response.code();
                            //System.out.println(responseData);
                            System.out.println("AddContactResponseData:" + code);
                            Message message = new Message();
                            message.what = 1;
                            message.obj = code;
                            handler.sendMessage(message);
                        }
                    });
                }catch ( Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private Request initRequest(Contact contact){
        //定义访问的api
        String url = BASE_URL + "contact/addContact";
        ObjectMapper objectMapper = new ObjectMapper();
        String contactJson = "";
        try {
            contactJson = objectMapper.writeValueAsString(contact);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return RequestUtils.buildRequestForPostByJson(url, contactJson);
    }

    private void showSuccessDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("添加成功！");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                turnToMainUiActivity();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //设置确认提示框
    private void showErrorDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("添加失败！");
        builder.setPositiveButton("确认", null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void turnToMainUiActivity() {
        //用户已登录成功，向MainActivity跳转，并携带用户信息
        Intent intent = new Intent(UserActivity.this, MainUiActivity.class);

        Bundle userBundle = new Bundle();
        userBundle.putSerializable("user", user);
        intent.putExtra("userBundle", userBundle);

        startActivity(intent);
    }
}
