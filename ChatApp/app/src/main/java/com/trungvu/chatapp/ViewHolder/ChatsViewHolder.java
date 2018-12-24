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


public class ChatsViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public ChatsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }
    public void setUserName(String userName){
        TextView userNameDisplay = mView.findViewById(R.id.all_users_username);
        userNameDisplay.setText(userName);
    }
    public void setThumbImage(final String thumbImage, final Context ctx){
        final CircleImageView thumb_image = mView.findViewById(R.id.all_users_profile_image);

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

    public void setUserOnline(String online_status) {
        ImageView onlineStatusView = mView.findViewById(R.id.online_status_image);
        if (online_status.equals("true")){
            onlineStatusView.setVisibility(View.VISIBLE);
        }
        else{
            onlineStatusView.setVisibility(View.INVISIBLE);
        }
    }

    public void setUserStatus(String userStatus) {
        TextView user_status = mView.findViewById(R.id.all_users_userstatus);
        user_status.setText(userStatus);
    }
}
