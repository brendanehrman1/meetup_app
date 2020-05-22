package com.example.planowestapp1;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity {
    private static final String PREF_NAME = "sharedPrefs";

    private EditText nameInput;
    private TextView output;
    private String input;
    private int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        nameInput = (EditText) findViewById(R.id.nameInput);
        output = (TextView) findViewById(R.id.output);
        loadData();
        if (nameInput != null) {
            try {
                displayMessage(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void displayMessage(View v) throws IOException {
        if (input == null)
            input = nameInput.getText().toString();
        String info = getPersonData(input);
        try {
            JSONObject json = new JSONObject(info);
            output.setText(json.getString("username") + "'s ID is " + json.getString("userid") + ".");
            saveData();
        } catch(Exception e) {
            output.setText("Your user does not exist");
        }
        input = null;
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NAME", input);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        input = sharedPreferences.getString("NAME", null);
    }

    public static String getPersonData(String name) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/planowestapp1_webservice/people/" + name).openConnection();

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