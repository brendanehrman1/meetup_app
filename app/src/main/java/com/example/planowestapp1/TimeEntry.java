package com.example.planowestapp1;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class TimeEntry implements Parcelable {
    private String name;
    private int date;
    private int hour;
    private int minute;
    private int duration;
    private String description;

    public TimeEntry(String name, int date, int hour, int minute, int duration, String description) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.description = description;
    }

    protected TimeEntry(Parcel in) {
        name = in.readString();
        date = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        duration = in.readInt();
        description = in.readString();
    }

    public static final Creator<TimeEntry> CREATOR = new Creator<TimeEntry>() {
        @Override
        public TimeEntry createFromParcel(Parcel in) {
            return new TimeEntry(in);
        }

        @Override
        public TimeEntry[] newArray(int size) {
            return new TimeEntry[size];
        }
    };

    public String getDay() {
        if (date == 0)
            return "Today";
        else if (date == 1)
            return "Tomorrow";
        else
            return "In Two Days";
    }

    public String toString() { return "{" + hour + ":" + minute + " - " + description + "}"; }

    public int getDate() { return date; }

    public String getName() { return name; }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(date);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(duration);
        dest.writeString(description);
    }
}
