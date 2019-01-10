package com.trungvu.chatapp.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.Model.AllUsers;
import com.trungvu.chatapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edt_username;
    EditText edt_phone;
    EditText edt_birthday;

    Spinner spinner_sex;
    ArrayList<String> data_spinner_sex;
    String sex_selection;

    Button btn_save;

    FirebaseAuth auth;
    DatabaseReference reference;

    Intent intent_SettingsActivity;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();


        String user_id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        edt_username = findViewById(R.id.editText_userName_InfoActivity);

        edt_phone = findViewById(R.id.editText_Phone_InfoActivity);
        btn_save = findViewById(R.id.btn_Save_InfoActivity);

        spinner_sex = findViewById(R.id.spinner_Sex_InfoActivity);
        data_spinner_sex = new ArrayList<>();
        data_spinner_sex.add(getString(R.string.spinner_item_male));
        data_spinner_sex.add(getString(R.string.spinner_item_fermale));

        edt_birthday = findViewById(R.id.editText_Birthday_InfoActivity);

        ArrayAdapter<String> adapter_spinner = new ArrayAdapter<String>
                (
                        this,
                        R.layout.spinner_sex_item,
                        data_spinner_sex
                );
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner_sex.setAdapter(adapter_spinner);
        spinner_sex.setOnItemSelectedListener(new MyProcessEventSpinner());

        progressDialog = new ProgressDialog(this);
    }

    private class MyProcessEventSpinner implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0,
                                   View arg1,
                                   int arg2,
                                   long arg3) {
            if (data_spinner_sex.get(arg2).equals(getString(R.string.spinner_item_male))) {
                sex_selection = "male";
            }
            else {
                sex_selection = "fermale";
            }
        }

        // If user not select
        public void onNothingSelected(AdapterView<?> arg0) {
            sex_selection = "male";
        }
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_InfoActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.button_change_info_SettingsActivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addEvents() {
        FetchInfo();

        edt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectBirthday();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edt_username.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                String birthday = edt_birthday.getText().toString().trim();

                saveInfo(username, phone, sex_selection, birthday);
            }
        });
    }

    private void FetchInfo() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllUsers users = dataSnapshot.getValue(AllUsers.class);

                edt_username.setText(users.getUser_name());
                edt_username.setSelection(edt_username.getText().length());
                users.setUser_name_lowercase(edt_username.getText().toString().toLowerCase());

                if (users.getUser_phone_number().equals("default_phone_number")){
                    edt_phone.setText("");
                    edt_phone.setHint(getString(R.string.YouHaveNotSetAPhoneNumber_InfoActivity));
                } else {
                    edt_phone.setText(users.getUser_phone_number());
                    edt_phone.setSelection(edt_phone.getText().length());
                }

                if (users.getUser_sex().equals("male")){
                    spinner_sex.setSelection(0);
                } else {
                    spinner_sex.setSelection(1);
                }

                if (users.getUser_birthday().equals("default_birthday")){
                    edt_birthday.setText("");
                    edt_birthday.setHint(getString(R.string.YouHaveNotSetABirthDay_InfoActivity));
                } else {
                    edt_birthday.setText(users.getUser_birthday());
                    edt_birthday.setSelection(edt_birthday.getText().length());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveInfo(String username, String phone, String sex_selection, String birthday) {
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(sex_selection) && TextUtils.isEmpty(birthday)) {
            Toast.makeText(this, getString(R.string.Please_enter_full_information), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, getString(R.string.please_write_your_name_register_activity), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, getString(R.string.please_write_your_phone_InfoActivity), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(sex_selection)) {
                Toast.makeText(this, getString(R.string.please_update_your_sex_InfoActivity), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(birthday)) {
                Toast.makeText(this, getString(R.string.please_update_your_birthday_InfoActivity), Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setTitle(getString(R.string.Title_InfoActivity));
                progressDialog.setMessage(getString(R.string.please_wait_image_setting_activity));
                progressDialog.show();

                Map hashMap = new HashMap();
                hashMap.put("user_name", username);
                hashMap.put("user_phone_number", phone);
                hashMap.put("user_sex", sex_selection);
                hashMap.put("user_birthday", birthday);

                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(InfoActivity.this, getString(R.string.UpdateSuccessful_InfoActivity), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(InfoActivity.this, getString(R.string.error_status_activity), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
    }

    private void SelectBirthday() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(InfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edt_birthday.setText("");

                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edt_birthday.setText(simpleDateFormat.format(calendar.getTime()));
                edt_birthday.setSelection(edt_birthday.getText().length());
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}
