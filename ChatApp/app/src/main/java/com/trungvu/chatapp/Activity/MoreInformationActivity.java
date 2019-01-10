package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.trungvu.chatapp.Model.AllUsers;
import com.trungvu.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreInformationActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView profile_image;
    TextView username, phone_number, sex, birthday, email, join_date;

    FirebaseAuth auth;
    DatabaseReference reference;

    Intent intent_Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_information);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();
        intent_Profile = getIntent();
        String visit_user_id = intent_Profile.getStringExtra("receiver_user_id");

        addControls();
        addEvents(visit_user_id);
    }

    private void addControls() {
        createToolBar();
        profile_image = findViewById(R.id.circleImageView_MoreInformationActivity);
        username = findViewById(R.id.textView_Username_MoreInformationActivity);
        phone_number = findViewById(R.id.textView_PhoneNumber_MoreInformationActivity);
        sex = findViewById(R.id.textView_Sex_MoreInformationActivity);
        birthday = findViewById(R.id.textView_Birthday_MoreInformationActivity);
        email = findViewById(R.id.textView_Email_MoreInformationActivity);
        join_date = findViewById(R.id.textView_JoinDate_MoreInformationActivity);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_MoreInformationActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.button_MoreInfo_ProfileActivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addEvents(String visit_user_id) {
        fetchInfo(visit_user_id);
    }

    private void fetchInfo(String visit_user_id) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(visit_user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllUsers users = dataSnapshot.getValue(AllUsers.class);

                Picasso.with(MoreInformationActivity.this).load(users.getUser_thumb_image()).placeholder(R.drawable.profile).into(profile_image);

                username.append(" " + users.getUser_name());

                if (users.getUser_phone_number().equals("default_phone_number")){
                    phone_number.append(" " + getString(R.string.not_set));
                } else {
                    phone_number.append(" " + users.getUser_phone_number());
                }

                if (users.getUser_sex().equals("male")){
                    sex.append(" " + getString(R.string.spinner_item_male));
                } else {
                    sex.append(" " + getString(R.string.spinner_item_fermale));
                }

                if (users.getUser_birthday().equals("default_birthday")){
                    birthday.append(" " + getString(R.string.not_set));
                } else {
                    birthday.append(" " + users.getUser_birthday());
                }

                email.append(" " + users.getUser_email());
                join_date.append(" " + users.getUser_join_date());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
