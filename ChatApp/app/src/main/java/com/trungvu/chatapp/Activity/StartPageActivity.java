package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

public class StartPageActivity extends AppCompatActivity {
    Button btn_already_have_account;
    Button btn_need_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        addControls();
        addEvents();
    }

    private void addControls() {
        btn_already_have_account = findViewById(R.id.button_already_have_account_StartPageActivity);
        btn_need_account = findViewById(R.id.button_need_account_button_StartPageActivity);
    }

    private void addEvents() {
        btn_already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        btn_need_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartPageActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
