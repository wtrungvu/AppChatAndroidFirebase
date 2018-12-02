package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trungvu.mychatapp.R;

public class MainActivity extends AppCompatActivity {
    private TextView txtEmail;
    private Button btnDangXuat;

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
        txtEmail = findViewById(R.id.txtEmail);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }

    private void addEvents() {
        txtEmail.setText(user.getEmail());

        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangXuat();
            }
        });
    }

    public void DangXuat(){
        mAuth.signOut();
        finish(); // đóng màn hình Main
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
    }

}
