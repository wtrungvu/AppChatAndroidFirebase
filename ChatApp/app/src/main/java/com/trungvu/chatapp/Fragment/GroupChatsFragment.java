package com.trungvu.chatapp.Fragment;


import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trungvu.chatapp.Activity.CreateGroupsActivity;
import com.trungvu.chatapp.Model.Friends;
import com.trungvu.chatapp.Model.InfoGroups;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.GroupChatViewHolder;

import java.util.ArrayList;
import java.util.List;


public class GroupChatsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    List<InfoGroups> infoGroupsList;
    DatabaseReference reference_Groups_Info;

    DatabaseReference reference_Groups_List_Users;

    String online_user_id;
    String group_Chat_id;

    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_GroupChatsFragment);

        auth = FirebaseAuth.getInstance();
        online_user_id = auth.getCurrentUser().getUid();

        intent = getActivity().getIntent();

        try{
            group_Chat_id = getArguments().getString("group_Chat_id");

            reference_Groups_Info = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_Chat_id).child("Info");
            reference_Groups_Info.keepSynced(true);

            reference_Groups_List_Users = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_Chat_id).child("List_Users");
            reference_Groups_List_Users.keepSynced(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<InfoGroups,GroupChatViewHolder> adapter = new FirebaseRecyclerAdapter<InfoGroups, GroupChatViewHolder>(
                InfoGroups.class,
                R.layout.item_group_chats,
                GroupChatViewHolder.class,
                reference_Groups_Info
        ) {
            @Override
            protected void populateViewHolder(final GroupChatViewHolder viewHolder, InfoGroups model, final int position) {

                viewHolder.setNameGroupChats(model.getGroup_name());
                viewHolder.setCreateDate(model.getDate_created(), getContext());
                viewHolder.setCountMemberGroup(model.getCount_members(), getContext());
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
