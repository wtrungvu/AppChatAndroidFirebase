package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLAH_TIME_OUT = 3000;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StatusBarUtil.setTransparent(this);

        auth = FirebaseAuth.getInstance();

        checkUsers();
    }

    private void checkUsers() {
        final FirebaseUser currentUser = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, StartPageActivity.class));
                    finish();
                }
            }
        }, SPLAH_TIME_OUT);

    }

}
