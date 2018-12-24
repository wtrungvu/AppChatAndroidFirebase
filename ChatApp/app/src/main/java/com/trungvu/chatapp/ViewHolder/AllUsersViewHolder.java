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
    public View mView;
    public AllUsersViewHolder(View itemView) {
        super(itemView);
        mView =itemView;
    }
    public void setUser_name(String user_name){
        TextView name = mView.findViewById(R.id.all_users_username);
        name.setText(user_name);
    }
    public void setUser_status(String user_status){
        TextView status = mView.findViewById(R.id.all_users_userstatus);
        status.setText(user_status);
    }
    public void setUser_thumb_image(final Context ctx, final String user_thumb_image){
        final CircleImageView thumb_image = mView.findViewById(R.id.all_users_profile_image);

        Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                .into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.profile).into(thumb_image);
                    }
                });
    }
}
