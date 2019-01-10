package com.trungvu.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trungvu.chatapp.Activity.ChatActivity;
import com.trungvu.chatapp.Model.Friends;
import com.trungvu.chatapp.Activity.ProfileActivity;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.FriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


public class FriendsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    DatabaseReference reference_Friends;
    DatabaseReference reference_User;
    String online_user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_FriendsFragment);

        auth = FirebaseAuth.getInstance();
        online_user_id = auth.getCurrentUser().getUid();

        reference_Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        reference_Friends.keepSynced(true);
        reference_User = FirebaseDatabase.getInstance().getReference().child("Users");
        reference_User.keepSynced(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                FriendsViewHolder.class,
                reference_Friends
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate(), getContext());
                final String list_user_id = getRef(position).getKey();

                reference_User.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String online_status = (String) dataSnapshot.child("online").getValue().toString();

                            viewHolder.setUserOnline(online_status);
                        }

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, getContext());

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[] = new CharSequence[]{
                                        getString(R.string.See_profile),
                                        getString(R.string.Send_Message)
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(getString(R.string.Select_option));
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        if (position == 0) {
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if (position == 1) {
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
                                    }
                                });
                                builder.show();
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
