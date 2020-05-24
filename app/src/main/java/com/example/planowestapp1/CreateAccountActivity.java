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

public class CreateAccountActivity extends AppCompatActivity {

    private EditText displayNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confPassInput;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public void confirmAccount(View v) throws IOException, JSONException {
        displayNameInput = (EditText) findViewById(R.id.displayName);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        confPassInput = (EditText) findViewById(R.id.confPass);
        status = (TextView) findViewById(R.id.statusDisplay);
        if (!passwordInput.getText().toString().equals(confPassInput.getText().toString())) {
            status.setText("Sorry, your password and confirmation password do not match. Please try again.");
        } else {
            String info = getAccountData(displayNameInput.getText().toString(),
                    usernameInput.getText().toString(), passwordInput.getText().toString());
            JSONObject json = new JSONObject(info);
            if (json.getString("status").equals("displayName")) {
                status.setText("Sorry, you display name is already taken. Please try again.");
            } else if (json.getString("status").equals("userPass")) {
                status.setText("Sorry, another account has the same username and password. Please try again.");
            } else {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            }
        }
    }

    public static String getAccountData(String displayName, String user, String pass) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/login/add?displayName=" + displayName + "&username=" + user + "&password=" + pass).openConnection();

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
