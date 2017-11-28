package com.shmeli.surakat.ui.new_version;

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
import com.shmeli.surakat.ui.new_version.fragments.RegisterFragment;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;

import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class ExternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

    private SignInFragment  signInFragment;

    private ActionBar       actionBar;
    private Toolbar         toolbar;

    private TextView        toolbarTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external);

        Log.e("LOG", "ExternalActivity: onCreate()");

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

        int toolbarTitleResId = 0;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        //Log.e("LOG", "ExternalActivity: init(): signInFragment name: " +(SignInFragment.newInstance().getClass().getName()));

        signInFragment = (SignInFragment) fragmentManager.findFragmentByTag(SignInFragment.newInstance().getClass().getName());

        //Log.e("LOG", "ExternalActivity: init(): signInFragment is null: " +(signInFragment == null));

        // first start of app and need to create the signInFragment
        if (signInFragment == null) {

            // create the signInFragment
            signInFragment = SignInFragment.newInstance();
            signInFragment.setFragmentCode(CONST.SIGN_IN_FRAGMENT_CODE);
            signInFragment.setFragmentTitleResId(R.string.text_sign_in);

            setCurrentFragmentCode(CONST.SIGN_IN_FRAGMENT_CODE);

            replaceFirstLayerFragment(signInFragment);

            toolbarTitleResId = signInFragment.getFragmentTitleResId();
        }
        // app is started again and signInFragment exists
        else {

            int backStackSize = fragmentManager.getBackStackEntryCount();

//            Log.e("LOG", "ExternalActivity: init(): backStackSize= " +backStackSize);

            // if back stack contains not only signInFragment
            if(backStackSize > 1) {

//                Log.e("LOG", "ExternalActivity: init(): fragment name: " +fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());

                // get the last back stack fragments
                ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
                toolbarTitleResId = fragment.getFragmentTitleResId();

                showOrHideToolbarBackButton(fragment.getFragmentCode());
            }
            // if back stack contains only signInFragment
            else {

                toolbarTitleResId = signInFragment.getFragmentTitleResId();
            }
        }

        // if resource identifier for toolbar title is correct
        if(toolbarTitleResId > 0) {
            setToolbarTitle(toolbarTitleResId);
        }
        // if resource identifier for toolbar title is incorrect
        else {
            Log.e("LOG", "ExternalActivity: init(): toolbarTitleResId has incorrect value= " +toolbarTitleResId);
        }
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

//        Log.e("LOG", "ExternalActivity: onBackStackChanged(): backStackSize= " +backStackSize);

        if(backStackSize > 0) {

//            Log.e("LOG", "ExternalActivity: onBackStackChanged(): fragment name: " +fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());

            ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
            setToolbarTitle(fragment.getFragmentTitleResId());

            showOrHideToolbarBackButton(fragment.getFragmentCode());
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
        Log.e("LOG", "ExternalActivity: setFirstLayerFragment()");

        /*ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.SIGN_IN_FRAGMENT_CODE:
                fragment = SignInFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_sign_in);
                break;
            default:
                Log.e("LOG", "ExternalActivity: setFirstLayerFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            setCurrentFragmentCode(fragmentCode);

//            replaceFirstLayerFragment(   fragment,
//                                        animate,
//                                        addToBackStack);

            replaceFirstLayerFragment(fragment);
        }
        else {
            Log.e("LOG", "ExternalActivity: setFirstLayerFragment(): fragment is null");
        }*/
    }

    @Override
    public void setSecondLayerFragment(int      fragmentCode,
                                       String   selectedUserId) {
        Log.e("LOG", "ExternalActivity: setSecondLayerFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.REGISTER_FRAGMENT_CODE:
                fragment = RegisterFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_create_an_account);
                break;
            default:
                Log.e("LOG", "ExternalActivity: setSecondLayerFragment(): undefined fragment code: " +fragmentCode);
        }

        if(fragment != null) {

            fragment.setFragmentCode(fragmentCode);

            setCurrentFragmentCode(fragmentCode);

            addSecondLayerFragment(fragment);
        }
        else {
            Log.e("LOG", "ExternalActivity: setSecondLayerFragment(): fragment is null");
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
    }

    // ----------------------------------- TOOLBAR ----------------------------------------- //

    @Override
    public void setToolbarTitle(int titleResId) {
        Log.e("LOG", "ExternalActivity: setToolbarTitle()");

        if(titleResId > 0) {
            toolbarTitleTextView.setText(titleResId);
        }
    }

    private void showOrHideToolbarBackButton(int fragmentCode) {
        Log.e("LOG", "ExternalActivity: showOrHideToolbarBackButton()");

        switch (fragmentCode) {

            case CONST.REGISTER_FRAGMENT_CODE:
                showToolbarBackButton();
                break;
            case CONST.SIGN_IN_FRAGMENT_CODE:
                hideToolbarBackButton();
                break;
            default:
                Log.e("LOG", "ExternalActivity: showOrHideToolbarBackButton(): undefined fragment code: " +fragmentCode);
        }
    }

    private void showToolbarBackButton() {
        Log.e("LOG", "ExternalActivity: showToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void hideToolbarBackButton() {
        Log.e("LOG", "ExternalActivity: hideToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }
}
