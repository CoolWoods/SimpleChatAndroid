package com.simplechat.ui.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.simplechat.R;
import com.simplechat.ui.message.domain.MessageListItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<MessageListItem> {
    private List<MessageListItem> messageListItems;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<MessageListItem> objects) {
        super(context, resource, objects);
        messageListItems = objects;
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.messagelist_item, parent, false);
        //用户头像
        ImageView userImage = (ImageView) view.findViewById(R.id.user_image);
        //用户名字或者昵称，如果有昵称，应当显示昵称
        TextView username =(TextView) view.findViewById(R.id.nickname);
        //用户个性签名
        TextView userSign = (TextView) view.findViewById(R.id.user_message);
        //最后一条消息的时间
        TextView msgClock = (TextView) view.findViewById(R.id.msg_clock);
        MessageListItem messageListItem = messageListItems.get(position);
        userImage.setImageResource(R.mipmap.ic_launcher);
        username.setText(messageListItem.getNickname());
        userSign.setText(messageListItem.getLastMsg());

        //数据库中的时间
        Date date = messageListItem.getLastMsgDate();
        msgClock.setText(dateFormat(new Date(), date));
        //DateFormat df = new SimpleDateFormat("M月d日");
        return view;
    }


    /**
     * 格式化消息列表中要显示的时间
     * @param now 现在的时间
     * @param date  数据库中的时间
     * @return 格式化之后的字符时间
     */
    @SuppressLint("SimpleDateFormat")
    private static String dateFormat(Date now, Date date){
        //两者的时间差，毫秒
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
                return df.format(now);
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
