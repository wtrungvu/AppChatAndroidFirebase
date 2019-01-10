package com.trungvu.chatapp.ViewHolder;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trungvu.chatapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public ChatsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setUserName(String userName) {
        TextView username = view.findViewById(R.id.username_all_users_display);
        username.setText(userName);
    }

    public void setThumbImage(final String thumbImage, final Context ctx) {
        final CircleImageView profile_image = view.findViewById(R.id.profile_image_all_users_display);

        Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                .into(profile_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.profile).into(profile_image);
                    }
                });
    }

    public void setUserOnline(String online_status) {
        ImageView online = view.findViewById(R.id.online_status_image_all_users_display);
        ImageView offline = view.findViewById(R.id.offline_status_image_all_users_display);

        if (online_status.equals("true")) {
            online.setVisibility(View.VISIBLE);
            offline.setVisibility(View.GONE);
        } else {
            online.setVisibility(View.GONE);
            offline.setVisibility(View.VISIBLE);
        }
    }

    public void setUserStatus(String userStatus) {
        TextView user_status = view.findViewById(R.id.user_status_all_users_display);
        user_status.setText(userStatus);
    }
}
