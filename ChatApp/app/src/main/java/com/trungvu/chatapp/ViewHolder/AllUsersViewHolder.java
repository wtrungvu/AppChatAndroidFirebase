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


public class AllUsersViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public AllUsersViewHolder(View view) {
        super(view);
        this.view = view;
    }

    public void setUser_name(String user_name) {
        TextView username = view.findViewById(R.id.username_all_users_display);
        username.setText(user_name);
    }

    public void setUser_status(String user_status) {
        TextView status = view.findViewById(R.id.user_status_all_users_display);
        status.setText(user_status);
    }

    public void setUser_thumb_image(final Context ctx, final String user_thumb_image) {
        final CircleImageView profile_image = view.findViewById(R.id.profile_image_all_users_display);

        Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                .into(profile_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.profile).into(profile_image);
                    }
                });
    }
}
