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

public class CreateAccountActivity extends AppCompatActivity {

    private EditText displayNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confPassInput;
    private TextView status;
    private View wrapper;

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

    public void goBack(View v) {
        startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
    }

    public void confirmAccount(View v) throws IOException, JSONException {
        displayNameInput = (EditText) findViewById(R.id.displayName);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        confPassInput = (EditText) findViewById(R.id.confPass);
        status = (TextView) findViewById(R.id.statusDisplay);
        wrapper = (View) findViewById(R.id.wrapper);
        if (!passwordInput.getText().toString().equals(confPassInput.getText().toString())) {
            status.setText("Sorry, your password and confirmation password do not match. Please try again.");
        } else {
            String displayNameStr = displayNameInput.getText().toString();
            String usernameStr = usernameInput.getText().toString();
            String passwordStr = passwordInput.getText().toString();
            if (displayNameStr.length() == 0 || usernameStr.length() == 0 || passwordStr.length() == 0) {
                wrapper.getLayoutParams().height = 900;
                wrapper.requestLayout();
                status.setText("Sorry, you must enter your display name, username, and password before creating an account. Please try again.");
                return;
            }
            String info = getAccountData(displayNameStr, usernameStr, passwordStr);
            JSONObject json = new JSONObject(info);
            if (json.getString("status").equals("displayName")) {
                wrapper.getLayoutParams().height = 910;
                wrapper.requestLayout();
                status.setText("Sorry, your display name is already taken. Please try again.");
            } else if (json.getString("status").equals("userPass")) {
                wrapper.getLayoutParams().height = 910;
                wrapper.requestLayout();
                status.setText("Sorry, another account has the same username and password. Please try again.");
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("NAME", displayNameStr);
                editor.apply();
                startActivity(new Intent(CreateAccountActivity.this, AccountActivity.class));
            }
        }
    }

    public static String getAccountData(String displayName, String user, String pass) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        user = user.replaceAll(" ", "%20");
        user = user.replaceAll("&", "%26");
        user = user.replaceAll("#", "%23");
        pass = pass.replaceAll(" ", "%20");
        pass = pass.replaceAll("&", "%26");
        pass = pass.replaceAll("#", "%23");

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
