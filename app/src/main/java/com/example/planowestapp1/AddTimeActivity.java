package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class AddTimeActivity extends AppCompatActivity {

    private Spinner daySpinner;
    private Spinner hourSpinner;
    private Spinner minuteSpinner;
    private Spinner durationSpinner;
    private EditText descInput;
    private TextView status;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadTimeData();
        ArrayList<String> dayOptions = new ArrayList<>();
        String[] days = {"Today", "Tomorrow", "In Two Days"};
        for (int i = 0; i < 3; i++) {
            dayOptions.add(days[i]);
        }
        ArrayList<String> hourOptions = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            hourOptions.add(Integer.toString(i));
        }
        ArrayList<String> minuteOptions = new ArrayList<>();
        for (int i = 0; i <= 60; i += 5) {
            minuteOptions.add(Integer.toString(i));
        }
        ArrayList<String> durationOptions = new ArrayList<>();
        for (int i = 15; i <= 1440; i += 15) {
            if (i < 60)
                durationOptions.add(i + " minutes");
            else
                durationOptions.add((i / 60) + " hours, " + (i % 60) + " minutes");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayOptions);
        daySpinner.setAdapter(dataAdapter);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hourOptions);
        hourSpinner.setAdapter(dataAdapter);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, minuteOptions);
        minuteSpinner.setAdapter(dataAdapter);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationSpinner.setAdapter(dataAdapter);
    }

    public void loadTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        daySpinner = (Spinner) findViewById(R.id.daySpinner);
        hourSpinner = (Spinner) findViewById(R.id.hourSpinner);
        minuteSpinner = (Spinner) findViewById(R.id.minuteSpinner);
        durationSpinner = (Spinner) findViewById(R.id.durationSpinner);
        descInput = (EditText) findViewById(R.id.descInput);
        status = (TextView) findViewById(R.id.statusDisplay);
    }

    public void addTime(View v) throws IOException, JSONException {
        String changedDayStr = "0";
        String day = daySpinner.getSelectedItem().toString();
        if (day.equals("Tomorrow"))
            changedDayStr = "1";
        else if (day.equals("In Two Days"))
            changedDayStr = "2";
        int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
        String hour = hourSpinner.getSelectedItem().toString();
        hour = Integer.toString(Integer.parseInt(hour) - offsetHours);
        if (Integer.parseInt(hour) > 24) {
            changedDayStr = Integer.toString(Integer.parseInt(changedDayStr) + 1);
            hour = Integer.toString(Integer.parseInt(hour) - 24);
        } else if (Integer.parseInt(hour) < 0) {
            changedDayStr = Integer.toString(Integer.parseInt(changedDayStr) - 1);
            hour = Integer.toString(Integer.parseInt(hour) + 24);
        }

        String minute = minuteSpinner.getSelectedItem().toString();
        Scanner scan = new Scanner(durationSpinner.getSelectedItem().toString());
        String duration = Integer.toString(scan.nextInt() * 60);
        if (scan.next().equals("minutes")) {
            duration = Integer.toString(Integer.parseInt(duration) / 60);
        } else {
            duration = Integer.toString(Integer.parseInt(duration) + scan.nextInt());
        }
        String description = descInput.getText().toString();
        addTimeData(Integer.toString(Integer.parseInt(changedDayStr) + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1), hour, minute, duration, description);
        //JSONObject json = new JSONObject(info);
        //status.setText(json.get("status") + " " + hour + " " + minute + " " + (Integer.toString(Integer.parseInt(dayStr) + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)));
        startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        //startActivity(new Intent(AddTimeActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AddTimeActivity.class));
    }

    public String addTimeData(String date, String hour, String minute, String duration, String description) throws IOException {

        description = description.replaceAll(" ", "%20");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/add?userName=" + displayName + "&date=" + date + "&hour=" + hour + "&minute=" + minute + "&duration=" + duration + "&description=" + description;

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
