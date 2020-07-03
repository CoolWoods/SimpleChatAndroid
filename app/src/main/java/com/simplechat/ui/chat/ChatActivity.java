package com.simplechat.ui.chat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.simplechat.R;
import com.simplechat.ui.friendlist.domain.Friend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter msgAdapter;
    private List<Msg> msgList=new ArrayList<Msg>();
    private Friend friend;
    public static String USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //从Intent中取出传来messageListItemList
        initMessageListItemListByIntent();
        //init();
        initMsg();
        initView();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            try {
                actionBar.setTitle(friend.getNickname());
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void initView() {
        msgAdapter=new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
        inputText=(EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgListView=(ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(msgAdapter);//给主页面的ListView设置适配器
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg=new Msg(friend.getUsername(),friend.getFUsername(),content,getDate(),Msg.TYPE_SEND);
                    saveMsgService.msgSave(msg);
                    msgList.add(msg);
                    msgAdapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示  
                    msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
                    inputText.setText("");//清空输入框的内容  
                }
            }
        });
    }


    private void init() {
       /* userName = getIntent().getStringExtra(ChatActivity.USERNAME);
        fUserName = getIntent().getStringExtra(ChatActivity.FUSERNAME);
        nickName=getIntent().getStringExtra(ChatActivity.NICKNAME);*/

       /* userName ="123456";
        fUserName ="123457";
        nickName="孙猴子";*/
    }

    private void initMsg() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMessageListItemListByIntent() {
        try {
            Intent intent = getIntent();
            Bundle bun = intent.getBundleExtra("bun");
            friend = (Friend) bun.get("friend");
            USERNAME = friend.getUsername();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return format.format(new Date());
    }
}
