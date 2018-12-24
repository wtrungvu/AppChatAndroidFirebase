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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    EditText RegisterUserName,RegisterUserEmail,RegisterUserPassword;
    Button CreateAccountButton;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        mAuth = FirebaseAuth.getInstance();

        addControls();
        addEvents();
    }

    private void addControls() {
        mToolbar =findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.name_activity_register));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RegisterUserName = findViewById(R.id.edt_name_register);
        RegisterUserEmail =findViewById(R.id.edt_email_register);
        RegisterUserPassword = findViewById(R.id.edt_password_register);
        CreateAccountButton = findViewById(R.id.create_account_button);
        loadingBar = new ProgressDialog(this);
    }

    private void addEvents() {
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = RegisterUserName.getText().toString();
                String email = RegisterUserEmail.getText().toString();
                String password = RegisterUserPassword.getText().toString();
                RegisterAccount(name,email,password);
            }
        });
    }

    private void RegisterAccount(final String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.please_write_your_name_register_activity), Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.please_write_your_email_login_activity), Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.please_write_your_password_login_activity), Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle(getString(R.string.creating_new_account_register_activity));
            loadingBar.setMessage(getString(R.string.please_wait_image_setting_activity));
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                                String current_user_id = mAuth.getCurrentUser().getUid();

                                database = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                                database.child("user_name").setValue(name);
                                database.child("user_status").setValue("Xin chào! Tôi đang sử dụng Chat App, kết bạn với tôi nhé!");
                                database.child("user_image").setValue("default_profile");
                                database.child("device_token").setValue(DeviceToken);
                                database.child("user_thumb_image").setValue("default_image")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, getString(R.string.register_successful), Toast.LENGTH_SHORT).show();
                                                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, getString(R.string.error_register_activity), Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
