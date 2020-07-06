package com.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplechat.domain.User;
import com.simplechat.login.LoginActivity;
import com.simplechat.mainui.MainUiActivity;
import com.simplechat.utils.FileUtils;

import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {
    //已登录的用户
    private static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        if (user == null){
            //用户未登录，调用LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            //用户已登录，向MainUiActivity跳转，并携带用户信息
            Intent intent = new Intent(MainActivity.this, MainUiActivity.class);

            Bundle userBundle = new Bundle();
            userBundle.putSerializable("user", user);
            intent.putExtra("userBundle", userBundle);

            startActivity(intent);
        }
    }

    private void init(){

        try {
            //获取已经登录的用户信息
            Bundle userBundle = getIntent().getBundleExtra("userBundle");
            assert userBundle != null;
            user = (User) userBundle.getSerializable("user");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
