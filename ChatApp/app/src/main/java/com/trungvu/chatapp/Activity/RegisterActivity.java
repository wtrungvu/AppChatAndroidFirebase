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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText username;
    EditText email;
    EditText RegisterUserPassword;

    Button btn_Create_Account;
    ProgressDialog loadingBar;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        username = findViewById(R.id.editText_username_RegisterActivity);
        email = findViewById(R.id.editText_email_RegisterActivity);
        RegisterUserPassword = findViewById(R.id.editText_password_RegisterActivity);
        btn_Create_Account = findViewById(R.id.button_create_account_RegisterActivity);
        loadingBar = new ProgressDialog(this);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_RegisterActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.name_activity_register));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        btn_Create_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = RegisterUserPassword.getText().toString();

                RegisterAccount(txt_username, txt_email, txt_password);
            }
        });
    }

    private void RegisterAccount(final String username, final String email, String password) {
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.Please_enter_full_information), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, getString(R.string.please_write_your_name_register_activity), Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, getString(R.string.please_write_your_email_login_activity), Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.please_write_your_password_login_activity), Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle(getString(R.string.creating_new_account_register_activity));
                loadingBar.setMessage(getString(R.string.please_wait_image_setting_activity));
                loadingBar.show();

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String error = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            error = getString(R.string.Weak_Password);
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = getString(R.string.Invalid_Email);
                        } catch (FirebaseAuthUserCollisionException e) {
                            error = getString(R.string.Existing_Account);
                        } catch (Exception e) {
                            error = getString(R.string.error_register_activity);
                            //e.printStackTrace();
                        }

                        if (task.isSuccessful()) {
                            String current_user_id = auth.getCurrentUser().getUid();

                            Date d = new Date();
                            SimpleDateFormat sdf_currentHour = new SimpleDateFormat("HH:mm");
                            String join_time = sdf_currentHour.format(d);

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf_currentDate = new SimpleDateFormat("dd/MM/yyyy");
                            String join_date = sdf_currentDate.format(calendar.getTime());

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                            Map hashMap = new HashMap();
                            hashMap.put("user_name", username);
                            hashMap.put("user_status", "Xin chào! Tôi đang sử dụng Chat App, kết bạn với tôi nhé!");
                            hashMap.put("user_image", "default_profile");
                            hashMap.put("user_thumb_image", "default_image");
                            hashMap.put("user_email", email);
                            hashMap.put("user_sex", "male");
                            hashMap.put("user_phone_number", "default_phone_number");
                            hashMap.put("user_birthday", "default_birthday");
                            hashMap.put("user_name_lowercase", username.toLowerCase());
                            hashMap.put("user_join_date", join_date);
                            hashMap.put("user_join_time", join_time);

                            reference.setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, getString(R.string.register_successful), Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }
                                    });
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
