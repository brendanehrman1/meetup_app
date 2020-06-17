package com.example.planowestapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FriendTimeListAdapter extends ArrayAdapter<TimeEntry> {

    Context context;
    int resource;
    String displayName;

    public FriendTimeListAdapter(Context context, int resource, ArrayList<TimeEntry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String description = getItem(position).getDescription();

        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(resource, parent, false);
        TextView friendNameDisplay = (TextView) convertView.findViewById(R.id.friendNameDisplay);
        TextView friendDescDisplay = (TextView) convertView.findViewById(R.id.friendDescDisplay);

        if (displayName.equals(name))
            friendNameDisplay.setText("Me");
        else
            friendNameDisplay.setText(name);
        friendDescDisplay.setText(description);

        return convertView;
    }
}
