package com.shmeli.surakat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shmeli.surakat.data.CONST;


import com.shmeli.surakat.ui.fragments.FriendsFragment;

import com.shmeli.surakat.ui.new_version.fragments.AllUsersFragment;

/**
 * Created by Serghei Ostrovschi on 11/16/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

            case CONST.ALL_USERS_TAB_ID:
                return AllUsersFragment.newInstance();
            case CONST.FRIENDS_TAB_ID:
                return new FriendsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return CONST.TABS_COUNT;
    }

    // ----------------------------------------------------------------------- //

    public CharSequence getPageTitle(int position) {

        switch(position) {

            case CONST.ALL_USERS_TAB_ID:
                return CONST.ALL_USERS_TAB_NAME;
            case CONST.FRIENDS_TAB_ID:
                return CONST.FRIENDS_TAB_NAME;
            default:
                return null;
        }
    }
}
