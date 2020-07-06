package com.simplechat.mainui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simplechat.R;
import com.simplechat.addfriend.AddFriendActivity;
import com.simplechat.domain.User;
import com.simplechat.login.LoginActivity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainUiActivity extends AppCompatActivity {
    //已登录的用户
    private static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        init();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_friendlist, R.id.navigation_message)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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

    /**
     * fragment可以直接获取MainActivity的intent，因此不必再存

    private void saveUser(){
        Bundle userBundle = new Bundle();
        userBundle.putSerializable("user", user);
        getIntent().putExtra("userBundle", userBundle);
    }*/

    public void addFriend(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUiActivity.this, AddFriendActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtra("userBundle", bundle);
                startActivity(intent);
            }
        });
    }
}
