package com.trungvu.chatapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trungvu.chatapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView profile_image;
    TextView username;
    TextView status;
    Button btn_Change_Image;
    Button btn_Change_Status;
    Button btn_Change_Info;

    // Storage Firebase
    StorageReference storageReference;
    StorageReference thumbImageRef;

    FirebaseAuth auth;
    DatabaseReference reference;

    Bitmap thumb_bitmap = null;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        StatusBarUtil.setTransparent(this); // Set StatusBar Color Transparent

        auth = FirebaseAuth.getInstance();

        String online_user_id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        reference.keepSynced(true);

        // Location Storage Firebase
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();
        profile_image = findViewById(R.id.profile_image_SettingsActivity);
        username = findViewById(R.id.username_SettingsActivity);
        status = findViewById(R.id.status_SettingsActivity);
        btn_Change_Image = findViewById(R.id.button_change_image_SettingsActivity);
        btn_Change_Status = findViewById(R.id.button_change_status_SettingsActivity);
        btn_Change_Info = findViewById(R.id.button_change_info_SettingsActivity);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.toolbar_SettingsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.main_account_settings_button));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addEvents() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String txt_name = dataSnapshot.child("user_name").getValue().toString();
                String txt_status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                username.setText(txt_name);
                status.setText(txt_status);
                if (!image.equals("default_profile")) {
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile).into(profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profile).into(profile_image);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_Change_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start cropping activity for pre-acquired image saved on the device
                //Instantiating the cropImage feature and setting the ratio in 1:1.
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start((Activity) SettingsActivity.this);
            }
        });

        btn_Change_Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_status = status.getText().toString();
                Intent changeStatus = new Intent(SettingsActivity.this, StatusActivity.class);
                changeStatus.putExtra("user_status", old_status);
                startActivity(changeStatus);
            }
        });

        btn_Change_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeInfo = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(changeInfo);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking if the selected image is true or not and the result is Ok or not.
        if (resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri ImageUri = data.getData();
        }

        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        //Checking if the image is cropped or not
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && result != null) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.updating_profile_image_setting_activity));
            progressDialog.setMessage(getString(R.string.please_wait_image_setting_activity));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Uri results_after_cropping_Uri = result.getUri();

            //Getting the Current UID of the User and storing it in a String.
            File thumb_filePathUri = new File(results_after_cropping_Uri.getPath());

            //Saving the image in the Firebase Storage and naming the child with the UID.
            String current_user_id = auth.getCurrentUser().getUid();

            try {
                thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_filePathUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] thumb_byte = baos.toByteArray();

            StorageReference filePath = storageReference.child(current_user_id + ".jpg");
            final StorageReference thumb_filePath = thumbImageRef.child(current_user_id + ".jpg");

            if (results_after_cropping_Uri != null) {
                //We Will setup an OnCompleteListener to store the image in the desired location in the storage.
                filePath.putFile(results_after_cropping_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task_Profile_Images) {
                        if (task_Profile_Images.isSuccessful()) {

                            //Toast.makeText(SettingsActivity.this, getString(R.string.saving_your_profile_image_setting_activity), Toast.LENGTH_LONG).show();
                            final String profile_Images_DownloadURL = task_Profile_Images.getResult().getDownloadUrl().toString();

                            // Thumb_Images
                            UploadTask uploadTask_Thumb_Images = thumb_filePath.putBytes(thumb_byte);
                            uploadTask_Thumb_Images.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_Images_DownloadURL = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("user_image", profile_Images_DownloadURL);
                                        update_hashMap.put("user_thumb_image", thumb_Images_DownloadURL);

                                        reference.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, getString(R.string.Success_Uploading), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, getString(R.string.Error_in_uploading_thumbnail), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, getString(R.string.Error_in_uploading), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(SettingsActivity.this, getString(R.string.No_image_selected), Toast.LENGTH_LONG).show();
            }

            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this, "error crop image: " + error, Toast.LENGTH_LONG).show();
                Log.d("CROP_IMAGE_ERROR_CODE", "error crop image: " + error);
            }
        }
    }

}
