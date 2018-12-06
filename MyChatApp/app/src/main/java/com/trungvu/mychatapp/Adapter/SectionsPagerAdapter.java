package com.trungvu.mychatapp.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trungvu.mychatapp.Fragment.ChatsFragment;
import com.trungvu.mychatapp.Fragment.FriendsFragment;
import com.trungvu.mychatapp.Fragment.RequestsFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // 3 Page = Request, Chat, Friend
    }

    // Thêm tên tiêu đề
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LỜI MỜI";
            case 1:
                return "TIN NHẮN";
            case 2:
                return "BẠN BÈ";
            default:
                return null;
        }
    }
}
