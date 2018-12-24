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
    public View mView;
    public RequestViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setUserName(String userName) {
        TextView userNameDisplay = mView.findViewById(R.id.request_profile_name);
        userNameDisplay.setText(userName);
    }

    public void setThumbImage(final String thumbImage, final Context ctx) {
        final CircleImageView thumb_image = mView.findViewById(R.id.request_profile_image);

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

    public void setUserStatus(String userStatus) {
        TextView status = mView.findViewById(R.id.request_profile_status);
        status.setText(userStatus);
    }
}
