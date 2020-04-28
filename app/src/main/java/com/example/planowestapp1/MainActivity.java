package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button b;
    class Helper extends TimerTask
    {
        public void run()
        {
            b.setX(b.getX() + 2);
            b.setY(b.getY() + 2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer timer = new Timer();
        TimerTask task = new Helper();
        b = (Button) findViewById(R.id.button);
        timer.schedule(task, 2000, 10);
    }
}
