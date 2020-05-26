package com.example.planowestapp1;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class TimeEntry {
    private int date;
    private int hour;
    private int minute;
    private int duration;
    private String description;

    public TimeEntry(int date, int hour, int minute, int duration, String description) {
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.description = description;
    }

    public String getDay() {
        if (date == 0)
            return "Today";
        else if (date == 1)
            return "Tomorrow";
        else
            return "In Two Days";
    }

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

}
