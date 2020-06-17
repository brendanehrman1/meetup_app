package com.example.planowestapp1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.math.BigInteger;
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
        View iconDisplay = (View) convertView.findViewById(R.id.icon);

        iconDisplay.setBackgroundColor(Color.parseColor(stringToColour(friendName)));

        if (tab.equals("FRIEND_REQUESTS"))
            friendDisplay.setText(friendName);
        else
            friendDisplay.setText(nickname);

        return convertView;
    }

    String stringToColour(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (int)(str.charAt(i)) + ((hash << 5) - hash);
        }
        String colour = "#";
        for (int i = 0; i < 3; i++) {
            int value = (hash >> (i * 8)) & 0xFF;
            String part = "00" + BigInteger.valueOf(value).toString(16);
            System.out.println(part);
            colour += part.substring(part.length() - 2);
        }
        return colour;
    }


}
