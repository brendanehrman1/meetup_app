package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class CalendarActivity extends AppCompatActivity {

    private ListView listView;
    private TextView dayDisplay;
    private TextView timeDisplay;
    private View rightArrow;
    private View leftArrow;
    private SeekBar scrollBar;

    public ArrayList<TimeEntry> timeList;

    private String displayName;
    private int minutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadUserData();
        update();
        leftArrow.setVisibility(View.GONE);
        scrollBar.setProgress((int)((double)(minutes % 1440) / 1440 * 100));
        scrollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int curMinutes = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) / 15 * 15 + 1);
                if (minutes < 1440 && (double)progress / 100 * 1440 < curMinutes)
                    seekBar.setProgress((int)((double)(curMinutes % 1440) / 1440 * 100));
                else if (progress != 100){
                    minutes = (int)((double)progress / 100 * 1440) / 15 * 15 + (minutes / 1440 * 1440) + 1;
                    update();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void update() {
        int day = minutes / 1440;
        if (day == 0)
            dayDisplay.setText("Today");
        else if (day == 1)
            dayDisplay.setText("Tomorrow");
        else
            dayDisplay.setText("In Two Days");
        boolean am = false;
        int hours = (minutes - 1) % 1440 / 60;
        if (hours < 12)
            am = true;
        else
            hours -= 12;
        if (hours == 0)
            hours = 12;
        String time = String.format("%02d:%02d", hours, ((minutes - 1) % 1440 % 60));
        if (am)
            time += " AM";
        else
            time += " PM";
        timeDisplay.setText(time);
        ArrayList<TimeEntry> toInclude = new ArrayList<>();
        for (int i = 0; i < timeList.size(); i++) {
            int stTime = (timeList.get(i).getDate() - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) * 1440 + timeList.get(i).getHour() * 60 + timeList.get(i).getMinute();
            int endTime = stTime + timeList.get(i).getDuration();
            if (stTime <= minutes && endTime >= minutes) {
                toInclude.add(timeList.get(i));
            }
        }
        FriendTimeListAdapter friendTimeListAdapter = new FriendTimeListAdapter(this, R.layout.friend_time_entry_layout, toInclude);
        friendTimeListAdapter.setDisplayName(displayName);
        listView.setAdapter(friendTimeListAdapter);
    }

    public void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        listView = (ListView) findViewById(R.id.friendTimeList);
        dayDisplay = (TextView) findViewById(R.id.dayDisplay);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        leftArrow = (View) findViewById(R.id.leftArrow);
        rightArrow = (View) findViewById(R.id.rightArrow);
        scrollBar = (SeekBar) findViewById(R.id.scrollBar);
        minutes = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);
        minutes = minutes / 15 * 15 + 1;
        int date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1;
        try {
            String info = getFriendTimes(date);
            JSONObject json = new JSONObject(info);
            JSONArray jArr = json.getJSONArray("times");
            timeList = new ArrayList<TimeEntry>();
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject timeOb = new JSONObject(jArr.getString(i));
                int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
                int dateInc = timeOb.getInt("date");
                int hourInc = timeOb.getInt("hour");
                hourInc += offsetHours;
                if (hourInc < 0) {
                    date--;
                    hourInc += 24;
                } else if (hourInc > 24) {
                    date++;
                    hourInc -= 24;
                }
                timeList.add(new TimeEntry(timeOb.getString("displayName"), dateInc, hourInc, timeOb.getInt("minute"), timeOb.getInt("duration"), timeOb.getString("description")));
            }
            //dayDisplay.setText(timeList.toString());
        } catch (Exception e) {
            //dayDisplay.setText(e.toString());
        }
    }

    public void goRight(View v) {
        if (minutes < 2880) {
            minutes += 1440;
            update();
            if (minutes > 1440)
                leftArrow.setVisibility(View.VISIBLE);
            if (minutes > 2880)
                rightArrow.setVisibility(View.GONE);
        }
    }

    public void goLeft(View v) {
        if (minutes > 1440) {
            minutes -= 1440;
            update();
            if (minutes < 1440) {
                leftArrow.setVisibility(View.GONE);
                int curMinutes = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) / 15 * 15 + 1);
                if (minutes < curMinutes) {
                    minutes = curMinutes;
                    scrollBar.setProgress((int) ((double) (minutes % 1440) / 1440 * 100));
                    update();
                }
            }
            if (minutes < 2880)
                rightArrow.setVisibility(View.VISIBLE);
        }
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(CalendarActivity.this, AccountActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(CalendarActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(CalendarActivity.this, AddTimeActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(CalendarActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(CalendarActivity.this, CalendarActivity.class));
    }

    public String getFriendTimes(int date) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/friends?userName=" + displayName + "&date=" + date;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                response += scanner.nextLine();
                response += "\n";
            }
            scanner.close();

            return response;
        }

        // an error happened
        return null;
    }

}
