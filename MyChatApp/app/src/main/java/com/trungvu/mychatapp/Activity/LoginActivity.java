package com.trungvu.mychatapp.Activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.trungvu.mychatapp.R;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolBarDangNhap;
    TextInputEditText edtEmailDangNhap, edtMatKhauDangNhap;
    Button btnDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

}
