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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private TextView status;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void createAccount(View v) throws IOException {
        startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
    }

    public void login(View v) throws IOException, JSONException {
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        status = (TextView) findViewById(R.id.statusDisplay);
        String usernameInputStr = usernameInput.getText().toString();
        String passwordInputStr = passwordInput.getText().toString();
        if (usernameInputStr.length() == 0 || passwordInputStr.length() == 0) {
            status.setText("Sorry, you must enter your username and password before logging in. Please try again.");
            return;
        }
        String info = getLoginData(usernameInputStr, passwordInputStr);
        JSONObject json = new JSONObject(info);
        if (json.getString("status").equals("notExist"))
            status.setText("Sorry, your username and password are not correct. Please try again.");
        else {
            displayName = json.getString("displayName");
            saveLoginData();
            startActivity(new Intent(LoginActivity.this, AccountActivity.class));
        }
    }

    public void saveLoginData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NAME", displayName);
        editor.apply();
    }

    public String getLoginData(String user, String pass) throws IOException {

        user = user.replaceAll(" ", "%20");
        user = user.replaceAll("&", "%26");
        user = user.replaceAll("#", "%23");
        pass = pass.replaceAll(" ", "%20");
        pass = pass.replaceAll("&", "%26");
        pass = pass.replaceAll("#", "%23");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/login/user?username=" + user + "&password=" + pass).openConnection();

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
