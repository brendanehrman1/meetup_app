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
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class TimesActivity extends AppCompatActivity {

    private ListView listView;
    private TextView display;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_times);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadUserData();
        listView = (ListView) findViewById(R.id.timeList);
        display = (TextView) findViewById(R.id.statusDisplay);
        try {
            int daysInYear = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.DAY_OF_YEAR) - 1;
            int hour = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE);
            String info = getTimeData(displayName, daysInYear, hour, minute);
            JSONObject json = new JSONObject(info);
            JSONArray jArr = json.getJSONArray("times");
            ArrayList<TimeEntry> timeList = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject friendOb = new JSONObject(jArr.getString(i));
                int date = Integer.parseInt(friendOb.getString("date"));
                hour = Integer.parseInt(friendOb.getString("hour"));
                minute = Integer.parseInt(friendOb.getString("minute"));
                int duration = Integer.parseInt(friendOb.getString("duration"));
                String description = friendOb.getString("description");
                int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
                hour += offsetHours;
                if (hour < 0) {
                    date--;
                    hour += 24;
                } else if (hour > 24) {
                    date++;
                    hour -= 24;
                }
                daysInYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1;
                int difference = date - daysInYear;
                if (difference >= 0 && difference <= 2)
                    timeList.add(new TimeEntry(displayName, difference, hour, minute, duration, description));
            }
            TimeListAdapter timeListAdapter = new TimeListAdapter(this, R.layout.time_entry_layout, timeList);
            listView.setAdapter(timeListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(TimesActivity.this, TimeDataActivity.class);
                    i.putExtra("DAY", timeList.get(position).getDay());
                    i.putExtra("HOUR", timeList.get(position).getHour());
                    i.putExtra("MINUTE", timeList.get(position).getMinute());
                    i.putExtra("DURATION", timeList.get(position).getDuration());
                    i.putExtra("DESCRIPTION", timeList.get(position).getDescription());
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            display.setText(e.getMessage());
        }
    }

    public void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(TimesActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(TimesActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(TimesActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(TimesActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(TimesActivity.this, AddTimeActivity.class));
    }

    public String getTimeData(String displayName, int date, int hour, int minute) throws IOException {

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/get?userName=" + displayName + "&date=" + date + "&hour=" + hour + "&minute=" + minute;

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
