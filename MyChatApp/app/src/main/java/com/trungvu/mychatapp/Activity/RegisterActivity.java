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
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trungvu.mychatapp.R;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolBarDangKy;
    private TextInputEditText edtTenHienThiDangKy, edtEmailDangKy, edtMatKhauDangKy;
    private Button btnDangKy;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarDangKy = findViewById(R.id.toolBarDangKy);
        edtTenHienThiDangKy = findViewById(R.id.edtTenHienThiDangKy);
        edtEmailDangKy = findViewById(R.id.edtEmailDangKy);
        edtMatKhauDangKy = findViewById(R.id.edtMatKhauDangKy);
        btnDangKy = findViewById(R.id.btnDangKy);
    }

    private void addEvents() {
        actionToolBar();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangKy();
            }
        });
    }

    // Tạo nút back button trên thanh ToolBar
    private void actionToolBar() {
        setSupportActionBar(toolBarDangKy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set button back in toolbar

        toolBarDangKy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // đóng màn hình hiện tại
            }
        });
    }

    private void DangKy() {
        String tenHienThiDangKy = edtTenHienThiDangKy.getText().toString().trim();
        String emailDangKy = edtEmailDangKy.getText().toString().trim();
        final String matKhauDangKy = edtMatKhauDangKy.getText().toString().trim();

        if (tenHienThiDangKy.equals("") && emailDangKy.equals("") && matKhauDangKy.equals("")) {
            Toast.makeText(this, "Xin vui lòng không được bỏ trống thông tin nhập!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                mAuth.createUserWithEmailAndPassword(emailDangKy, matKhauDangKy)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);

                                    finish(); // Đóng màn hình Đăng ký hiện tại, nhảy sang màn hình Main
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Email đã có người đăng ký, xin vui lòng đăng ký email khác!", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Xin vui lòng nhập đầy đủ thông tin Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
