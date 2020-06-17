package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class FriendDataActivity extends AppCompatActivity {

    private TextView removeFriendBtn;
    private TextView changeNicknameBtn;
    private TextView friendDisplay;
    private View friendIcon;
    private TextView displayNameDisplay;
    private TextView nicknameDisplay;

    private String displayName;
    private String friendName;
    private String nickname;
    private String tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_data);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadAccountData();
        setScreen();
    }

    public void setScreen() {
        removeFriendBtn = (TextView) findViewById(R.id.removeFriendBtn);
        changeNicknameBtn = (TextView) findViewById(R.id.changeNicknameBtn);
        friendDisplay = (TextView) findViewById(R.id.friendName);
        friendIcon = (View) findViewById(R.id.icon);
        displayNameDisplay = (TextView) findViewById(R.id.displayName);
        nicknameDisplay = (TextView) findViewById(R.id.nickname);
        if (tab.equals("FRIEND_REQUESTS")) {
            Intent intent = new Intent(FriendDataActivity.this, FriendRequestActivity.class);
            intent.putExtra("FRIEND_NAME", friendName);
            intent.putExtra("TAB", tab);
            startActivity(intent);
        } else {
            changeNicknameBtn.setText("CHANGE NICKNAME");
            removeFriendBtn.setText("REMOVE FRIEND");
            if (tab.equals("MY_REQUESTS"))
                removeFriendBtn.setText("REMOVE REQUEST");
            displayNameDisplay.setText(friendName);
            nicknameDisplay.setText(nickname);
            friendDisplay.setText(nickname);
            friendIcon.setBackgroundColor(Color.parseColor(stringToColour(friendName)));
        }
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

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(FriendDataActivity.this, AccountActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(FriendDataActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(FriendDataActivity.this, AddTimeActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(FriendDataActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(FriendDataActivity.this, CalendarActivity.class));
    }

    public void goToFriendRequests(View v) throws IOException {
        Intent i = new Intent(FriendDataActivity.this, FriendActivity.class);
        i.putExtra("TAB", "FRIEND_REQUESTS");
        startActivity(i);
    }

    public void goToMyRequests(View v) throws IOException {
        Intent i = new Intent(FriendDataActivity.this, FriendActivity.class);
        i.putExtra("TAB", "MY_REQUESTS");
        startActivity(i);
    }

    public void loadAccountData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        Bundle extras = getIntent().getExtras();
        friendName = extras.getString("FRIEND_NAME");
        nickname = extras.getString("NICKNAME");
        tab = extras.getString("TAB");
    }

    public void changeNickname(View v) throws IOException, JSONException {
        Intent intent = new Intent(FriendDataActivity.this, ChangeNicknameActivity.class);
        intent.putExtra("FRIEND_NAME", friendName);
        intent.putExtra("NICKNAME", nickname);
        intent.putExtra("TAB", tab);
        startActivity(intent);
    }

    public void removeFriend(View v) throws IOException, JSONException {
        removeFriendData();
        startActivity(new Intent(FriendDataActivity.this, FriendActivity.class));
    }

    public void removeFriendData() throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        friendName = friendName.replaceAll(" ", "%20");
        friendName = friendName.replaceAll("&", "%26");
        friendName = friendName.replaceAll("#", "%23");

        if (tab.equals("FRIENDS") || tab.equals("MY_REQUESTS")) {
            String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/remove?userName=" + displayName + "&friendName=" + friendName;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");

            connection.getResponseCode();
        } else {
            String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/remove?userName=" + friendName + "&friendName=" + displayName;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");

            connection.getResponseCode();
        }
    }
}
