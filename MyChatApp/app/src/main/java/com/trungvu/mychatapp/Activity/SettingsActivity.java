package com.trungvu.mychatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trungvu.mychatapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView txtTenHienThi, txtTrangThai;
    private Button btnThayDoiAvatar, btnCapNhatTrangThai;

    private FirebaseUser currentUser;

    // Firebase Realtime Database
    private DatabaseReference userDatabase;

    private static final int GALLERY_PICK = 1;

    // Firebase Storage
    private StorageReference imageStorage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Firebase Storage
        imageStorage = FirebaseStorage.getInstance().getReference();

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

                // Load Data
                if (!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.drawable.img_avatar_default).into(circleImageView);
                }
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

        btnThayDoiAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                // Android Image Cropper
                // start picker to get image for cropping and then use the image in cropping activity
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                */
            }
        });
    }

    //Overriding a Method to get the result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking if the selected image is true or not and the result is Ok or not.
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            //Instantiating the cropImage feature and setting the ratio in 1:1.
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

            //Toast.makeText(this, (CharSequence) imageUri, Toast.LENGTH_SHORT).show();
        }

        //Checking if the image is cropped or not
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //Checking if the result is Ok or not, if yes we will store the image in a uri.
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Đang tải ảnh lên");
                progressDialog.setMessage("Xin vui lòng chờ một chút.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                //Getting the Current UID of the User and storing it in a String.
                final String current_user_id = currentUser.getUid();

                //Saving the image in the Firebase Storage and naming the child with the UID.
                StorageReference filePatch = imageStorage.child("profile_images").child(current_user_id + ".jpg");

                //We Will setup an OnCompleteListener to store the image in the desired location in the storage.
                filePatch.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        //If the task is Successful we will display a toast.
                        if (task.isSuccessful()) {
                            imageStorage.child("profile_images").child(current_user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String downloadUrl = uri.toString();

                                    userDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Lỗi khi tải ảnh lên!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //If the task is not successful then we will display an Error Message.
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    // Android random string generator
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
