package com.trungvu.mychatapp.Activity;

import android.content.Intent;
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
import com.trungvu.mychatapp.R;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolBarMain;
    private TextView txtEmail;

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
        txtEmail = findViewById(R.id.txtEmail);
    }

    private void addEvents() {
        txtEmail.setText(user.getEmail());
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

    // Bắt sự kiện nếu ấn một item trên thanh ToolBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_logout_item){
            DangXuat();
        }
        return super.onOptionsItemSelected(item);
    }

    public void DangXuat(){
        mAuth.signOut();
        finish(); // đóng màn hình Main
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
    }

}
