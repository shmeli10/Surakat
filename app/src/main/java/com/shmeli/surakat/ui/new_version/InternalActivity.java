package com.shmeli.surakat.ui.new_version;

import android.app.FragmentManager;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;

import android.view.MenuItem;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.interfaces.TransferSelectedUser;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.new_version.fragments.ChatFragment;
import com.shmeli.surakat.ui.new_version.fragments.ParentFragment;
import com.shmeli.surakat.ui.new_version.fragments.TabsFragment;
import com.shmeli.surakat.ui.new_version.fragments.UserProfileFragment;
import com.shmeli.surakat.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class InternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener,
                                            TransferSelectedUser {

    private TabsFragment    tabsFragment;

    private ActionBar       actionBar;
    private Toolbar         toolbar;

    private TextView        toolbarTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        Log.e("LOG", "InternalActivity: onCreate()");

        toolbar         = UiUtils.findView( this,
                                            R.id.internalActivityToolbar);
        setSupportActionBar(toolbar);

        toolbarTitleTextView = UiUtils.findView(toolbar,
                                                R.id.appToolbarTitleTextView);

        actionBar       = getSupportActionBar();

        init();
    }

    // ----------------------------------- OTHER ----------------------------------------- //

    private void init() {
        Log.e("LOG", "InternalActivity: init()");

        if(initCurrentUser()) {

            int toolbarTitleResId = 0;

            Log.e("LOG", "InternalActivity: init(): initCurrentUser success");

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.addOnBackStackChangedListener(this);

            Log.e("LOG", "InternalActivity: init(): tabsFragment name: " +(TabsFragment.newInstance().getClass().getName()));

            tabsFragment = (TabsFragment) fragmentManager.findFragmentByTag(TabsFragment.newInstance().getClass().getName()); //CONST.TABS_FRAGMENT_NAME);

            Log.e("LOG", "InternalActivity: init(): tabsFragment is null: " +(tabsFragment == null));

            // first start of app and need to create the tabsFragment
            if (tabsFragment == null) {

                // add the fragment
                tabsFragment = TabsFragment.newInstance();
                tabsFragment.setFragmentCode(CONST.TABS_FRAGMENT_CODE);
                tabsFragment.setFragmentTitleResId(R.string.text_tabs_fragment);

                setCurrentFragmentCode(CONST.TABS_FRAGMENT_CODE);

                replaceFirstLayerFragment(tabsFragment);

                toolbarTitleResId = tabsFragment.getFragmentTitleResId();

                //setFirstLayerFragment(CONST.TABS_FRAGMENT_CODE);
            }
            // app is started again and tabsFragment exists
            else {

                int backStackSize = fragmentManager.getBackStackEntryCount();

                Log.e("LOG", "InternalActivity: init(): backStackSize= " +backStackSize);

                // if back stack contains not only tabsFragment
                if(backStackSize > 1) {

                    Log.e("LOG", "InternalActivity: init(): fragment name: " +fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());

                    // get the last back stack fragments
                    ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
                    toolbarTitleResId = fragment.getFragmentTitleResId();

                    showOrHideToolbarBackButton(fragment.getFragmentCode());
                }
                // if back stack contains only signInFragment
                else {

                    toolbarTitleResId = tabsFragment.getFragmentTitleResId();
                }
            }

            // if resource identifier for toolbar title is correct
            if(toolbarTitleResId > 0) {
                setToolbarTitle(toolbarTitleResId);
            }
            // if resource identifier for toolbar title is incorrect
            else {
                Log.e("LOG", "InternalActivity: init(): toolbarTitleResId has incorrect value= " +toolbarTitleResId);
            }

//            setToolbarTitle(tabsFragment.getFragmentTitleResId());

//            getFragmentManager().addOnBackStackChangedListener(this);
//
//            setFirstLayerFragment(CONST.TABS_FRAGMENT_CODE);


//            setFirstLayerFragment(CONST.TABS_FRAGMENT_CODE,
//                    false,
//                    false);
        }
        else {

            Log.e("LOG", "InternalActivity: init(): initCurrentUser error");
        }
    }

    public void moveToExternalActivity() {
        Log.e("LOG", "InternalActivity: moveToExternalActivity()");

        Intent internalActivityIntent = new Intent( InternalActivity.this,
                                                    ExternalActivity.class);
        internalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(internalActivityIntent);

        finish();
    }

    public String getCurrentDate() {

        SimpleDateFormat simpleDateFormat   = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String currentDate                  = simpleDateFormat.format(new Date());

        return currentDate;
    }

    // ----------------------------------- FRAGMENTS ----------------------------------------- //

    @Override
    public void onTransferSelectedUserSuccess(int       targetFragmentCode,
                                              String    selectedUserKey,
                                              User      selectedUser) {
//        Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess()");

        Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): targetFragmentCode: " +targetFragmentCode);

        if(targetFragmentCode <= 0) {
            Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): incorrect targetFragmentCode: " +targetFragmentCode);

            return;
        }

        if(!TextUtils.isEmpty(selectedUserKey)) {
            Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): selectedUserKey: " + selectedUserKey);

            setSecondLayerFragment( targetFragmentCode,
                                    selectedUserKey);
        }

        if(selectedUser != null) {

            if(!TextUtils.isEmpty(selectedUser.getUserName()))
                Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): selectedUser name: " + selectedUser.getUserName());
            else
                Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): selectedUser name is empty or null");
        }
    }

    @Override
    public void onTransferSelectedUserError(String error) {
        Log.e("LOG", "InternalActivity: onBackStackChanged()");
    }

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "InternalActivity: onBackStackChanged()");

        FragmentManager fragmentManager = getFragmentManager();

        int backStackSize = fragmentManager.getBackStackEntryCount();

        Log.e("LOG", "InternalActivity: onBackStackChanged(): backStackSize= " +backStackSize);

        if(backStackSize > 0) {

            ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
            setToolbarTitle(fragment.getFragmentTitleResId());

            showOrHideToolbarBackButton(fragment.getFragmentCode());

            /*switch (fragment.getFragmentCode()) {

                case CONST.TABS_FRAGMENT_CODE:

//                    Log.e("LOG", "InternalActivity: onBackStackChanged(): backStackSize= " +backStackSize);

//                    if(backStackSize == 2) {
//
//                        Log.e("LOG", "InternalActivity: onBackStackChanged(): pop last version fragment");
//
//                        fragmentManager.popBackStack(   fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getId(),
//                                                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    }

                    hideToolbarBackButton();
                    break;
                case CONST.USER_PROFILE_FRAGMENT_CODE:
                    showToolbarBackButton();
                    break;
                default:
                    //Log.e("LOG", "InternalActivity: onBackStackChanged(): undefined fragment code: " + getCurrentFragmentCode());
                    Log.e("LOG", "InternalActivity: onBackStackChanged(): undefined fragment code: " +fragment.getFragmentCode());
            }*/
        }
        else {

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setFirstLayerFragment(int fragmentCode) {
        Log.e("LOG", "InternalActivity: setFirstLayerFragment()");

/*        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.TABS_FRAGMENT_CODE:
                fragment = TabsFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_tabs_fragment);
                break;
            default:
                Log.e("LOG", "InternalActivity: setFirstLayerFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            setCurrentFragmentCode(fragmentCode);

            replaceFirstLayerFragment(fragment);
        }
        else {
            Log.e("LOG", "InternalActivity: setFirstLayerFragment(): fragment is null");
        }*/
    }

    @Override
    public void setSecondLayerFragment(int      fragmentCode,
                                       String   selectedUserId) {
        Log.e("LOG", "InternalActivity: setSecondLayerFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.CHAT_FRAGMENT_CODE:
                fragment = ChatFragment.newInstance(selectedUserId);
                fragment.setFragmentTitleResId(R.string.text_chat);
                break;
            case CONST.USER_PROFILE_FRAGMENT_CODE:
                fragment = UserProfileFragment.newInstance(selectedUserId);
                fragment.setFragmentTitleResId(R.string.text_profile);
                break;
            default:
                Log.e("LOG", "InternalActivity: setSecondLayerFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            setCurrentFragmentCode(fragmentCode);

            addSecondLayerFragment(fragment);
        }
        else {
            Log.e("LOG", "InternalActivity: setSecondLayerFragment(): fragment is null");
        }
    }

    /*@Override
    public void setFirstLayerFragment(int     fragmentCode,
                                      boolean animate,
                                      boolean addToBackStack) {
        Log.e("LOG", "InternalActivity: setFirstLayerFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.TABS_FRAGMENT_CODE:
                fragment = TabsFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_tabs_fragment);

                hideToolbarBackButton();
                break;

*//*            case CONST.FILL_ACCOUNT_FRAGMENT:
                fragment = FillAccountFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_fill_account);

                showToolbarBackButton();
                break;*//*
//            case CONST.REGISTER_FRAGMENT_CODE:
//                fragment = RegisterFragment.newInstance();
//                fragment.setFragmentTitleResId(R.string.text_create_an_account);
//
//                showToolbarBackButton();
//                break;
//            case CONST.SIGN_IN_FRAGMENT_CODE:
//                fragment = SignInFragment.newInstance();
//                fragment.setFragmentTitleResId(R.string.text_sign_in);
//
//                hideToolbarBackButton();
//                break;
            default:
                Log.e("LOG", "InternalActivity: setFirstLayerFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            setCurrentFragmentCode(fragmentCode);

            replaceFirstLayerFragment(   fragment,
                                animate,
                                addToBackStack);

            setToolbarTitle(fragment.getFragmentTitleResId());
        }
        else {
            Log.e("LOG", "InternalActivity: setFirstLayerFragment(): fragment is null");
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {
        Log.e("LOG", "InternalActivity: setSecondLayerFragment()");
    }*/

    @Override
    public void onBackPressed() {
        Log.e("LOG", "InternalActivity: onBackPressed()");

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }

//        FragmentManager fragmentManager = getFragmentManager();
//
//        if(currentFragmentCode == CONST.FILL_ACCOUNT_FRAGMENT) {
//
//            Log.e("LOG", "InternalActivity: onBackPressed(): go to SignInFragment");
//
//            for (int i = 0; i<fragmentManager.getBackStackEntryCount(); i++)
//                fragmentManager.popBackStack();
//        }
//        else {
//
//            Log.e("LOG", "InternalActivity: onBackPressed(): go back");
//
//            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                getFragmentManager().popBackStack();
//            }
//            else {
//                super.onBackPressed();
//            }
//        }
    }

    // ----------------------------------- TOOLBAR ----------------------------------------- //

    @Override
    public void setToolbarTitle(int titleResId) {
        Log.e("LOG", "InternalActivity: setToolbarTitle()");

        if(titleResId > 0) {
            toolbarTitleTextView.setText(titleResId);
        }
    }

    private void showOrHideToolbarBackButton(int fragmentCode) {
        Log.e("LOG", "InternalActivity: showOrHideToolbarBackButton()");

        switch (fragmentCode) {

            case CONST.TABS_FRAGMENT_CODE:
                hideToolbarBackButton();
                break;
            case CONST.CHAT_FRAGMENT_CODE:
            case CONST.USER_PROFILE_FRAGMENT_CODE:
                showToolbarBackButton();
                break;
            default:
                Log.e("LOG", "InternalActivity: showOrHideToolbarBackButton(): undefined fragment code: " +fragmentCode);
        }
    }

    private void showToolbarBackButton() {
//        Log.e("LOG", "InternalActivity: showToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void hideToolbarBackButton() {
//        Log.e("LOG", "InternalActivity: hideToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }
}
