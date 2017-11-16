package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.fragments.ParentFragment;
import com.shmeli.surakat.ui.new_version.fragments.TabsFragment;
import com.shmeli.surakat.ui.new_version.fragments.UserProfileFragment;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class InternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

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

            Log.e("LOG", "InternalActivity: init(): initCurrentUser success");

            getFragmentManager().addOnBackStackChangedListener(this);

            setFirstLayerFragment(CONST.TABS_FRAGMENT);

//            setFirstLayerFragment(CONST.TABS_FRAGMENT,
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

    // ----------------------------------- FRAGMENTS ----------------------------------------- //

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "InternalActivity: onBackStackChanged()");

        FragmentManager fragmentManager = getFragmentManager();

        int backStackSize = fragmentManager.getBackStackEntryCount();

        if(backStackSize > 0) {

            ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
            setToolbarTitle(fragment.getFragmentTitleResId());

            switch (fragment.getFragmentCode()) {

                case CONST.TABS_FRAGMENT:
                    hideToolbarBackButton();
                    break;
                case CONST.USER_PROFILE_FRAGMENT:
                    showToolbarBackButton();
                    break;
                default:
                    Log.e("LOG", "InternalActivity: onBackStackChanged(): undefined fragment code: " + getCurrentFragmentCode());
            }
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

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.TABS_FRAGMENT:
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
        }
    }

    @Override
    public void setSecondLayerFragment(int      fragmentCode,
                                       String   selectedUserId) {
        Log.e("LOG", "InternalActivity: setSecondLayerFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.USER_PROFILE_FRAGMENT:
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

            case CONST.TABS_FRAGMENT:
                fragment = TabsFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_tabs_fragment);

                hideToolbarBackButton();
                break;

*//*            case CONST.FILL_ACCOUNT_FRAGMENT:
                fragment = FillAccountFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_fill_account);

                showToolbarBackButton();
                break;*//*
//            case CONST.REGISTER_FRAGMENT:
//                fragment = RegisterFragment.newInstance();
//                fragment.setFragmentTitleResId(R.string.text_create_an_account);
//
//                showToolbarBackButton();
//                break;
//            case CONST.SIGN_IN_FRAGMENT:
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

    public void showToolbarBackButton() {
//        Log.e("LOG", "InternalActivity: showToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void hideToolbarBackButton() {
//        Log.e("LOG", "InternalActivity: hideToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

}
