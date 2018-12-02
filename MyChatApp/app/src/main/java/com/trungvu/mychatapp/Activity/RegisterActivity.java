package com.trungvu.mychatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);
    }

    private void addEvents() {
        actionToolBar();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenHienThiDangKy = edtTenHienThiDangKy.getText().toString().trim();
                String emailDangKy = edtEmailDangKy.getText().toString().trim();
                String matKhauDangKy = edtMatKhauDangKy.getText().toString().trim();

                if (!TextUtils.isEmpty(tenHienThiDangKy) || !TextUtils.isEmpty(emailDangKy) || !TextUtils.isEmpty(matKhauDangKy)){
                    progressDialog.setTitle("Đăng ký tài khoản");
                    progressDialog.setMessage("Hệ thống đang đăng ký tài khoản của bạn, Xin vui lòng đợi một chút!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    DangKy(tenHienThiDangKy, emailDangKy, matKhauDangKy);
                } else {
                    Toast.makeText(RegisterActivity.this, "Xin vui lòng nhập thông tin đầy đủ!", Toast.LENGTH_SHORT).show();
                }
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

    private void DangKy(String tenHienThiDangKy, String emailDangKy, String matKhauDangKy) {
        if (tenHienThiDangKy.equals("") && emailDangKy.equals("") && matKhauDangKy.equals("")) {
            progressDialog.hide();
            Toast.makeText(this, "Xin vui lòng không được bỏ trống thông tin nhập!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                mAuth.createUserWithEmailAndPassword(emailDangKy, matKhauDangKy)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);

                                    finish(); // Đóng màn hình Đăng ký hiện tại, nhảy sang màn hình Main
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                } else {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Email đã có người đăng ký, xin vui lòng đăng ký email khác!", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            } catch (Exception e) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), "Xin vui lòng nhập đầy đủ thông tin Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
