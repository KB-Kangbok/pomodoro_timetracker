package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {

    public UserListAdapter(Context context, ArrayList<User> data) {
        super(context, 0, data);

    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_user_row_item, parent, false);
        }
        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView emailText = (TextView) convertView.findViewById(R.id.emailText);

        nameText.setText(user.firstName + " " + user.lastName);
        emailText.setText(user.email);

        return convertView;
    }

}
