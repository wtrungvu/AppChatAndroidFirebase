package com.trungvu.chatapp.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trungvu.chatapp.R;

public class GroupChatViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public GroupChatViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setNameGroupChats(String nameGroupChats) {
        TextView groupName = view.findViewById(R.id.name_item_group_chats);
        groupName.setText(nameGroupChats);
    }

    public void setCreateDate(String date, Context context) {
        TextView dateCreated = view.findViewById(R.id.dateCreated_item_group_chats);
        dateCreated.setText("Date Created: " + date);
    }

    public void setCountMemberGroup(int size, Context context){
        TextView count = view.findViewById(R.id.countMember_item_group_chats);
        count.setText(count + " Members");
    }


}
