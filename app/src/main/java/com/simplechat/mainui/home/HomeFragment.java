package com.simplechat.mainui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.login.LoginActivity;
import com.simplechat.mainui.MainUiActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment {
    private TextView nickname;
    private TextView username;
    private TextView signature;
    private TextView sex;
    private TextView birthday;
    private TextView tel;
    private TextView email;
    private Button bnSignout;
    private User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        init(root);
        setClickListener();
        return root;
    }

    private void setClickListener(){
        bnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HomeFragment.this.getActivity().deleteFile("saveUser.dat");
                    Intent intent = new Intent(HomeFragment.this.getActivity(), LoginActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void init(View root){
        //head = findViewById(R.id.image_head);
        try {
            nickname = root.findViewById(R.id.text_nickname);
            username = root.findViewById(R.id.text_username);
            signature = root.findViewById(R.id.text_signature);
            sex = root.findViewById(R.id.text_sex);
            birthday = root.findViewById(R.id.text_birthday);
            tel = root.findViewById(R.id.text_tel);
            email = root.findViewById(R.id.text_email);
            bnSignout = root.findViewById(R.id.bn_signout);
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取登录的用户信息
        try {
            Intent intent = this.getActivity().getIntent();

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
            tel.setText(null);
            email.setText(null);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (user !=null){
                nickname.setText(user.getNickname());
                username.setText(user.getUsername());
                signature.setText(user.getSignature());
                sex.setText(user.getSex());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Date birthday = user.getBirthday();
                this.birthday.setText(sdf.format(birthday));
                tel.setText(user.getTel());
                email.setText(user.getEmail());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
