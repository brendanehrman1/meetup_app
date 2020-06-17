package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class FriendActivity extends AppCompatActivity {

    private ListView listView;
    private TextView display;
    private TextView status;
    private String displayName;
    private String tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadUserData();
        listView = (ListView) findViewById(R.id.friendList);
        display = (TextView) findViewById(R.id.statusDisplay);
        try {
            String info = getFriendData(displayName);
            JSONObject json = new JSONObject(info);
            JSONArray jArr = json.getJSONArray("friends");
            ArrayList<FriendEntry> friendList = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject friendOb = new JSONObject(jArr.getString(i));
                friendList.add(new FriendEntry(friendOb.getString("friendName"), friendOb.getString("nickname")));
            }
            FriendListAdapter friendListAdapter = new FriendListAdapter(this, R.layout.friend_entry_layout, friendList);
            friendListAdapter.setTab(tab);
            listView.setAdapter(friendListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(FriendActivity.this, FriendDataActivity.class);
                    i.putExtra("TAB", tab);
                    i.putExtra("FRIEND_NAME", friendList.get(position).getFriendName());
                    i.putExtra("NICKNAME", friendList.get(position).getNickname());
                    startActivity(i);
                }
            });
        } catch (IOException | JSONException e) {
            display.setText(e.getMessage());
        }
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(FriendActivity.this, FriendActivity.class));
    }

    public void goToFriendRequests(View v) throws IOException {
        Intent i = new Intent(FriendActivity.this, FriendActivity.class);
        i.putExtra("TAB", "FRIEND_REQUESTS");
        startActivity(i);
    }

    public void goToMyRequests(View v) throws IOException {
        Intent i = new Intent(FriendActivity.this, FriendActivity.class);
        i.putExtra("TAB", "MY_REQUESTS");
        startActivity(i);
    }

    public void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        Bundle extras = getIntent().getExtras();
        if (extras == null)
            tab = "FRIENDS";
        else
            tab = extras.getString("TAB");
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(FriendActivity.this, AccountActivity.class));
    }

    public void addFriend(View v) throws IOException {
        Intent intent = new Intent(FriendActivity.this, AddFriendActivity.class);
        intent.putExtra("TAB", tab);
        startActivity(intent);
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(FriendActivity.this, AddTimeActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(FriendActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(FriendActivity.this, CalendarActivity.class));
    }

    public String getFriendData(String displayName) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?userName=" + displayName + "&pending=0";

        if (tab.equals("FRIEND_REQUESTS"))
            url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?friendName=" + displayName + "&pending=1";
        else if (tab.equals("MY_REQUESTS"))
            url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?userName=" + displayName + "&pending=1";

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
