package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.trungvu.mychatapp.R;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolBarDangNhap;
    private TextInputEditText edtEmailDangNhap, edtMatKhauDangNhap;
    private Button btnDangNhap;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarDangNhap = findViewById(R.id.toolBarDangNhap);
        edtEmailDangNhap = findViewById(R.id.edtEmailDangNhap);
        edtMatKhauDangNhap = findViewById(R.id.edtMatKhauDangNhap);
        btnDangNhap = findViewById(R.id.btnDangNhap);
    }

    private void addEvents() {
        actionToolBar();

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangNhap();
            }
        });
    }

    // Tạo nút back button trên thanh ToolBar
    private void actionToolBar() {
        setSupportActionBar(toolBarDangNhap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set button back in toolbar

        toolBarDangNhap.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // đóng màn hình hiện tại
            }
        });
    }

    private void DangNhap() {
        String emailDangNhap = edtEmailDangNhap.getText().toString().trim();
        String passwordDangNhap = edtMatKhauDangNhap.getText().toString().trim();

        if (emailDangNhap.equals("") && passwordDangNhap.equals("")) {
            Toast.makeText(this, "Xin vui lòng không được bỏ trống thông tin nhập!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                mAuth.signInWithEmailAndPassword(emailDangNhap, passwordDangNhap).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Đóng màn hình Login hiện tại, nhảy sang màn hình Main
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Sai email hoặc mật khẩu xin vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Xin vui lòng nhập đầy đủ thông tin Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
