package com.trungvu.chatapp.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trungvu.chatapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public RequestViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setUserName(String userName) {
        TextView userNameDisplay = view.findViewById(R.id.username_friend_request_all_users);
        userNameDisplay.setText(userName);
    }

    public void setUserStatus(String userStatus) {
        TextView status = view.findViewById(R.id.status_friend_request_all_users);
        status.setText(userStatus);
    }

    public void setThumbImage(final String thumbImage, final Context ctx) {
        final CircleImageView thumb_image = view.findViewById(R.id.profile_image_friend_request_all_users);

        Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                .into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.profile).into(thumb_image);
                    }
                });
    }

}
