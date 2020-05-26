package com.example.planowestapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends ArrayAdapter<FriendEntry> {

    Context context;
    int resource;
    String tab;

    public FriendListAdapter(Context context, int resource, ArrayList<FriendEntry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String friendName = getItem(position).getFriendName();
        String nickname = getItem(position).getNickname();
        FriendEntry friendEntry = new FriendEntry(friendName, nickname);

        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(resource, parent, false);
        TextView friendDisplay = (TextView) convertView.findViewById(R.id.friendName);

        if (tab.equals("FRIEND_REQUESTS"))
            friendDisplay.setText(friendName);
        else
            friendDisplay.setText(nickname);

        return convertView;
    }


}
