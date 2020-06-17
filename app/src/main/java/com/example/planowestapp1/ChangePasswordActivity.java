package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

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

public class ChangePasswordActivity extends AppCompatActivity {

    private String displayName;
    private String username;
    private String password;
    private TextView statusDisplay;
    private TextView displayNameDisplay;
    private TextView usernameDisplay;
    private TextView passwordDisplay;
    private EditText passInput;
    private EditText passConfInput;
    private View wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            loadAccountData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        update();
    }

    public void update() {
        displayNameDisplay = (TextView) findViewById(R.id.displayNameBelow);
        usernameDisplay = (TextView) findViewById(R.id.usernameBelow);
        passwordDisplay = (TextView) findViewById(R.id.passwordBelow);
        passInput = (EditText) findViewById(R.id.password);
        passConfInput = (EditText) findViewById(R.id.confPass);
        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
        wrapper = (View) findViewById(R.id.wrapper);
        displayNameDisplay.setText(displayName);
        usernameDisplay.setText(username);
        passwordDisplay.setText(password);
    }

    public void changePassword(View v) throws IOException, JSONException {
        String password = passInput.getText().toString();
        String passConf = passConfInput.getText().toString();
        if (password.length() == 0 || passConf.length() == 0) {
            wrapper.getLayoutParams().height = 570;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, you must enter and confirm a new password for your account. Please try again.");
        } else if (!password.equals(passConf)) {
            wrapper.getLayoutParams().height = 570;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, your new password and confirmation password must be the same. Please try again.");
        } else {
            String status = changePassword(displayName, password);
            if (status.equals("otherUser")) {
                wrapper.getLayoutParams().height = 570;
                wrapper.requestLayout();
                statusDisplay.setText("Sorry, another user has the same username and password. Please try again.");
            } else {
                startActivity(new Intent(ChangePasswordActivity.this, AccountActivity.class));
            }
        }
    }

    public void goBack(View v) {
        startActivity(new Intent(ChangePasswordActivity.this, AccountActivity.class));
    }

    public void loadAccountData() throws IOException, JSONException {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        String info = getAccountData(displayName);
        JSONObject json = new JSONObject(info);
        username = json.getString("username");
        password = json.getString("password");
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(ChangePasswordActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(ChangePasswordActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(ChangePasswordActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(ChangePasswordActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(ChangePasswordActivity.this, AddTimeActivity.class));
    }

    public static String changePassword(String displayName, String newPass) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        newPass = newPass.replaceAll(" ", "%20");
        newPass = newPass.replaceAll("&", "%26");
        newPass = newPass.replaceAll("#", "%23");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/login/mod?displayName=" + displayName + "&password=" + newPass).openConnection();

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

    public static String getAccountData(String displayName) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/login/user?displayName=" + displayName).openConnection();

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
