package com.simplechat.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.simplechat.R;
import com.simplechat.ui.chat.ChatActivity;
import com.simplechat.ui.chat.Msg;

import java.lang.reflect.Array;
import java.util.List;

public class MsgAdapter extends ArrayAdapter<Msg> {
    private int resourceId;

    public MsgAdapter(@NonNull Context context,int textViewResourceId, @NonNull List<Msg> objects) {
        super(context,textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Msg msg=getItem(position);
        View view;
        ViewHolder viewHolder;
        int type;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            viewHolder.head1 = (ImageView) view.findViewById(R.id.head1);
            viewHolder.head2 = (ImageView) view.findViewById(R.id.head2);
            viewHolder.time1 = (TextView) view.findViewById(R.id.time1);
            viewHolder.time2 = (TextView) view.findViewById(R.id.time2);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder =(ViewHolder)view.getTag();
        }

            if(msg.getType() ==Msg.TYPE_RECEIVED) {//0
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.head1.setVisibility(View.VISIBLE);
                viewHolder.head2.setVisibility(View.GONE);
                viewHolder.time1.setVisibility(View.VISIBLE);
                viewHolder.time2.setVisibility(View.GONE);
                viewHolder.leftMsg.setText(msg.getMessageContent());
                viewHolder.time1.setText(msg.getMessageDate());
            } else if(msg.getType() ==Msg.TYPE_SEND) {//1
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.head1.setVisibility(View.GONE);
                viewHolder.head2.setVisibility(View.VISIBLE);
                viewHolder.time1.setVisibility(View.GONE);
                viewHolder.time2.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getMessageContent());
                viewHolder.time2.setText(msg.getMessageDate());
            }




        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView head1;
        ImageView head2;
        TextView time1;
        TextView time2;
    }

}
