package com.simplechat.addfriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.simplechat.R;
import com.simplechat.domain.User;
import com.simplechat.webservices.RequestImage;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private List<User> userList;
    private Integer flag = 0;
    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        userList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.userlist_item, parent, false);
        //用户名字或者昵称，如果有昵称，应当显示昵称
        TextView username = (TextView) convertView.findViewById(R.id.nickname);
        //用户个性签名
        TextView userSign = (TextView) convertView.findViewById(R.id.user_sign);
        User user = userList.get(position);
        username.setText(user.getNickname());
        userSign.setText(user.getSignature());
        //DateFormat df = new SimpleDateFormat("M月d日");
        return convertView;
    }
}
