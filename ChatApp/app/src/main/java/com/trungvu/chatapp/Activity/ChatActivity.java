package com.trungvu.chatapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.trungvu.chatapp.Adapter.MessageAdapter;
import com.trungvu.chatapp.Model.AllUsers;
import com.trungvu.chatapp.Notification.APIService;
import com.trungvu.chatapp.Notification.Client;
import com.trungvu.chatapp.Notification.Data;
import com.trungvu.chatapp.Notification.MyResponse;
import com.trungvu.chatapp.Notification.Sender;
import com.trungvu.chatapp.Notification.Token;
import com.trungvu.chatapp.Ultil.LastSeenTime;
import com.trungvu.chatapp.Model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.trungvu.chatapp.R;
import com.trungvu.chatapp.Ultil.LastSeenTime_vi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton btn_Select_Image;
    ImageView emojiImageView;
    EmojiconEditText edt_Emojicon_Message;
    ImageButton btn_Send_Message;
    View rootView;

    String messageReceiverId;
    String messageReceiverName;

    // chat_custom_bar layout
    TextView display_user_name_chat_custom_bar;
    TextView last_seen_chat_custom_bar;
    CircleImageView profile_image_chat_custom_bar;

    DatabaseReference reference;

    FirebaseAuth auth;
    String messageSenderId;
    private static int Gallery_Pick = 1;
    RecyclerView recyclerView;

    List<Messages> messagesList;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;
    StorageReference MessageImageStorageRef;
    ProgressDialog progressDialog;

    // Notifications
    String userid;
    APIService apiService;
    boolean notify = false;

//    ValueEventListener seenListener_Sender;
//    DatabaseReference reference_Sender_Messages;
//
//    ValueEventListener seenListener_Receiver;
//    DatabaseReference reference_Receiver_Messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        messageSenderId = auth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();

        MessageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");

        // Notifications
        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        addControls();
        addEvents();
    }

    private void addControls() {
        createToolBar();

        progressDialog = new ProgressDialog(this);

        display_user_name_chat_custom_bar = findViewById(R.id.display_user_name_chat_custom_bar);
        last_seen_chat_custom_bar = findViewById(R.id.last_seen_chat_custom_bar);
        profile_image_chat_custom_bar = findViewById(R.id.profile_image_chat_custom_bar);

        rootView = findViewById(R.id.root_view);
        btn_Send_Message = findViewById(R.id.imageButton_Send_Message_ChatActivity);
        btn_Select_Image = findViewById(R.id.imageButton_select_image_ChatActivity);
        edt_Emojicon_Message = findViewById(R.id.emojiconEditText_message_ChatActivity);
        emojiImageView = findViewById(R.id.emojiImageView);

        EmojIconActions emojIcon = new EmojIconActions(this, rootView, edt_Emojicon_Message, emojiImageView);
        emojIcon.ShowEmojIcon();

        recyclerView = findViewById(R.id.recyclerView_ChatActivity);
        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList, getApplicationContext());

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);
    }

    private void createToolBar() {
        toolbar = findViewById(R.id.chat_bar_ChatActivity);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
    }

    private void addEvents() {
        FetchMessages();

        display_user_name_chat_custom_bar.setText(messageReceiverName);

        reference.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(getApplicationContext()).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                        .into(profile_image_chat_custom_bar, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(userThumb).placeholder(R.drawable.profile).into(profile_image_chat_custom_bar);
                            }
                        });

                if (online.equals("true")) {
                    last_seen_chat_custom_bar.setText(getString(R.string.Online));
                } else {
                    if (getString(R.string.locale).equals("Vietnamese")) {
                        LastSeenTime_vi getTime = new LastSeenTime_vi();
                        long last_seen = Long.parseLong(online);
                        String lastSeenDisplayTime = getTime.getTimeAgo(last_seen);
                        last_seen_chat_custom_bar.setText(lastSeenDisplayTime);
                    } else {
                        LastSeenTime getTime = new LastSeenTime();
                        long last_seen = Long.parseLong(online);
                        String lastSeenDisplayTime = getTime.getTimeAgo(last_seen);
                        last_seen_chat_custom_bar.setText(lastSeenDisplayTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_Send_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        btn_Select_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

//        SeenMessage();
    }

    private void FetchMessages() {
        reference.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage() {
        String messageText = edt_Emojicon_Message.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(ChatActivity.this, getString(R.string.Please_write_your_message), Toast.LENGTH_SHORT).show();
        } else {
            String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = reference.child("Messages")
                    .child(messageSenderId).child(messageReceiverId)
                    .push();

            String message_push_id = user_message_key.getKey();

            Date d = new Date();
            SimpleDateFormat sdf_currentHour = new SimpleDateFormat("HH:mm");
            String time = sdf_currentHour.format(d);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf_currentDate = new SimpleDateFormat("dd/MM/yyyy");
            String date = sdf_currentDate.format(calendar.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("seen", false);
            messageTextBody.put("type", "text");
            messageTextBody.put("timestamp", ServerValue.TIMESTAMP);
            messageTextBody.put("from", messageSenderId);
            messageTextBody.put("receiver", messageReceiverId);
            messageTextBody.put("time", time);
            messageTextBody.put("date", date);

            Map messageBodyDetail = new HashMap();
            messageBodyDetail.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBodyDetail.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            reference.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat_log", databaseError.getMessage().toString());
                    }
                    edt_Emojicon_Message.setText("");
                }
            });

            // Notifications
//            final String msg = messageText;
//            reference = FirebaseDatabase.getInstance().getReference("Users").child(messageSenderId);
//
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    AllUsers users = dataSnapshot.getValue(AllUsers.class);
//
//                    if (notify) {
//                        sendNotification(messageReceiverId, users.getUser_name(), msg);
//                    }
//
//                    notify = false;
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        }
    }

