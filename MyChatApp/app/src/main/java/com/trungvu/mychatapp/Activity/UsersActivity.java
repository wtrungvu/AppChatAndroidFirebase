package com.trungvu.mychatapp.Activity;

import android.graphics.drawable.RippleDrawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trungvu.mychatapp.Model.Users;
import com.trungvu.mychatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar userToolbar;
    private RecyclerView userList;

    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        addControls();
        addEvents();
    }

    private void addControls() {
        userToolbar = findViewById(R.id.usersToolBar);
        userList = findViewById(R.id.usersList);
    }

    private void addEvents() {
        actionToolBar();

        userList.setHasFixedSize(true);
        userList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void actionToolBar() {
        setSupportActionBar(userToolbar);
        getSupportActionBar().setTitle("Xem Người Dùng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(usersDatabase, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users users) {
                holder.setName(users.getName());
                holder.setStatus(users.getStatus());
                holder.setImage(users.getImage());
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_item, parent, false);
                return new UsersViewHolder(view);
            }
        };

        userList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView userNameView = view.findViewById(R.id.usersSingleName);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView userStatus = view.findViewById(R.id.usersSingleStatus);
            userStatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView userImage = view.findViewById(R.id.usersSingleImage);
            Picasso.get().load(image).into(userImage);
        }

    }

}
