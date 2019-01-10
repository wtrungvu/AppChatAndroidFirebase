package com.trungvu.chatapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.iid.FirebaseInstanceId;
import com.trungvu.chatapp.Activity.ChatActivity;
import com.trungvu.chatapp.Model.Chats;
import com.trungvu.chatapp.Notification.Token;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.ChatsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;



public class ChatsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    DatabaseReference reference_Friends;
    DatabaseReference reference_User;

    String online_user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_ChatsFragment);

        auth = FirebaseAuth.getInstance();
        online_user_id = auth.getCurrentUser().getUid();

        reference_Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        reference_User = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Chats, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(
                Chats.class,
                R.layout.all_users_display_layout,
                ChatsViewHolder.class,
                reference_Friends
        ) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {
                final String list_user_id = getRef(position).getKey();
                reference_User.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String online_status = (String) dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_status);
                        }
                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, getContext());
                        viewHolder.setUserStatus(userStatus);
                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dataSnapshot.child("online").exists()) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", list_user_id);
                                    chatIntent.putExtra("user_name", userName);
                                    startActivity(chatIntent);
                                } else {
                                    reference_User.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id", list_user_id);
                                                    chatIntent.putExtra("user_name", userName);
                                                    startActivity(chatIntent);
                                                }
                                            });
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

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(online_user_id).setValue(token1);
    }

}
