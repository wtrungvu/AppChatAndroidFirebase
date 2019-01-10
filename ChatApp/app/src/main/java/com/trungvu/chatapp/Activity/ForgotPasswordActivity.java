package com.trungvu.chatapp.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edt_mail;
    Button btn_reset;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();
        edt_mail = findViewById(R.id.editText_ResetPassword_ForgotPasswordActivity);
        btn_reset = findViewById(R.id.Button_ResetPassword_ForgotPasswordActivity);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_ForgotPasswordActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.Forgot_Password_LoginActivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_mail.getText().toString().trim();

                if (email.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.Please_enter_full_information), Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.Toast_PleaseCheckYourEmail_ForgotPasswordActivity), Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
