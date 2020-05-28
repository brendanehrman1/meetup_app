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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class AddTimeActivity extends AppCompatActivity {

    private Spinner daySpinner;
    private TimePicker timePicker;
    private TimePicker durationPicker;
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
        String[] days = {"Today", "Tomorrow", "In Two Days"};
        ArrayList<String> dayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dayList.add(days[i]);
        }
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field fieldHr = classForid.getField("hour");
            NumberPicker mHourPicker = (NumberPicker) durationPicker.findViewById(fieldHr.getInt(null));
            mHourPicker.setWrapSelectorWheel(false);
            Field fieldMin = classForid.getField("minute");
            NumberPicker mMinutePicker = (NumberPicker) durationPicker.findViewById(fieldMin.getInt(null));
            mMinutePicker.setMinValue(1);
            mMinutePicker.setMaxValue(3);
            ArrayList<String> mDisplayedValuesMin = new ArrayList<String>();
            for (int i = 15; i < 60; i += 15) {
                mDisplayedValuesMin.add(String.format("%02d", i));
            }
            mMinutePicker.setDisplayedValues(mDisplayedValuesMin.toArray(new String[0]));
            mMinutePicker.setWrapSelectorWheel(false);
            mMinutePicker = (NumberPicker) timePicker.findViewById(fieldMin.getInt(null));
            mMinutePicker.setMaxValue(3);
            mDisplayedValuesMin = new ArrayList<String>();
            for (int i = 0; i < 60; i += 15) {
                mDisplayedValuesMin.add(String.format("%02d", i));
            }
            mMinutePicker.setDisplayedValues(mDisplayedValuesMin.toArray(new String[0]));
            mMinutePicker.setWrapSelectorWheel(false);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {

        }
        daySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayList));
        durationPicker.setIs24HourView(true);
    }

    public void loadTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        daySpinner = (Spinner) findViewById(R.id.daySpinner);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        durationPicker = (TimePicker) findViewById(R.id.durationPicker);
        descInput = (EditText) findViewById(R.id.descInput);
        status = (TextView) findViewById(R.id.statusDisplay);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addTime(View v) throws IOException, JSONException {
        String changedDayStr = daySpinner.getSelectedItem().toString();
        int changedDay = 0;
        if (changedDayStr.equals("Tomorrow"))
            changedDay = 1;
        else if (changedDayStr.equals("In Two Days"))
            changedDay = 2;
        int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
        int hour = timePicker.getCurrentHour();
        hour -= offsetHours;
        if (hour > 24) {
            changedDay++;
            hour -= 24;
        } else if (hour < 0) {
            changedDay--;
            hour += 24;
        }

        int minute = timePicker.getCurrentMinute() * 15;
        int duration = durationPicker.getHour() * 60 + (durationPicker.getMinute() * 15);
        String description = descInput.getText().toString();
        if (description.length() == 0)
            status.setText("Sorry, you must provide a description to set a time. Please try again.");
        else if (changedDay == 0 && (hour < Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY) || hour == Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY) && minute < Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE)))
            status.setText("Sorry, your event must be held after the current time. Please try again.");
        else {
            addTimeData(changedDay + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1, hour, minute, duration, description);
            //JSONObject json = new JSONObject(info);
            //status.setText(json.get("status") + " " + hour + " " + minute + " " + (Integer.toString(Integer.parseInt(dayStr) + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)));
            startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
        }
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AddTimeActivity.class));
    }

    public String addTimeData(int date, int hour, int minute, int duration, String description) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        description = description.replaceAll(" ", "%20");
        description = description.replaceAll("&", "%26");
        description = description.replaceAll("#", "%23");

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
