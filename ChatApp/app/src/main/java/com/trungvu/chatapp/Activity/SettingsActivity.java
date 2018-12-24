package com.trungvu.chatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trungvu.chatapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    CircleImageView settingsDisplayProfileImage;
    TextView setttingsDisplayName, settingsDisplayStatus;
    Button setttingChangeProfileButton, settingChangeStatus;
    DatabaseReference getUserDataReference;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    Bitmap thumb_bitmap = null;
    StorageReference thumbImageRef;
    private final static int Gallery_Pick = 1;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();
        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getUserDataReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        addControls();
        addEvents();
    }

    private void addControls() {
        settingsDisplayProfileImage = findViewById(R.id.settings_profile_image);
        setttingsDisplayName = findViewById(R.id.settings_username);
        settingsDisplayStatus = findViewById(R.id.settings_user_status);
        setttingChangeProfileButton = findViewById(R.id.settings_change_profile_button);
        settingChangeStatus = findViewById(R.id.settings_change_status_button);
        loadingBar = new ProgressDialog(this);
    }

    private void addEvents() {
        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                setttingsDisplayName.setText(name);
                settingsDisplayStatus.setText(status);

                if (!image.equals("default_profile")) {
//
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile).into(settingsDisplayProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profile).into(settingsDisplayProfileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setttingChangeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        settingChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_status = settingsDisplayStatus.getText().toString();
                Intent changeStatus = new Intent(SettingsActivity.this, StatusActivity.class);
                changeStatus.putExtra("user_status", old_status);
                startActivity(changeStatus);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking if the selected image is true or not and the result is Ok or not.
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            //Instantiating the cropImage feature and setting the ratio in 1:1.
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        //Checking if the image is cropped or not
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            ///Checking if the result is Ok or not, if yes we will store the image in a uri.
            if (resultCode == RESULT_OK) {

                loadingBar.setTitle(getString(R.string.updating_profile_image_setting_activity));
                loadingBar.setMessage(getString(R.string.please_wait_image_setting_activity));
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                //Getting the Current UID of the User and storing it in a String.
                File thumb_filePathUri = new File(resultUri.getPath());

                //Saving the image in the Firebase Storage and naming the child with the UID.
                String user_id = mAuth.getCurrentUser().getUid();

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
                StorageReference filePath = storageReference.child(user_id + ".jpg");
                final StorageReference thumb_filePath = thumbImageRef.child(user_id + ".jpg");

                //We Will setup an OnCompleteListener to store the image in the desired location in the storage.
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, getString(R.string.saving_your_profile_image_setting_activity), Toast.LENGTH_LONG).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if (task.isSuccessful()) {
                                        Map update_user_data = new HashMap();
                                        update_user_data.put("user_image", downloadUrl);
                                        update_user_data.put("user_thumb_image", thumb_downloadUrl);

                                        getUserDataReference.updateChildren(update_user_data)
                                                .addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        loadingBar.dismiss();
                                                        Toast.makeText(SettingsActivity.this, getString(R.string.profile_image_updated_successfully_image_setting_activity), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });

                            getUserDataReference.child("user_image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SettingsActivity.this, getString(R.string.profile_image_updated_successfully_image_setting_activity), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(SettingsActivity.this, getString(R.string.image_saving_error_image_setting_activity), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
