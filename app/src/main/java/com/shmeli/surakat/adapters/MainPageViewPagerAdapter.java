package com.shmeli.surakat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.fragments.AllUsersFragment;
import com.shmeli.surakat.ui.fragments.ChatsFragment;
import com.shmeli.surakat.ui.fragments.FriendsFragment;
import com.shmeli.surakat.ui.fragments.RequestsFragment;

/**
 * Created by Serghei Ostrovschi on 10/18/17.
 */

public class MainPageViewPagerAdapter extends FragmentPagerAdapter {

//    private final int REQUESTS_TAB_ID   = 0;
//    private final int CHATS_TAB_ID      = 1;
//    private final int FRIENDS_TAB_ID    = 2;

    private final int ALL_USERS_TAB_ID  = 1;
    private final int FRIENDS_TAB_ID    = 0;


    private int tabsCount = 0;

    public MainPageViewPagerAdapter(Context         context,
                                    FragmentManager fragmentManager) {
        super(fragmentManager);

        tabsCount = context.getResources().getInteger(R.integer.main_page_view_pager_tabs_count);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

//            case REQUESTS_TAB_ID:
//                RequestsFragment requestsFragment = new RequestsFragment();
//                return requestsFragment;
//            case CHATS_TAB_ID:
//                ChatsFragment chatsFragment = new ChatsFragment();
//                return chatsFragment;
            case ALL_USERS_TAB_ID:
                AllUsersFragment allUsersFragment = new AllUsersFragment();
                return allUsersFragment;
            case FRIENDS_TAB_ID:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    // ----------------------------------------------------------------------- //

    public CharSequence getPageTitle(int position) {

        switch(position) {

//            case REQUESTS_TAB_ID:
//                return CONST.REQUESTS_TAB_NAME;
//            case CHATS_TAB_ID:
//                return CONST.CHATS_TAB_NAME;

            case ALL_USERS_TAB_ID:
                return CONST.ALL_USERS_TAB_NAME;
            case FRIENDS_TAB_ID:
                return CONST.FRIENDS_TAB_NAME;
            default:
                return null;
        }
    }
}
