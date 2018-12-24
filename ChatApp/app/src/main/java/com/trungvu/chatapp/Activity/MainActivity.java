package com.trungvu.chatapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.Adapter.TabsPaperAdapter;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    ViewPager myViewPaper;
    TabLayout myTabLayout;
    FirebaseUser currentUser;
    DatabaseReference UsersReference;
    TabsPaperAdapter myTabsPaperAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser !=null){
            String online_user_id = mAuth.getCurrentUser().getUid();
            UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }

        addControls();
        addEvents();
    }

    private void addControls() {
        myViewPaper = findViewById(R.id.main_tabs_paper);
        myTabsPaperAdapter = new TabsPaperAdapter(getSupportFragmentManager(), this);
        myViewPaper.setAdapter(myTabsPaperAdapter);
        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPaper);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat App");
    }

    private void addEvents() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            LogOutUser();
        }else if (currentUser!=null) {
            UsersReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser!=null) {
            UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOutUser() {
        Intent startPageIntent = new Intent(MainActivity.this,StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.main_logout_button){
            if (currentUser !=null){
                UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            Toast.makeText(this, getString(R.string.logout_successful), Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            LogOutUser();
        }
        if (item.getItemId()==R.id.main_account_settings_button){
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if (item.getItemId()==R.id.main_all_users_button){
            Intent allUsersIntent = new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(allUsersIntent);
        }
        return true;
    }
}
