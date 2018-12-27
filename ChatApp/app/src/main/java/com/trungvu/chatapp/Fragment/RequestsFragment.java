package com.trungvu.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class RequestsFragment extends Fragment {
    RecyclerView myRequestList;
    View myMainView;
    DatabaseReference FriendsRequestReference;
    FirebaseAuth mAuth;
    String online_user_id;
    DatabaseReference UsersReference;

    DatabaseReference FriendsDatabaseRef;
    DatabaseReference FriendReqDatabaseRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestList = myMainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");


        myRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);

        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                Requests.class,
                R.layout.friend_request_all_users_layout,
                RequestViewHolder.class,
                FriendsRequestReference
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {
                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                final Button req_sent_btn = viewHolder.mView.findViewById(R.id.request_accept_btn);
                final Button req_decline_btn = viewHolder.mView.findViewById(R.id.request_decline_btn);

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String request_type = dataSnapshot.getValue().toString();
                            if (request_type.equals("received")) {
                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage, getContext());
                                        viewHolder.setUserStatus(userStatus);

                                        final CharSequence option[] = new CharSequence[]{
                                                getString(R.string.Accept_Friend_Request),
                                                getString(R.string.Cancel_Friend_Request)
                                        };

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAcceptAndCancel(option, list_user_id);
                                            }
                                        });

                                        req_sent_btn.setText(getString(R.string.Accept));
                                        req_sent_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogAcceptAndCancel(option, list_user_id);
                                            }
                                        });

                                        req_decline_btn.setText(getString(R.string.Cancel));
                                        req_decline_btn.setOnClickListener(new View.OnClickListener() {
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
                            }
                            else if (request_type.equals("sent")) {
                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("user_status").getValue().toString();
                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage, getContext());
                                        viewHolder.setUserStatus(userStatus);
                                        final CharSequence option[] = new CharSequence[]{
                                                getString(R.string.Cancel_Friend_Request)
                                        };
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogCancelFriendRequest(option, list_user_id);
                                            }
                                        });

                                        // Request Friend
                                        req_sent_btn.setText(getString(R.string.Request_sent)); // Request Sent Button Show

                                        req_sent_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogCancelFriendRequest(option, list_user_id);
                                            }
                                        });

                                        req_decline_btn.setVisibility(View.GONE); // Hide
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

                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
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
        myRequestList.setAdapter(adapter);
    }

    private void dialogAcceptAndCancel(CharSequence option[], final String list_user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Friend_request_option));
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    final String saveCurrentDate = currentDate.format(calForDate.getTime());

                    FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
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

                if (position == 1) {
                    FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
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
                    FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
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

}
