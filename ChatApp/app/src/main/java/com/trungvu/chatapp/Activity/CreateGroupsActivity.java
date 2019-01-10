package com.trungvu.chatapp.Activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trungvu.chatapp.Model.Friends;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.ListUserFriendsViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateGroupsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText txt_groupName;
    RecyclerView recyclerView;
    Button btn_Create_Group_Chat;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference_Friends;
    DatabaseReference reference_User;
    String online_user_id;
    String group_Chat_id;

    List<String> list_add_user_id_Group = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_groups);

        auth = FirebaseAuth.getInstance();
        online_user_id = auth.getCurrentUser().getUid();

        reference_Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        reference_Friends.keepSynced(true);

        reference_User = FirebaseDatabase.getInstance().getReference().child("Users");
        reference_User.keepSynced(true);

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();
        toolbar = findViewById(R.id.toolBar_CreateGroupsActivity);
        txt_groupName = findViewById(R.id.groupName_CreateGroupsActivity);
        btn_Create_Group_Chat = findViewById(R.id.button_Create_Group_Chat);

        recyclerView = findViewById(R.id.recyclerView_CreateGroupsActivity);
        CreateAdapterRecyclerView();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolBar_CreateGroupsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create A New Group Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addEvents() {
        btn_Create_Group_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_GroupName = txt_groupName.getText().toString();

                if (TextUtils.isEmpty(txt_GroupName)) {
                    Toast.makeText(CreateGroupsActivity.this, getString(R.string.Please_enter_full_information), Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference reference_Groups_Info = FirebaseDatabase.getInstance().getReference().child("Groups").push();
                    group_Chat_id = reference_Groups_Info.getKey();

                    Date d = new Date();
                    SimpleDateFormat sdf_currentHour = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf_currentHour.format(d);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf_currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf_currentDate.format(calendar.getTime());

                    Map hashMap_Info = new HashMap();
                    hashMap_Info.put("group_name", txt_GroupName);
                    hashMap_Info.put("date_created", date);
                    hashMap_Info.put("time_created", time);
                    hashMap_Info.put("ount_Members", list_add_user_id_Group.size());

                    reference_Groups_Info.child("Info").setValue(hashMap_Info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                                Intent intent = new Intent(CreateGroupsActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("group_Chat_id", group_Chat_id);
                                startActivity(intent);
                                Toast.makeText(CreateGroupsActivity.this, "Create Group Successfull!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    DatabaseReference reference_Group_ListUsers = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_Chat_id);

                    Map hashMap_ListUsers = new HashMap();
                    hashMap_ListUsers.put("host_user_id", online_user_id);

                    for (int i = 0; i < list_add_user_id_Group.size(); i++){
                        hashMap_ListUsers.put("user_id_" + i, list_add_user_id_Group.get(i));
                    }

                    reference_Group_ListUsers.child("List_Users").setValue(hashMap_ListUsers);

                }
            }
        });
    }

    private void CreateAdapterRecyclerView() {
        FirebaseRecyclerAdapter<Friends, ListUserFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, ListUserFriendsViewHolder>(
                Friends.class,
                R.layout.item_list_user_friends,
                ListUserFriendsViewHolder.class,
                reference_Friends
        ) {
            @Override
            protected void populateViewHolder(final ListUserFriendsViewHolder viewHolder, Friends model, final int position) {
                viewHolder.setDate(model.getDate(), CreateGroupsActivity.this);
                final String user_id_position = getRef(position).getKey();

                reference_User.child(user_id_position).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, CreateGroupsActivity.this);

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (viewHolder.getCheckBoxSelectedItem() == false) {
                                    viewHolder.setCheckBoxSelectedItem(true);
                                    list_add_user_id_Group.add(user_id_position);
                                    Log.d("AAA", String.valueOf(list_add_user_id_Group.size()));
                                } else {
                                    viewHolder.setCheckBoxSelectedItem(false);
                                    for (int i = 0; i < list_add_user_id_Group.size(); i++) {
                                        if (user_id_position.equals(list_add_user_id_Group.get(i))) {
                                            list_add_user_id_Group.remove(i);
                                        }
                                    }
                                    Log.d("AAA-2", String.valueOf(list_add_user_id_Group.size()));
                                }
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }


}
