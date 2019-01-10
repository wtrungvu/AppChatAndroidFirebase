package com.trungvu.chatapp.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trungvu.chatapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public FriendsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDate(String date, Context context) {
        TextView sinceFriendsDate = view.findViewById(R.id.user_status_all_users_display);
        sinceFriendsDate.setText(context.getString(R.string.Friend_since) + " " + date);
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
        if (online_status.equals("true")) {
            online.setVisibility(View.VISIBLE);
        } else {
            online.setVisibility(View.GONE);
        }
    }
}
