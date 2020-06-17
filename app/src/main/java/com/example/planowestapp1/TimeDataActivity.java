package com.example.planowestapp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class TimeDataActivity extends AppCompatActivity {

    private TextView dayDisplay;
    private TextView startTimeDisplay;
    private TextView endTimeDisplay;
    private TextView durationDisplay;
    private TextView descriptionDisplay;
    
    private String displayName;
    private String day;
    private int hour;
    private int minute;
    private int duration;
    private String description;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_data);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadTimeData();
        int hour = this.hour;
        
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
        String durationStr = (duration / 60) + " hours, " + (duration % 60) + " minutes";
        dayDisplay.setText(day);
        startTimeDisplay.setText(startTimeStr);
        endTimeDisplay.setText(endTimeStr);
        durationDisplay.setText(durationStr);
        descriptionDisplay.setText(description);
        
    }

    public void loadTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        dayDisplay = (TextView) findViewById(R.id.day);
        startTimeDisplay = (TextView) findViewById(R.id.startTime);
        endTimeDisplay = (TextView) findViewById(R.id.endTime);
        durationDisplay = (TextView) findViewById(R.id.duration);
        descriptionDisplay = (TextView) findViewById(R.id.description);
        Bundle extras = getIntent().getExtras();
        day = extras.getString("DAY");
        hour = extras.getInt("HOUR");
        minute = extras.getInt("MINUTE");
        duration = extras.getInt("DURATION");
        description = extras.getString("DESCRIPTION");
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(TimeDataActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(TimeDataActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(TimeDataActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(TimeDataActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(TimeDataActivity.this, AddTimeActivity.class));
    }

    public void removeTime(View v) throws IOException, JSONException {
        int hour = this.hour;
        String dayStr = "0";
        if (day.equals("Tomorrow"))
            dayStr = "1";
        else if (day.equals("In Two Days"))
            dayStr = "2";
        int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
        hour -= offsetHours;
        if (hour > 24) {
            dayStr = Integer.toString(Integer.parseInt(dayStr) + 1);
            hour -= 24;
        } else if (hour < 0) {
            dayStr = Integer.toString(Integer.parseInt(dayStr) - 1);
            hour += 24;
        }
        removeTimeData(hour, Integer.toString(Integer.parseInt(dayStr) + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1));
        startActivity(new Intent(TimeDataActivity.this, TimesActivity.class));
    }

    public void modTime(View v) throws IOException, JSONException {
        Intent intent = new Intent(TimeDataActivity.this, ModTimeActivity.class);
        intent.putExtra("DAY", day);
        intent.putExtra("HOUR", hour);
        intent.putExtra("MINUTE", minute);
        intent.putExtra("DURATION", duration);
        intent.putExtra("DESCRIPTION", description);
        startActivity(intent);

    }

    public String removeTimeData(int hour, String date) throws IOException {

        description = description.replaceAll(" ", "%20");
        description = description.replaceAll("&", "%26");
        description = description.replaceAll("#", "%23");
        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/remove?userName=" + displayName + "&date=" + date + "&hour=" + hour + "&minute=" + minute + "&duration=" + duration + "&description=" + description;

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
