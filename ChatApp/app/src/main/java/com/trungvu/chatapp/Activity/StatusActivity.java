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
import com.trungvu.chatapp.R;

public class StatusActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText StatusInput;
    Button SaveChangesButton;
    DatabaseReference changeStatusRef;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        addControls();
        addEvents();
    }

    private void addControls() {
        toolbar = findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);
        String change_status_activity = getString(R.string.change_status_activity);
        getSupportActionBar().setTitle(change_status_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingBar = new ProgressDialog(this);
        StatusInput = findViewById(R.id.edt_status);
        SaveChangesButton = findViewById(R.id.btn_save_changes);

        String old_status = getIntent().getExtras().get("user_status").toString();
        StatusInput.setText(old_status);
        StatusInput.setSelection(StatusInput.getText().length()); // Place cursor at the end of text in EditText
    }

    private void addEvents() {
        SaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status = StatusInput.getText().toString();
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
            loadingBar.setTitle(getString(R.string.loading_bar_changes_profile_status));
            loadingBar.setMessage(getString(R.string.loading_bar_please_wait));
            loadingBar.show();
            changeStatusRef.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
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
