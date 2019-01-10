package com.trungvu.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.trungvu.chatapp.Activity.ProfileActivity;
import com.trungvu.chatapp.Model.Requests;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.RequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RequestsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    String online_user_id;

    DatabaseReference reference_FriendsRequest_online_user_id;
    DatabaseReference reference_Users;
    DatabaseReference reference_Friends;
    DatabaseReference reference_FriendsRequest;

    CharSequence option[];

    public RequestsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_RequestsFragment);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        auth = FirebaseAuth.getInstance();
        online_user_id = auth.getCurrentUser().getUid();

        reference_FriendsRequest_online_user_id = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        reference_Users = FirebaseDatabase.getInstance().getReference().child("Users");
        reference_Friends = FirebaseDatabase.getInstance().getReference().child("Friends");
        reference_FriendsRequest = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                Requests.class,
                R.layout.friend_request_all_users_layout,
                RequestViewHolder.class,
                reference_FriendsRequest_online_user_id
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {
                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                final Button btn_accept = viewHolder.view.findViewById(R.id.button_accept_status_friend_request_all_users);
                final Button btn_decline = viewHolder.view.findViewById(R.id.button_deline_status_friend_request_all_users);

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String request_type = dataSnapshot.getValue().toString();
                            if (request_type.equals("received")) {
                                reference_Users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage, getContext());
                                        viewHolder.setUserStatus(userStatus);

                                        //  Check if the fragment is attached to the activity or not
                                        if (isAdded()){
                                            option = new CharSequence[]{
                                                    getString(R.string.See_profile),
                                                    getString(R.string.Accept_Friend_Request),
                                                    getString(R.string.Cancel_Friend_Request)
                                            };
                                        }

                                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAcceptAndCancel(option, list_user_id);
                                            }
                                        });

                                        if (isAdded()){
                                            btn_accept.setText(getString(R.string.Accept));
                                        }

                                        btn_accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogAcceptAndCancel(option, list_user_id);
                                            }
                                        });

                                        if (isAdded()){
                                            btn_decline.setText(getString(R.string.Cancel));
                                        }

                                        btn_decline.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogAcceptAndCancel(option, list_user_id);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (request_type.equals("sent")) {
                                reference_Users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage, getContext());
                                        viewHolder.setUserStatus(userStatus);

                                        if (isAdded()){
                                            option = new CharSequence[]{
                                                    getString(R.string.See_profile),
                                                    getString(R.string.Cancel_Friend_Request)
                                            };
                                        }

                                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogCancelFriendRequest(option, list_user_id);
                                            }
                                        });

                                        if (isAdded()){
                                            btn_accept.setText(getString(R.string.Request_sent));
                                        }

                                        btn_accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogCancelFriendRequest(option, list_user_id);
                                            }
                                        });

                                        btn_decline.setVisibility(View.GONE); // Hide
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                reference_Users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, getContext());
                        viewHolder.setUserStatus(userStatus);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void dialogAcceptAndCancel(CharSequence option[], final String list_user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Friend_request_option));
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent intent_profile = new Intent(getContext(), ProfileActivity.class);
                    intent_profile.putExtra("visit_user_id", list_user_id);
                    startActivity(intent_profile);
                }

                if (position == 1) {
                    Date d = new Date();
                    SimpleDateFormat sdf_currentHour = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf_currentHour.format(d);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf_currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf_currentDate.format(calendar.getTime());

                    final Map hashMap = new HashMap();
                    hashMap.put("time", time);
                    hashMap.put("date", date);

                    reference_Friends.child(online_user_id).child(list_user_id).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    reference_Friends.child(list_user_id).child(online_user_id).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    reference_FriendsRequest.child(online_user_id).child(list_user_id).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        reference_FriendsRequest.child(list_user_id).child(online_user_id).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), getString(R.string.friend_request_accepted_successfully), Toast.LENGTH_SHORT).show();
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

                if (position == 2) {
                    reference_FriendsRequest.child(online_user_id).child(list_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference_FriendsRequest.child(list_user_id).child(online_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), getString(R.string.friends_request_cancelled_successfully), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
        builder.show();
    }

    private void dialogCancelFriendRequest(CharSequence option[], final String list_user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Friend_request_sent));
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent intent_profile = new Intent(getContext(), ProfileActivity.class);
                    intent_profile.putExtra("visit_user_id", list_user_id);
                    startActivity(intent_profile);
                }

                if (position == 1) {
                    reference_FriendsRequest.child(online_user_id).child(list_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference_FriendsRequest.child(list_user_id).child(online_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), getString(R.string.friends_request_cancelled_successfully), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
        builder.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
