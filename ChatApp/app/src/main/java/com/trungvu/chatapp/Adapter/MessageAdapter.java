package com.trungvu.chatapp.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trungvu.chatapp.Model.Messages;
import com.trungvu.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    DatabaseReference UsersDatabaseReference;
    private FirebaseAuth mAuth;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent,false);
            mAuth = FirebaseAuth.getInstance();
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent,false);
            mAuth = FirebaseAuth.getInstance();
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        String message_sender_id = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        UsersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("user_name").getValue().toString();
                String userImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(holder.userProfileImage.getContext()).load(userImage)
                        .placeholder(R.drawable.profile).into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){
            holder.messagePicture.setVisibility(View.GONE); // Ẩn hẳn control
            if (fromUserId.equals(message_sender_id)){
                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.WHITE);
                //holder.messageText.setGravity(Gravity.LEFT);
            }
            else{
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                //holder.messageText.setGravity(Gravity.RIGHT);
            }
            holder.messageText.setText(messages.getMessage());
        }
        else{
            holder.messageText.setVisibility(View.GONE);
            holder.messageText.setPadding(0,0,0,0);

            Picasso.with(holder.userProfileImage.getContext()).load(messages.getMessage())
                    .placeholder(R.drawable.profile).into(holder.messagePicture);
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String message_sender_id = mAuth.getInstance().getCurrentUser().getUid();

        if (userMessagesList.get(position).getFrom().equals(message_sender_id)){
            return MSG_TYPE_LEFT;
        } else {
            return MSG_TYPE_RIGHT;
        }

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView messagePicture;
        public TextView messageText;
        public CircleImageView userProfileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            userProfileImage = itemView.findViewById(R.id.messages_image);
            messagePicture = itemView.findViewById(R.id.message_image_view);
        }
    }
}
