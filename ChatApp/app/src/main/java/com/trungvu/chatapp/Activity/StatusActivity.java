package com.trungvu.chatapp.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaeger.library.StatusBarUtil;
import com.trungvu.chatapp.R;

public class StatusActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edt_status;
    Button btn_Save_Changes;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        progressDialog = new ProgressDialog(this);
        edt_status = findViewById(R.id.editText_StatusActivity);
        btn_Save_Changes = findViewById(R.id.button_save_changes_StatusActivity);

        String old_status = getIntent().getExtras().get("user_status").toString();
        edt_status.setText(old_status);
        edt_status.setSelection(edt_status.getText().length()); // Place cursor at the end of text in EditText
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.app_bar_StatusActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.change_status_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        btn_Save_Changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status = edt_status.getText().toString();
                ChangeProfileStatus(new_status);
            }
        });
    }

    private void ChangeProfileStatus(String new_status) {
        if (TextUtils.isEmpty(new_status)){
            String please_write_status_activity = getString(R.string.please_write_status_activity);
            Toast.makeText(this, please_write_status_activity , Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle(getString(R.string.loading_bar_changes_profile_status));
            progressDialog.setMessage(getString(R.string.loading_bar_please_wait));
            progressDialog.show();

            reference.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(StatusActivity.this, getString(R.string.loading_bar_profile_status_updated_successfully), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(StatusActivity.this, getString(R.string.error_status_activity), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
