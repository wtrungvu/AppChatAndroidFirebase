package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trungvu.mychatapp.R;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLAH_TIME_OUT = 3000; // Màn hình chào 3 giây khi mở app lên
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra xem người dùng có đăng nhập trước đó hay không?
        kiemTraTrangThaiTaiKhoan();
    }

    public void kiemTraTrangThaiTaiKhoan() {
        // Lấy User hiện tại
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        // null = chưa từng đăng nhập ứng dụng trước đó
        // not null = đã đăng nhập ứng dụng trước đó, chưa ấn nút đăng xuất

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Nếu đăng nhập trước đó rồi chuyển thẳng sang màn hình chính ứng dụng luôn (Main Activity)
                if (currentUser != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish(); // Đóng màn hình chào Splash Screen (Welcome Activity)
                } else { // Ngước lại , trước đó chưa đăng nhập thì chuyển sang màn hình tùy chọn đăng nhập & đăng ký (Start Activity)
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    finish(); // Đóng màn hình chào Splash Screen (Welcome Activity)
                }
            }
        }, SPLAH_TIME_OUT);
    }
}
