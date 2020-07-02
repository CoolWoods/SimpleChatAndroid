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
import com.simplechat.ui.domain.User;
import com.simplechat.ui.message.domain.MessageListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter msgAdapter;
    private List<Msg> msgList=new ArrayList<Msg>();
    private MessageListItem messageListItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //从Intent中取出传来messageListItemList
        initMessageListItemListByIntent();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            try {
                actionBar.setTitle(messageListItem.getNickname());
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        initMsg();
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
                    Msg msg=new Msg(content,Msg.TYPE_SEND);
                    msgList.add(msg);
                    msgAdapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示  
                    msgListView.setSelection(msgList.size());//将ListView定位到最后一行  
                    inputText.setText("");//清空输入框的内容  
                }
            }
        });
    }

    private void initMsg() {
        Msg msg1=new Msg("我喜欢你",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2=new Msg("我也喜欢你",Msg.TYPE_SEND);
        msgList.add(msg2);
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
            messageListItem = (MessageListItem) bun.get("messageListItem");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
