package com.trungvu.mychatapp.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trungvu.mychatapp.R;

import java.util.Random;

public class StatusActivity extends AppCompatActivity {
    private Toolbar toolBarStatus;
    private TextInputEditText txtTrangThaiThayDoi;
    private Button btnLuuThayDoiTrangThai;

    // Firebase Realtime Database
    private DatabaseReference userStatusDatabase;

    // Kiểm tra và lấy mã uid tài khoản đăng nhập hiện tại
    private FirebaseUser current_user;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Kiểm tra và lấy mã uid tài khoản đăng nhập hiện tại
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        // Thiết lập lấy dữ liệu theo cấu trúc cây đã tạo trong Firebase Realtime
        userStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarStatus = findViewById(R.id.toolBarStatus);
        txtTrangThaiThayDoi = findViewById(R.id.txtTrangThaiThayDoi);
        btnLuuThayDoiTrangThai = findViewById(R.id.btnLuuThayDoiTrangThai);

        // Dữ liệu trạng thái status cũ truyền Intent qua
        String status_value = getIntent().getStringExtra("status_value");
        txtTrangThaiThayDoi.setText(status_value);
    }

    private void addEvents() {
        actionToolBar();

        btnLuuThayDoiTrangThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Progress
                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Lưu thay đổi");
                progressDialog.setMessage("Đang lưu thay đổi, xin vui lòng chờ một chút!");
                progressDialog.show();

                String status = txtTrangThaiThayDoi.getText().toString().trim();
                userStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(StatusActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(StatusActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void actionToolBar() {
        setSupportActionBar(toolBarStatus);
        getSupportActionBar().setTitle("Trạng Thái Tài Khoản");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set button back in toolbar
    }

}
