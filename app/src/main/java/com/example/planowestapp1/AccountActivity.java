package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class AccountActivity extends AppCompatActivity {

    private String displayName;
    private String username;
    private String password;
    private TextView displayNameDisplay;
    private TextView usernameDisplay;
    private TextView passwordDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
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
        displayNameDisplay = (TextView) findViewById(R.id.displayName);
        usernameDisplay = (TextView) findViewById(R.id.username);
        passwordDisplay = (TextView) findViewById(R.id.password);
        displayNameDisplay.setText(displayName);
        usernameDisplay.setText(username);
        passwordDisplay.setText(password);
    }

    public void logout(View v) throws IOException {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("NAME");
        editor.apply();
        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
    }

    public void removeAccount(View v) throws IOException, JSONException {
        removeUserData(displayName);
        logout(null);
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
        startActivity(new Intent(AccountActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(AccountActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(AccountActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(AccountActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(AccountActivity.this, AddTimeActivity.class));
    }

    public void changePassword(View v) throws IOException {
        startActivity(new Intent(AccountActivity.this, ChangePasswordActivity.class));
    }

    public static String removeUserData(String displayName) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/login/remove?displayName=" + displayName).openConnection();

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
