package com.example.tejasvedantham.pttmobile2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdminListAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = getLayoutInflater().inflate(R.layout.admin_user_row_item, parent, false);
//        }
//
//        ((TextView) convertView.findViewById(android.R.id.text1))
//                .setText(getItem(position));
        return convertView;
    }
}
