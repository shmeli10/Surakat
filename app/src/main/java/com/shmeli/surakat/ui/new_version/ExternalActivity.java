package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;

import com.shmeli.surakat.ui.new_version.fragments.FillAccountFragment;
import com.shmeli.surakat.ui.new_version.fragments.ParentFragment;
import com.shmeli.surakat.ui.new_version.fragments.RegisterFragment;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;

import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class ExternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

    private RelativeLayout  rootContainer;

    private ActionBar       actionBar;
    private Toolbar         toolbar;

    private TextView        toolbarTitleTextView;

    private int             currentFragmentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external);

        Log.e("LOG", "ExternalActivity: onCreate()");

        rootContainer   = UiUtils.findView( this,
                                            R.id.externalActivityContainer);

        toolbar         = UiUtils.findView( this,
                                            R.id.externalActivityToolbar);
        setSupportActionBar(toolbar);

        toolbarTitleTextView = UiUtils.findView(toolbar,
                                                R.id.appToolbarTitleTextView);

        actionBar       = getSupportActionBar();

        init();
    }

    // ----------------------------------- OTHER ----------------------------------------- //

    private void init() {
        Log.e("LOG", "ExternalActivity: init()");

        getFragmentManager().addOnBackStackChangedListener(this);

        setFragment(CONST.SIGN_IN_FRAGMENT,
                false,
                false);
    }

    public void moveToInternalActivity() {
        Log.e("LOG", "ExternalActivity: moveToInternalActivity()");

        Intent internalActivityIntent = new Intent( ExternalActivity.this,
                InternalActivity.class);
        internalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(internalActivityIntent);

        finish();

    }

    // ----------------------------------- FRAGMENTS ----------------------------------------- //

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "ExternalActivity: onBackStackChanged()");

        FragmentManager fragmentManager = getFragmentManager();

        int backStackSize = fragmentManager.getBackStackEntryCount();
        if (backStackSize == 0) {
            currentFragmentCode = CONST.SIGN_IN_FRAGMENT;

            setToolbarTitle(R.string.text_sign_in);
            hideToolbarBackButton();

            return;
        }

        ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
        setToolbarTitle(fragment.getFragmentTitleResId());

        switch(currentFragmentCode) {

/*            case CONST.FILL_ACCOUNT_FRAGMENT:
                setToolbarTitle(R.string.text_fill_account);
                showToolbarBackButton();
                break;*/
            case CONST.REGISTER_FRAGMENT:
                setToolbarTitle(R.string.text_create_an_account);
                showToolbarBackButton();
                break;
//            case CONST.SIGN_IN_FRAGMENT:
//                hideToolbarBackButton();
//                break;
            default:
                Log.e("LOG", "ExternalActivity: onBackStackChanged(): undefined fragment code: " +currentFragmentCode);
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
    public void changeFragment(Fragment fragment) {
        Log.e("LOG", "ExternalActivity: changeFragment()");
    }

    @Override
    public void setFragment(int     fragmentCode,
                            boolean animate,
                            boolean addToBackStack) {
        Log.e("LOG", "ExternalActivity: setFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

/*            case CONST.FILL_ACCOUNT_FRAGMENT:
                fragment = FillAccountFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_fill_account);

                showToolbarBackButton();
                break;*/
            case CONST.REGISTER_FRAGMENT:
                fragment = RegisterFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_create_an_account);

                showToolbarBackButton();
                break;
            case CONST.SIGN_IN_FRAGMENT:
                fragment = SignInFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_sign_in);

                hideToolbarBackButton();
                break;
            default:
                Log.e("LOG", "ExternalActivity: setFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            currentFragmentCode = fragmentCode;

            changeFragmentTo(   fragment,
                                animate,
                                addToBackStack);

            setToolbarTitle(fragment.getFragmentTitleResId());
        }
        else {
            Log.e("LOG", "ExternalActivity: setFragment(): fragment is null");
        }
    }
    @Override
    public void onBackPressed() {
        Log.e("LOG", "ExternalActivity: onBackPressed()");

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
//            Log.e("LOG", "ExternalActivity: onBackPressed(): go to SignInFragment");
//
//            for (int i = 0; i<fragmentManager.getBackStackEntryCount(); i++)
//                fragmentManager.popBackStack();
//        }
//        else {
//
//            Log.e("LOG", "ExternalActivity: onBackPressed(): go back");
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
        Log.e("LOG", "ExternalActivity: setToolbarTitle()");

        if(titleResId > 0) {
            toolbarTitleTextView.setText(titleResId);
        }
    }

    public void showToolbarBackButton() {
//        Log.e("LOG", "ExternalActivity: showToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void hideToolbarBackButton() {
//        Log.e("LOG", "ExternalActivity: hideToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }
}
