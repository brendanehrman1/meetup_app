package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
        if (minutes < 1440)
            dayDisplay.setText("Today");
        else if (minutes < 2880)
            dayDisplay.setText("Tomorrow");
        else
            dayDisplay.setText("In Two Days");
        //dayDisplay.setText(Integer.toString(minutes));
        int hour = (minutes - 1) / 60 % 24;
        int minute = (minutes - 1) % 60;
        int duration = 15;
        boolean startam = hour < 12;
        boolean endam = (hour + (minute + duration) / 60) < 12;
        if (hour > 12)
            hour %= 12;
        String startTimeStr = "" + hour;
        if (startTimeStr.length() == 1)
            startTimeStr = "0" + startTimeStr;
        startTimeStr += ":" + minute;
        if (startTimeStr.length() == 4)
            startTimeStr = startTimeStr.substring(0, 3) + "0" + startTimeStr.substring(3);
        hour = (hour + (minute + duration) / 60);
        if (hour > 12)
            hour -= 12;
        String endTimeStr = "" + hour;
        if (endTimeStr.length() == 1)
            endTimeStr = "0" + endTimeStr;
        endTimeStr += ":" + ((minute + duration) % 60);
        if (endTimeStr.length() == 4)
            endTimeStr = endTimeStr.substring(0, 3) + "0" + endTimeStr.substring(3);
        if (startam)
            startTimeStr += " AM";
        else
            startTimeStr += " PM";
        if (endam)
            endTimeStr += "AM";
        else
            endTimeStr += " PM";
        if (startTimeStr.substring(0,2).equals("00"))
            startTimeStr = "12" + startTimeStr.substring(2);
        if (endTimeStr.substring(0,2).equals("00"))
            endTimeStr = "12" + endTimeStr.substring(2);
        if (startTimeStr.substring(6).equals("PM") && endTimeStr.substring(0,2).equals("12"))
            endTimeStr = endTimeStr.substring(0,6) + "AM";
        timeDisplay.setText(startTimeStr + " - " + endTimeStr);
        ArrayList<TimeEntry> toInclude = new ArrayList<>();
        for (int i = 0; i < timeList.size(); i++) {
            int stTime = (timeList.get(i).getDate() - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) * 1440 + timeList.get(i).getHour() * 60 + timeList.get(i).getMinute();
            int endTime = stTime + timeList.get(i).getDuration();
            if (stTime <= minutes && endTime >= minutes) {
                toInclude.add(timeList.get(i));
            } else {
                //dayDisplay.setText(stTime + " - " + minutes + " - " + endTime);
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
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            minutes = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);
            minutes = minutes / 15 * 15 + 1;
            int date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1;
            if (minutes < 2880)
                date--;
            if (minutes < 1440)
                date--;
            try {
                String info = getFriendTimes(date);
                dayDisplay.setText(Integer.toString(date));
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
            }  catch (Exception e) {
                //dayDisplay.setText(e.toString());
            }
        } else {
            minutes = extras.getInt("MINUTES") + 1;
            timeList = extras.getParcelableArrayList("LIST");
            dayDisplay.setText(timeList.toString());
        }
    }

    public void moveRight(View v) {
        if (minutes == 4306)
            return;
        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("MINUTES", minutes + 14);
        extras.putParcelableArrayList("LIST", timeList);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void moveLeft(View v) {
        if (minutes == (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE)) / 15 * 15 + 1)
            return;
        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("MINUTES", minutes - 16);
        extras.putParcelableArrayList("LIST", timeList);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void goToToday(View v) {
        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("MINUTES", (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE)) / 15 * 15);
        extras.putParcelableArrayList("LIST", timeList);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void goToTomorrow(View v) {
        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("MINUTES", 1920);
        extras.putParcelableArrayList("LIST", timeList);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void goToInTwoDays(View v) {
        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("MINUTES", 3360);
        extras.putParcelableArrayList("LIST", timeList);
        intent.putExtras(extras);
        startActivity(intent);
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
        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/friends?userName=" + displayName + "&date=" + date;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if(responseCode == 200){
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNextLine()){
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
