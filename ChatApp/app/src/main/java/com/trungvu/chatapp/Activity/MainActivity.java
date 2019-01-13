package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.trungvu.chatapp.Fragment.ChatsFragment;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.Adapter.TabsPagerAdapter;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    TabsPagerAdapter tabsPagerAdapter;
    ViewPager viewPager;
    FloatingActionButton fab_CreateNewGroup;

    FirebaseAuth auth;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

//    int POSITION_TAB_ICON_CHATS = 0;
//    int POSITION_TAB_ICON_GROUPS = 1;
//    int POSITION_TAB_ICON_REQUEST = 2;
//    int POSITION_TAB_ICON_FRIENDS = 3;

    int POSITION_TAB_ICON_CHATS = 0;
    int POSITION_TAB_ICON_REQUEST = 1;
    int POSITION_TAB_ICON_FRIENDS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String online_user_id = auth.getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        viewPager = findViewById(R.id.viewPager_MainActivity);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsPagerAdapter);

        tabLayout = findViewById(R.id.tabLayout_MainActivity);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(POSITION_TAB_ICON_CHATS).setIcon(R.drawable.tab_icon_chats);
//        tabLayout.getTabAt(POSITION_TAB_ICON_GROUPS).setIcon(R.drawable.tab_icon_groups);
        tabLayout.getTabAt(POSITION_TAB_ICON_REQUEST).setIcon(R.drawable.tab_icon_request);
        tabLayout.getTabAt(POSITION_TAB_ICON_FRIENDS).setIcon(R.drawable.tab_icon_friends);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setSelectedTabIndicatorHeight((int) (tabsPagerAdapter.getCount() * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#ffffff"));

        fab_CreateNewGroup = findViewById(R.id.floatingActionButton_CreateNewGroup);
    }

    private void addEvents() {
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                if (position == POSITION_TAB_ICON_GROUPS) {
//                    fab_CreateNewGroup.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                fab_CreateNewGroup.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//
//        fab_CreateNewGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent_GroupChat = new Intent(MainActivity.this, CreateGroupsActivity.class);
//                startActivity(intent_GroupChat);
//            }
//        });
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_MainActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");
    }

    private void LogOutUser() {
        Intent intent_StartPage = new Intent(MainActivity.this, StartPageActivity.class);
        intent_StartPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent_StartPage);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button) {
            if (firebaseUser != null) {
                reference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            Toast.makeText(this, getString(R.string.logout_successful), Toast.LENGTH_SHORT).show();
            auth.signOut();
            LogOutUser();
        }
        if (item.getItemId() == R.id.main_account_settings_button) {
            Intent intent_Settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent_Settings);
        }
        if (item.getItemId() == R.id.main_all_users_button) {
            Intent intent_AllUsers = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(intent_AllUsers);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        String online_user_id = auth.getCurrentUser().getUid();
        DatabaseReference reference_Users_online = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

        if (firebaseUser == null) {
            LogOutUser();
        } else if (firebaseUser != null) {
            reference_Users_online.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseUser == null) {
            DatabaseReference reference_Users_online = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
            reference_Users_online.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

}
