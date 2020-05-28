package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class FriendDataActivity extends AppCompatActivity {

    private EditText nicknameInput;
    private TextView negativeBtn;
    private TextView positiveBtn;
    private TextView friendInfoDisplay;
    private TextView status;

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
        nicknameInput = (EditText) findViewById(R.id.nicknameInput);
        negativeBtn = (TextView) findViewById(R.id.negativeBtn);
        positiveBtn = (TextView) findViewById(R.id.positiveBtn);
        friendInfoDisplay = (TextView) findViewById(R.id.friendInfoDisplay);
        status = (TextView) findViewById(R.id.statusDisplay);
        if (tab.equals("FRIENDS")) {
            positiveBtn.setPadding(positiveBtn.getPaddingLeft(), 10, positiveBtn.getPaddingRight(), positiveBtn.getPaddingBottom());
            positiveBtn.setText("                         CHANGE\n                          NICKNAME");
            negativeBtn.setPadding(negativeBtn.getPaddingLeft(), 10, negativeBtn.getPaddingRight(), negativeBtn.getPaddingBottom());
            negativeBtn.setText("                         REMOVE\n                            FRIEND");
            String buildString = "";
            buildString += "Here is your friend's information:\n\n";
            buildString += "Display Name: " + friendName + "\n";
            buildString += "Nickname: " + nickname + "\n";
            friendInfoDisplay.setText(buildString);
        } else if (tab.equals("FRIEND_REQUESTS")) {
            positiveBtn.setPadding(positiveBtn.getPaddingLeft(), 30, positiveBtn.getPaddingRight(), positiveBtn.getPaddingBottom());
            positiveBtn.setText("                         APPROVE");
            negativeBtn.setPadding(negativeBtn.getPaddingLeft(), 30, negativeBtn.getPaddingRight(), negativeBtn.getPaddingBottom());
            negativeBtn.setText("                             DENY");
            String buildString = "";
            buildString += "Somebody is inviting you to be their friend!\n\n";
            buildString += "Display Name: " + friendName + "\n";
            friendInfoDisplay.setText(buildString);

        } else if (tab.equals("MY_REQUESTS")) {
            positiveBtn.setPadding(positiveBtn.getPaddingLeft(), 10, positiveBtn.getPaddingRight(), positiveBtn.getPaddingBottom());
            positiveBtn.setText("                         CHANGE\n                          NICKNAME");
            negativeBtn.setPadding(negativeBtn.getPaddingLeft(), 10, negativeBtn.getPaddingRight(), negativeBtn.getPaddingBottom());
            negativeBtn.setText("                         REMOVE\n                           REQUEST");
            String buildString = "";
            buildString += "Here is your current request:\n\n";
            buildString += "Friend's Display Name: " + friendName + "\n";
            buildString += "Friend's Nickname: " + nickname + "\n";
            friendInfoDisplay.setText(buildString);
        }
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

    public void doPositive(View v) throws IOException, JSONException {
        if (tab.equals("FRIENDS")) {
            changeNickname();
        } else if (tab.equals("FRIEND_REQUESTS")) {
            approveRequest();
        } else if (tab.equals("MY_REQUESTS")) {
            changeNickname();
        }
    }

    public void doNegative(View v) throws IOException, JSONException {
        removeFriend();
    }

    public void changeNickname() throws IOException, JSONException {
        String nicknameInputStr = nicknameInput.getText().toString();
        if (nicknameInputStr.length() == 0) {
            status.setText("Sorry, you must enter a nickname before proceeding. Please try again.");
            return;
        }
        String info = changeNicknameData(nicknameInputStr);
        JSONObject json = new JSONObject(info);
        if (json.getString("status").equals("nickname"))
            status.setText("Sorry, you have already used that nickname for somebody else. Please try again.");
        else
            startActivity(new Intent(FriendDataActivity.this, FriendActivity.class));
    }

    public void removeFriend() throws IOException, JSONException {
        removeFriendData();
        startActivity(new Intent(FriendDataActivity.this, FriendActivity.class));
    }

    public void approveRequest() throws IOException, JSONException {
        approveData(nicknameInput.getText().toString());
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

    public void approveData(String nickname) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        friendName = friendName.replaceAll(" ", "%20");
        friendName = friendName.replaceAll("&", "%26");
        friendName = friendName.replaceAll("#", "%23");
        nickname = nickname.replaceAll(" ", "%20");
        nickname = nickname.replaceAll("&", "%26");
        nickname = nickname.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/mod?userName=" + friendName + "&friendName=" + displayName + "&pending=0";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        connection.getResponseCode();

        url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/add?userName=" + displayName + "&friendName=" + friendName + "&nickname=" + nickname;

        connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        connection.getResponseCode();
    }

    public String changeNicknameData(String nickname) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        friendName = friendName.replaceAll(" ", "%20");
        friendName = friendName.replaceAll("&", "%26");
        friendName = friendName.replaceAll("#", "%23");
        nickname = nickname.replaceAll(" ", "%20");
        nickname = nickname.replaceAll("&", "%26");
        nickname = nickname.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/mod?userName=" + displayName + "&friendName=" + friendName + "&nickname=" + nickname;

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
