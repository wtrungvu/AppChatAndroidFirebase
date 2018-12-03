package com.trungvu.mychatapp.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trungvu.mychatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView txtTenHienThi, txtTrangThai;
    private Button btnThayDoiAvatar, btnCapNhatTrangThai;

    private FirebaseUser currentUser;

    // Firebase Realtime Database
    private DatabaseReference userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addControls();
        addEvents();
    }

    private void addControls() {
        circleImageView = findViewById(R.id.avatar_image);
        txtTenHienThi = findViewById(R.id.txtTenHienThi);
        txtTrangThai = findViewById(R.id.txtTrangThai);
        btnThayDoiAvatar = findViewById(R.id.btnThayDoiAvatar);
        btnCapNhatTrangThai = findViewById(R.id.btnCapNhatTrangThai);
    }

    private void addEvents() {
        // Lấy thông tin user đang đăng nhập hiện tại
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Lấy mã uid user đang đăng nhập hiện tại
        String current_uid = currentUser.getUid();

        // Thiết lập lấy dữ liệu theo cấu trúc cây đã tạo trong Firebase Realtime
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("AAA", dataSnapshot.toString());
                //Toast.makeText(SettingsActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                //--------------------------------------------
                // Tách chuỗi json (dataSnapshot) trong database lưu hết vào các biến
                String image = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                txtTenHienThi.setText(name);
                txtTrangThai.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnCapNhatTrangThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trạng thái hiện tại
                String status_value = txtTrangThai.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("status_value", status_value);
                startActivity(statusIntent);
            }
        });
    }
}