//    private void sendNotification(String receiver, final String user_name, final String message) {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Token token = snapshot.getValue(Token.class);
//
//                    Data data = new Data(messageSenderId
//                            , R.mipmap.ic_launcher
//                            , user_name + ": " + message
//                            , "New Message"
//                            , userid);
//
//                    Sender sender = new Sender(data, token.getToken());
//
//                    apiService.sendNotification(sender)
//                            .enqueue(new retrofit2.Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if (response.code() == 200) {
//                                        if (response.body().success != 1) {
//                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            progressDialog.setTitle(getString(R.string.Sending_chat_image));
            progressDialog.setMessage(getString(R.string.loading_bar_please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Uri ImageUri = data.getData();
            final String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
            final String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = reference.child("Messages").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = user_message_key.getKey();

            StorageReference filePath = MessageImageStorageRef.child(message_push_id + ".jpg");
            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        Date d = new Date();
                        SimpleDateFormat sdf_currentHour = new SimpleDateFormat("HH:mm");
                        String time = sdf_currentHour.format(d);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf_currentDate = new SimpleDateFormat("dd/MM/yyyy");
                        String date = sdf_currentDate.format(calendar.getTime());

                        Map messageTextBody_Sender_ref = new HashMap();
                        messageTextBody_Sender_ref.put("message", downloadUrl);
                        messageTextBody_Sender_ref.put("seen", false);
                        messageTextBody_Sender_ref.put("type", "image");
                        messageTextBody_Sender_ref.put("timestamp", ServerValue.TIMESTAMP);
                        messageTextBody_Sender_ref.put("from", messageSenderId);
                        messageTextBody_Sender_ref.put("receiver", messageReceiverId);
                        messageTextBody_Sender_ref.put("time", time);
                        messageTextBody_Sender_ref.put("date", date);

                        Map messageTextBody_Receiver_ref = new HashMap();
                        messageTextBody_Receiver_ref.put("message", downloadUrl);
                        messageTextBody_Receiver_ref.put("seen", false);
                        messageTextBody_Receiver_ref.put("type", "image");
                        messageTextBody_Receiver_ref.put("timestamp", ServerValue.TIMESTAMP);
                        messageTextBody_Receiver_ref.put("from", messageSenderId);
                        messageTextBody_Receiver_ref.put("receiver", messageReceiverId);
                        messageTextBody_Receiver_ref.put("time", time);
                        messageTextBody_Receiver_ref.put("date", date);

                        Map messageBodyDetail = new HashMap();
                        messageBodyDetail.put(message_sender_ref + "/" + message_push_id, messageTextBody_Sender_ref);
                        messageBodyDetail.put(message_receiver_ref + "/" + message_push_id, messageTextBody_Receiver_ref);

                        reference.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("Chat_Log", databaseError.getMessage().toString());
                                }
                                edt_Emojicon_Message.setText("");
                                progressDialog.dismiss();
                            }
                        });
                        Toast.makeText(ChatActivity.this, getString(R.string.Picture_sent_successfully), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(ChatActivity.this, getString(R.string.Picture_not_sent), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
}

//    private void SeenMessage() {
//        reference_Sender_Messages = reference.child("Messages").child(messageSenderId).child(messageReceiverId);
//
//        seenListener_Sender = reference_Sender_Messages.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot_Sender) {
//                for (DataSnapshot snapshot : dataSnapshot_Sender.getChildren()) {
//                    Messages messages_Sender = snapshot.getValue(Messages.class);
//
//                    if (messages_Sender.getFrom().equals(messageSenderId)
//                            && messages_Sender.getReceiver().equals(messageReceiverId)) {
//                        Map hashMap_Sender = new HashMap();
//                        hashMap_Sender.put("seen", true);
//                        snapshot.getRef().updateChildren(hashMap_Sender);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        reference_Receiver_Messages = reference.child("Messages").child(messageReceiverId).child(messageSenderId);
//        seenListener_Receiver = reference_Receiver_Messages.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot_Receiver) {
//                for (DataSnapshot snapshot : dataSnapshot_Receiver.getChildren()) {
//                    Messages messages_Receiver = snapshot.getValue(Messages.class);
//
//                    if (messages_Receiver.getFrom().equals(messageSenderId)
//                            && messages_Receiver.getReceiver().equals(messageReceiverId)) {
//                        Map hashMap_Receiver = new HashMap();
//                        hashMap_Receiver.put("seen", true);
//                        snapshot.getRef().updateChildren(hashMap_Receiver);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
////        reference_Sender_Messages.removeEventListener(seenListener_Sender);
////        reference_Receiver_Messages.removeEventListener(seenListener_Receiver);
//    }
