package com.trungvu.chatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;


public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edt_login_email, edt_login_password;
    Button btn_Login;
    TextView txt_forgot_your_password;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        progressDialog = new ProgressDialog(this);
        edt_login_email = findViewById(R.id.edt_login_email_LoginActivity);
        edt_login_password = findViewById(R.id.edt_login_password_LoginActivity);
        btn_Login = findViewById(R.id.login_button_LoginActivity);
        txt_forgot_your_password = findViewById(R.id.textView_ForgotYourPassword_LoginActivity);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_LoginActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.name_activity_login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_login_email.getText().toString();
                String password = edt_login_password.getText().toString();
                LoginUserAccount(email, password);
            }
        });

        txt_forgot_your_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Forgot = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent_Forgot);
            }
        });
    }

    private void LoginUserAccount(String email, String password) {
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.Please_enter_full_information), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, getString(R.string.please_write_your_email_login_activity), Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.please_write_your_password_login_activity), Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setTitle(getString(R.string.log_in_account_login_activity));
                progressDialog.setMessage(getString(R.string.loading_bar_please_wait));
                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String online_user_id = auth.getCurrentUser().getUid();

                                    Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.please_check_your_email_or_password_login_activity), Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        }
    }
}
