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

public class AddFriendActivity extends AppCompatActivity {

    private EditText displayNameInput;
    private EditText nicknameInput;
    private TextView statusDisplay;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadData();
    }

    public void sendFriendRequest(View v) throws IOException, JSONException {
        String displayNameInputStr = displayNameInput.getText().toString();
        String nicknameInputStr = nicknameInput.getText().toString();
        if (displayNameInputStr.length() == 0 || nicknameInputStr.length() == 0)
            return;
        String info = getRequestData(displayNameInputStr, nicknameInputStr);
        JSONObject json = new JSONObject(info);
        String status = json.getString("status");
        if (status.equals("friendNotExist"))
            statusDisplay.setText("Sorry, your friend's display name does not exist. Please try again.");
        else if (status.equals("alreadyPending"))
            statusDisplay.setText("Sorry, this friend either already exists or is currently pending. Please try again.");
        else if (status.equals("nicknameUsed"))
            statusDisplay.setText("Sorry, you are already using this nickname for another friend. Please try again.");
        else
            startActivity(new Intent(AddFriendActivity.this, FriendActivity.class));
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        displayNameInput = (EditText) findViewById(R.id.displayNameInput);
        nicknameInput = (EditText) findViewById(R.id.nicknameInput);
        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(AddFriendActivity.this, FriendActivity.class));
    }

    public void goToFriendRequests(View v) throws IOException {
        Intent i = new Intent(AddFriendActivity.this, FriendActivity.class);
        i.putExtra("TAB", "FRIEND_REQUESTS");
        startActivity(i);
    }

    public void goToMyRequests(View v) throws IOException {
        Intent i = new Intent(AddFriendActivity.this, FriendActivity.class);
        i.putExtra("TAB", "MY_REQUESTS");
        startActivity(i);
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(AddFriendActivity.this, AccountActivity.class));
    }

    public String getRequestData(String friendName, String nickname) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/add?userName=" + displayName + "&friendName=" + friendName +  "&nickname=" + nickname).openConnection();

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
