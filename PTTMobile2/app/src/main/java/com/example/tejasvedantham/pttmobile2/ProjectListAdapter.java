package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectListAdapter extends ArrayAdapter<Project> {

    public ProjectListAdapter(Context context, ArrayList<Project> data) {
        super(context, 0, data);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Project project = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_row_item, parent, false);
        }

        TextView nameProject = (TextView) convertView.findViewById(R.id.nameProject);

        nameProject.setText(project.projectName);

        return convertView;
    }
}
