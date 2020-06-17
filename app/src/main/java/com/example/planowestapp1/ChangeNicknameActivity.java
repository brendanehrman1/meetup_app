package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

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

public class ChangeNicknameActivity extends AppCompatActivity {

    private EditText nicknameInput;
    private TextView friendDisplay;
    private View friendIcon;
    private TextView displayNameDisplay;
    private TextView nicknameDisplay;
    private TextView statusDisplay;
    private View wrapper;

    private String displayName;
    private String friendName;
    private String nickname;
    private String tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadAccountData();
        setScreen();
    }

    public void setScreen() {
        friendDisplay = (TextView) findViewById(R.id.friendName);
        friendIcon = (View) findViewById(R.id.icon);
        displayNameDisplay = (TextView) findViewById(R.id.displayNameBelow);
        nicknameDisplay = (TextView) findViewById(R.id.nicknameBelow);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
        wrapper = (View) findViewById(R.id.wrapper);
        displayNameDisplay.setText(friendName);
        nicknameDisplay.setText(nickname);
        friendDisplay.setText(nickname);
        friendIcon.setBackgroundColor(Color.parseColor(stringToColour(friendName)));
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

    public void changeNickname(View v) throws IOException, JSONException {

        String nicknameInputStr = nicknameInput.getText().toString();
        if (nicknameInputStr.length() == 0) {
            wrapper.getLayoutParams().height = 450;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, you must enter a nickname before proceeding. Please try again.");
            return;
        }
        String info = changeNicknameData(nicknameInputStr);
        JSONObject json = new JSONObject(info);
        if (json.getString("status").equals("nickname")) {
            wrapper.getLayoutParams().height = 450;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, you have already used that nickname for somebody else. Please try again.");
        } else {
            startActivity(new Intent(ChangeNicknameActivity.this, FriendActivity.class));
        }
    }

    public void goBack(View v) {
        Intent intent = new Intent(ChangeNicknameActivity.this, FriendDataActivity.class);
        intent.putExtra("TAB", tab);
        intent.putExtra("FRIEND_NAME", friendName);
        intent.putExtra("NICKNAME", nickname);
        startActivity(intent);
    }

    public void loadAccountData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        Bundle extras = getIntent().getExtras();
        friendName = extras.getString("FRIEND_NAME");
        nickname = extras.getString("NICKNAME");
        tab = extras.getString("TAB");
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
