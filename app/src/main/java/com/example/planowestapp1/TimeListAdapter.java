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

public class TimeListAdapter extends ArrayAdapter<TimeEntry> {

    Context context;
    int resource;

    public TimeListAdapter(Context context, int resource, ArrayList<TimeEntry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int hour = getItem(position).getHour();
        int minute = getItem(position).getMinute();
        int duration = getItem(position).getDuration();
        String description = getItem(position).getDescription();

        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(resource, parent, false);
        TextView startTime = (TextView) convertView.findViewById(R.id.startTime);
        TextView endTime = (TextView) convertView.findViewById(R.id.endTime);
        TextView dayDisplay = (TextView) convertView.findViewById(R.id.dayDisplay);
        TextView descDisplay = (TextView) convertView.findViewById(R.id.descDisplay);

        boolean startam = hour < 12 || hour == 24;
        boolean endam = ((hour + (minute + duration) / 60) % 24) < 12;
        if (hour > 12)
            hour -= 12;
        if (hour == 0)
            hour = 12;
        String startTimeStr = String.format("%02d:%02d", hour, minute);
        hour = (hour + (minute + duration) / 60);
        if (hour > 12)
            hour -= 12;
        if (hour == 0)
            hour = 12;
        String endTimeStr = String.format("%02d:%02d", hour, ((minute + duration) % 60));
        if (startam)
            startTimeStr += " AM";
        else
            startTimeStr += " PM";
        if (endam)
            endTimeStr += " AM";
        else
            endTimeStr += " PM";
        startTime.setText(startTimeStr);
        endTime.setText(endTimeStr);
        dayDisplay.setText(getItem(position).getDay());
        descDisplay.setText(description);

        return convertView;
    }
}
