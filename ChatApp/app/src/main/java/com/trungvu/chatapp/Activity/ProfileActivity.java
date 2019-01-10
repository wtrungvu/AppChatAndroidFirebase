package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.trungvu.chatapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView profile_image;
    TextView profile_username;
    TextView profile_status;
    Button btn_SendFriendRequest;
    Button btn_DeclineFriendRequest;
    Button btn_MoreInformation;

    FirebaseAuth auth;
    DatabaseReference reference_User;
    DatabaseReference reference_FriendRequest;
    DatabaseReference reference_Friends;

    String CURRENT_STATE;
    String sender_user_id;
    String receiver_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();
        sender_user_id = auth.getCurrentUser().getUid();

        reference_User = FirebaseDatabase.getInstance().getReference().child("Users");
        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        reference_FriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        reference_FriendRequest.keepSynced(true);

        reference_Friends = FirebaseDatabase.getInstance().getReference().child("Friends");
        reference_Friends.keepSynced(true);

        CURRENT_STATE = "not_friends";

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();
        btn_MoreInformation = findViewById(R.id.button_MoreInfo_ProfileActivity);
        btn_SendFriendRequest = findViewById(R.id.button_SendFriendRequest_ProfileActivity);
        btn_DeclineFriendRequest = findViewById(R.id.button_DeclineFriendRequest_ProfileActivity);
        profile_image = findViewById(R.id.imageView_Visit_User_ProfileActivity);
        profile_username = findViewById(R.id.username_Visit_User_ProfileActivity);
        profile_status = findViewById(R.id.status_Visit_User_ProfileActivity);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_ProfileActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addEvents() {
        reference_User.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profile_username.setText(name);
                profile_status.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile).into(profile_image);

                reference_FriendRequest.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiver_user_id)) {

                            String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("sent")) {
                                CURRENT_STATE = "request_sent";
                                btn_SendFriendRequest.setText(getString(R.string.cancel_friend_request));
                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btn_DeclineFriendRequest.setEnabled(false);
                            } else if (req_type.equals("received")) {
                                CURRENT_STATE = "request_received";
                                btn_SendFriendRequest.setText(getString(R.string.accept_friend_request));
                                btn_DeclineFriendRequest.setVisibility(View.VISIBLE);
                                btn_DeclineFriendRequest.setEnabled(true);
                                btn_DeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DeclineFriendRequest();
                                    }
                                });
                            }
                        } else {
                            reference_Friends.child(sender_user_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiver_user_id)) {
                                                CURRENT_STATE = "friends";
                                                btn_SendFriendRequest.setText(getString(R.string.unfriend_this_person));

                                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                btn_DeclineFriendRequest.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
        btn_DeclineFriendRequest.setEnabled(false);

        if (!sender_user_id.equals(receiver_user_id)) {
            btn_SendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_SendFriendRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToAPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")) {
                        UnFriendaFriend();
                    }
                }
            });
        } else {
            btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
            btn_SendFriendRequest.setVisibility(View.INVISIBLE);
        }

        btn_MoreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MoreInformationActivity.class);
                intent.putExtra("receiver_user_id", receiver_user_id);
                startActivity(intent);
            }
        });
    }


    private void DeclineFriendRequest() {
        reference_FriendRequest.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference_FriendRequest.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btn_SendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                btn_SendFriendRequest.setText(getString(R.string.profile_visit_send_req_btn));
                                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                btn_DeclineFriendRequest.setEnabled(false);


                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void UnFriendaFriend() {
        reference_Friends.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference_Friends.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btn_SendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                btn_SendFriendRequest.setText(getString(R.string.profile_visit_send_req_btn));
                                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                btn_DeclineFriendRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        reference_Friends.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference_Friends.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference_FriendRequest.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            reference_FriendRequest.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                btn_SendFriendRequest.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                btn_SendFriendRequest.setText(getString(R.string.unfriend_this_person));
                                                                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                                                btn_DeclineFriendRequest.setEnabled(false);

                                                                            }
                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void CancelFriendRequest() {
        reference_FriendRequest.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference_FriendRequest.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btn_SendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                btn_SendFriendRequest.setText(getString(R.string.profile_visit_send_req_btn));
                                                btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                btn_DeclineFriendRequest.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void SendFriendRequestToAPerson() {
        reference_FriendRequest.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference_FriendRequest.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("from", sender_user_id);
                                                hashMap.put("type", "request");

                                                if (task.isSuccessful()) {
                                                    btn_SendFriendRequest.setEnabled(true);
                                                    CURRENT_STATE = "request_sent";
                                                    btn_SendFriendRequest.setText(getString(R.string.cancel_friend_request));

                                                    btn_DeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                    btn_DeclineFriendRequest.setEnabled(false);
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
