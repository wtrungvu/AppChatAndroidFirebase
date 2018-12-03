package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trungvu.mychatapp.Adapter.SectionsPagerAdapter;
import com.trungvu.mychatapp.R;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolBarMain;

    private ViewPager viewPagerMain;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private TabLayout tabLayoutMain;

    private  FirebaseAuth mAuth;
    private  FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase Auth, Lấy user hiện tại
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarMain = findViewById(R.id.toolBarMain);

        // Tabs
        viewPagerMain = findViewById(R.id.viewPagerMain);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPagerMain.setAdapter(sectionsPagerAdapter);

        tabLayoutMain = findViewById(R.id.tabLayoutMain);
        tabLayoutMain.setupWithViewPager(viewPagerMain);
    }

    private void addEvents() {
        actionToolBar();
    }

    private void actionToolBar() {
        setSupportActionBar(toolBarMain);
        getSupportActionBar().setTitle("My App Chat");
    }

    // Tạo custom menu trên thanh ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Bắt sự kiện nếu ấn một item trên thanh ToolBar Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_logout_item){ // Menu -> Đăng Xuất
            DangXuat();
        }
        if(item.getItemId() == R.id.main_setting_item){ // Menu -> Thiết Lập Tài Khoản
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void DangXuat(){
        mAuth.signOut();
        finish(); // đóng màn hình Main
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
    }

}
