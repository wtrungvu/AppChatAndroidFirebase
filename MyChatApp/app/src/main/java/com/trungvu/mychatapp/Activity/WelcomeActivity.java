package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trungvu.mychatapp.R;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLAH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLAH_TIME_OUT);
    }
}
