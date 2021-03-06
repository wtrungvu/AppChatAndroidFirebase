package com.trungvu.chatapp.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trungvu.chatapp.Fragment.ChatsFragment;
import com.trungvu.chatapp.Fragment.FriendsFragment;
import com.trungvu.chatapp.Fragment.RequestsFragment;
import com.trungvu.chatapp.R;


public class TabsPagerAdapter extends FragmentPagerAdapter {
//    final int PAGE_COUNT = 4;
final int PAGE_COUNT = 3;
    Context context;

    public TabsPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
//            case 1:
//                GroupChatsFragment groupChatsFragment = new GroupChatsFragment();
//                return groupChatsFragment;
            case 1:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                String main_chats = context.getString(R.string.main_chats);
                return main_chats;
//            case 1:
//                String main_groups = context.getString(R.string.main_groups);
//                return main_groups;
            case 1:
                String main_requests = context.getString(R.string.main_requests);
                return main_requests;
            case 2:
                String main_friends = context.getString(R.string.main_friends);
                return main_friends;
            default:
                return null;
        }
    }

}
