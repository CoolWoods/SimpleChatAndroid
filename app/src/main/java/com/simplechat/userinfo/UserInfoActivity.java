package com.simplechat.userinfo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplechat.MainActivity;
import com.simplechat.mainui.MainUiActivity;
import com.simplechat.R;
import com.simplechat.chat.ChatActivity;
import com.simplechat.chat.Msg;
import com.simplechat.chat.SaveMsgService;
import com.simplechat.domain.Contact;
import com.simplechat.mainui.friendlist.domain.Friend;
import com.simplechat.webservices.RequestImage;


import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    private Button send_message;
    private Button delete_contacts;
    private ImageView friend_image_head;
    private TextView friend_text_nickname;
    private TextView friend_text_username;
    private static Contact contact;

    private Integer flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
        //设置标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(contact.getRemark());
        }
    }

    public void init() {
        Intent intent = this.getIntent();
        Bundle friendBundle = intent.getBundleExtra("friendBundle");
        assert friendBundle != null;
        Friend friend = (Friend) friendBundle.getSerializable("friend");
        friend_image_head=findViewById(R.id.friend_image_head);
        if (flag == 0){
            RequestImage requestImage = new RequestImage();
            requestImage.sendRequestImage(friend_image_head, friend.getHead());
            flag = 1;
        }
        //实例化Contact才能copyProperties
        contact = new Contact();
       contact.setUsername(friend.getUsername());
       contact.setFUsername(friend.getFUsername());
       contact.setRemark(friend.getNickname());
        initView();
    }

    public void initView() {
        List<Msg> list=new ArrayList<Msg>();
        send_message=findViewById(R.id.send_message);
        delete_contacts=findViewById(R.id.delete_contacts);
        friend_text_nickname=findViewById(R.id.friend_text_nickname);
        friend_text_username=findViewById(R.id.friend_text_username);
        friend_text_nickname.setText(contact.getRemark());
        friend_text_username.setText(contact.getFUsername());
        //设置删除好友按钮监听
        delete_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                }

        });

        //设置好友资料页面发消息监听
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", contact);
                intent.putExtra("contactBundle", bundle);
               startActivity(intent);
            }

        });
    }
    //设置确认提示框
    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.asdf);
        builder.setTitle("确认");
        builder.setMessage("是否确认？");
        builder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveMsgService.deleteContacts(contact);
                        Intent intent = new Intent(UserInfoActivity.this, MainUiActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("否", null);
        AlertDialog dialog=builder.create();
        dialog.show();

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
}
