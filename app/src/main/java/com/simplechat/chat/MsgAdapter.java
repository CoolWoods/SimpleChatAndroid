package com.simplechat.chat;

import android.annotation.SuppressLint;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            viewHolder.time2.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getMessageContent());
            try {
                viewHolder.time1.setVisibility(View.VISIBLE);
                viewHolder.time1.setText(simpleDateFormat(msg.getMessageDate()));
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if(msg.getType() ==Msg.TYPE_SEND) {//1
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.head1.setVisibility(View.GONE);
            viewHolder.time1.setVisibility(View.GONE);
            viewHolder.time2.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getMessageContent());

            try {
                viewHolder.head2.setVisibility(View.VISIBLE);
                viewHolder.time2.setText(simpleDateFormat(msg.getMessageDate()));
            }catch (Exception e){
                e.printStackTrace();
            }
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

    @SuppressLint("SimpleDateFormat")
    private String simpleDateFormat(Date date) {
        //两者的时间差，毫秒
        Date now = new Date();
        long l = now.getTime() - date.getTime();
        //两者差的天数
        long day = l/(24*60*60*1000);
        //星期数组
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        //Calendar用于日期计算方便
        Calendar calendarDate = Calendar.getInstance();
        Calendar calendarNow =  Calendar.getInstance();
        calendarNow.setTime(now);
        calendarDate.setTime(date);
        //数据库时间在一周中的位置
        int week = calendarDate.get(Calendar.DAY_OF_WEEK)-1;
        if (week < 0 ) week=0;
        //Date格式化
        DateFormat df = null;
        switch ((int) day){
            case 0:  df = new SimpleDateFormat("HH:mm");
                return df.format(date);
            case 1: return "昨天";
            case 2: return "前天";
            case 3:
            case 5:
            case 4:
            case 6:
                return weekDays[week];
            default: df = new SimpleDateFormat("M月d日");
                return df.format(date);
        }
    }

}
