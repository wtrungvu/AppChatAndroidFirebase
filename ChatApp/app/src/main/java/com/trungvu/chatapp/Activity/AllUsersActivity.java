package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.trungvu.chatapp.Model.AllUsers;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.ViewHolder.AllUsersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUsersActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView_allUsersList;
    EditText edt_Search;
    ImageButton btn_Search;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();
        edt_Search = findViewById(R.id.editText_Search_AllUsersActivity);
        btn_Search = findViewById(R.id.button_Search_Friend_AllUsersActivity);

        recyclerView_allUsersList = findViewById(R.id.recyclerView_AllUsersActivity);
        recyclerView_allUsersList.setHasFixedSize(true);
        recyclerView_allUsersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolBar_AllUsersActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.all_users));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_Search = edt_Search.getText().toString();
                if (TextUtils.isEmpty(text_Search)) {
                    Toast.makeText(AllUsersActivity.this, getString(R.string.Enter_name_you_want_to_find), Toast.LENGTH_SHORT).show();
                } else {
                    SearchForPeopleAndFriend(text_Search.toLowerCase());
                }
            }
        });
    }

    private void SearchForPeopleAndFriend(String text_Search) {
        Query searchPeopleAndFriend = reference.orderByChild("user_name_lowercase")
                .startAt(text_Search)
                .endAt(text_Search + "\uf8ff");
        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> adapter = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(
                AllUsers.class,
                R.layout.all_users_display_layout,
                AllUsersViewHolder.class,
                searchPeopleAndFriend
        ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(getApplicationContext(), model.getUser_thumb_image());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent_Profile = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        intent_Profile.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent_Profile);
                    }
                });
            }
        };
        recyclerView_allUsersList.setAdapter(adapter);
    }



}
