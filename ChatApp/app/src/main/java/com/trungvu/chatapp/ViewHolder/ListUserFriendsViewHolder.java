package com.trungvu.chatapp.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.trungvu.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListUserFriendsViewHolder extends RecyclerView.ViewHolder{
    public View view;

    public ListUserFriendsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setUserName(String userName) {
        TextView username = view.findViewById(R.id.username_item_list_user_friends);
        username.setText(userName);
    }

    public void setDate(String date, Context context) {
        TextView sinceFriendsDate = view.findViewById(R.id.user_sinceDate_item_list_user_friends);
        sinceFriendsDate.setText(context.getString(R.string.Friend_since) + " " + date);
    }

    public void setThumbImage(final String thumbImage, final Context ctx) {
        final CircleImageView profile_image = view.findViewById(R.id.profile_image_item_list_user_friends);

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

    public Boolean getCheckBoxSelectedItem(){
        CheckBox checkBox_Select_Item = view.findViewById(R.id.check_Selected_item_list_user_friends);

        if (checkBox_Select_Item.isChecked()){
            return true;
        } else {
            return false;
        }
    }

    public void setCheckBoxSelectedItem(Boolean Check){
        CheckBox checkBox_Select_Item = view.findViewById(R.id.check_Selected_item_list_user_friends);

        if (Check){
            checkBox_Select_Item.setChecked(true);
        } else {
            checkBox_Select_Item.setChecked(false);
        }
    }

}
