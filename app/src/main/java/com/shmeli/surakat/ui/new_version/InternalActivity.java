package com.shmeli.surakat.ui.new_version;

import android.app.AlertDialog;
import android.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.interfaces.TransferSelectedUser;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.new_version.fragments.ChatFragment;
import com.shmeli.surakat.ui.new_version.fragments.ParentFragment;
import com.shmeli.surakat.ui.new_version.fragments.SettingsFragment;
import com.shmeli.surakat.ui.new_version.fragments.TabsFragment;
import com.shmeli.surakat.ui.new_version.fragments.UserProfileFragment;
import com.shmeli.surakat.ui.new_version.fragments.UserStatusFragment;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.e("LOG", "InternalActivity: onCreateOptionsMenu()");

        getMenuInflater().inflate(  R.menu.menu,
                                    menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.e("LOG", "InternalActivity: onOptionsItemSelected()");

        switch(item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_signout:
                showAlertDialog();
                break;
            case R.id.menu_settings:
                moveToSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("LOG", "InternalActivity: onStart()");

        changeUserOnlineStatus(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("LOG", "InternalActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("LOG", "InternalActivity: onStop()");

        changeUserOnlineStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("LOG", "InternalActivity: onDestroy()");
    }

    // ----------------------------------- OTHER ----------------------------------------- //

    private void init() {
        Log.e("LOG", "InternalActivity: init()");

        if(initCurrentUser() &&
           currentUserExistsInFBDB()) {

            int toolbarTitleResId = 0;

            Log.e("LOG", "InternalActivity: init(): initCurrentUser success");

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.addOnBackStackChangedListener(this);

            //Log.e("LOG", "InternalActivity: init(): tabsFragment name: " +(TabsFragment.newInstance().getClass().getName()));

            tabsFragment = (TabsFragment) fragmentManager.findFragmentByTag(TabsFragment.newInstance().getClass().getName()); //CONST.TABS_FRAGMENT_NAME);

            //Log.e("LOG", "InternalActivity: init(): tabsFragment is null: " +(tabsFragment == null));

            // first start of app and need to create the tabsFragment
            if (tabsFragment == null) {

                // add the fragment
                tabsFragment = TabsFragment.newInstance();
                tabsFragment.setFragmentCode(CONST.TABS_FRAGMENT_CODE);
                tabsFragment.setFragmentTitleResId(R.string.text_tabs_fragment);

                setCurrentFragmentCode(CONST.TABS_FRAGMENT_CODE);

                replaceFirstLayerFragment(tabsFragment);

                toolbarTitleResId = tabsFragment.getFragmentTitleResId();
            }
            // app is started again and tabsFragment exists
            else {

                int backStackSize = fragmentManager.getBackStackEntryCount();

                //Log.e("LOG", "InternalActivity: init(): backStackSize= " +backStackSize);

                // if back stack contains not only tabsFragment
                if(backStackSize > 1) {

                    //Log.e("LOG", "InternalActivity: init(): fragment name: " +fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());

                    // get the last back stack fragments
                    ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
                    toolbarTitleResId = fragment.getFragmentTitleResId();

                    showOrHideToolbarBackButton(fragment.getFragmentCode());
                }
                // if back stack contains only signInFragment
                else {

                    toolbarTitleResId = tabsFragment.getFragmentTitleResId();
                    setCurrentFragmentCode(CONST.TABS_FRAGMENT_CODE);
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

            //getCurrentUserFBDatabaseRef().addListenerForSingleValueEvent(currentUserDataChangeListener);
        }
        else {

            Log.e("LOG", "InternalActivity: init(): initCurrentUser error");

            moveToExternalActivity();
        }
    }

    public String getCurrentDate() {

        SimpleDateFormat simpleDateFormat   = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String currentDate                  = simpleDateFormat.format(new Date());

        return currentDate;
    }

    private void showAlertDialog() {

        int alertDialogMessageResId = R.string.text_logout_confirm;
        int alertDialogHeaderResId  = R.string.text_logout_header;

        int alertDialogDividerColorResId = getResources().getColor(R.color.colorAccent);

        int noTextResId     = R.string.text_no;
        int yesTextResId    = R.string.text_yes;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(   InternalActivity.this,
                                                                            R.style.Theme_Sphinx_Dialog_Alert);
        alertDialogBuilder.setMessage(alertDialogMessageResId)
                .setCancelable(false)
                .setPositiveButton(yesTextResId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();
                    }
                })
                .setNegativeButton(noTextResId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(alertDialogHeaderResId);
        alertDialog.show();

        // Set title divider color
        int titleDividerId = getResources().getIdentifier(  "titleDivider",
                                                            "id",
                                                            "android");
        View titleDivider = alertDialog.findViewById(titleDividerId);

        if (titleDivider != null)
            titleDivider.setBackgroundColor(alertDialogDividerColorResId);
    }

    private void logout() {
        //Log.e("LOG", "InternalActivity: logout()");

        if(getFBAuth() != null) {

            changeUserOnlineStatus(false);

            getFBAuth().signOut();

            moveToExternalActivity();
        }
        else {
            Log.e("LOG", "InternalActivity: logout(): Error: getFBAuth() returns null");
        }
    }

    public void moveToExternalActivity() {
        //Log.e("LOG", "InternalActivity: moveToExternalActivity()");

        Intent internalActivityIntent = new Intent( InternalActivity.this,
                                                    ExternalActivity.class);
        internalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(internalActivityIntent);

        finish();
    }

    private void moveToSettings() {
        //Log.e("LOG", "InternalActivity: moveToSettings()");

        setSecondLayerFragment( CONST.SETTINGS_FRAGMENT_CODE,
                                null,
                                null);
    }

    private void changeUserOnlineStatus(boolean userIsOnline) {

        Log.e("LOG", "InternalActivity: changeUserOnlineStatus(): to: " +userIsOnline);

        if(userIsOnline) {
            getCurrentUserFBDatabaseRef().child(CONST.USER_IS_ONLINE).setValue(true);
        }
        else {
            getCurrentUserFBDatabaseRef().child(CONST.USER_IS_ONLINE).setValue(false);
            getCurrentUserFBDatabaseRef().child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
        }
    }

    // ----------------------------------- FRAGMENTS ----------------------------------------- //

    @Override
    public void onTransferSelectedUserSuccess(int       targetFragmentCode,
                                              String    selectedUserKey,
                                              User      selectedUser) {
        //Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess()");

        //Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): targetFragmentCode: " +targetFragmentCode);

        if(targetFragmentCode <= 0) {
            Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): incorrect targetFragmentCode: " +targetFragmentCode);

            return;
        }

        if(!TextUtils.isEmpty(selectedUserKey)) {

            //Log.e("LOG", "InternalActivity: onTransferSelectedUserSuccess(): selectedUserKey: " + selectedUserKey);

            setSecondLayerFragment( targetFragmentCode,
                                    selectedUserKey,
                                    selectedUser);
        }
    }

    @Override
    public void onTransferSelectedUserError(String error) {
        Log.e("LOG", "InternalActivity: onBackStackChanged(): error: " +error);
    }

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "InternalActivity: onBackStackChanged()");

        FragmentManager fragmentManager = getFragmentManager();

        int backStackSize = fragmentManager.getBackStackEntryCount();

        //Log.e("LOG", "InternalActivity: onBackStackChanged(): backStackSize= " +backStackSize);

        if(backStackSize > 0) {

            ParentFragment fragment = (ParentFragment) fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(backStackSize - 1).getName());
            setToolbarTitle(fragment.getFragmentTitleResId());

            showOrHideToolbarBackButton(fragment.getFragmentCode());
        }
        else {

            finish();
        }
    }

    @Override
    public void setSecondLayerFragment(int      fragmentCode,
                                       String   selectedUserId,
                                       User     selectedUser) {
        Log.e("LOG", "InternalActivity: setSecondLayerFragment()");

        ParentFragment fragment = null;

        switch(fragmentCode) {

            case CONST.CHAT_FRAGMENT_CODE:
                fragment = ChatFragment.newInstance(selectedUserId,
                                                    selectedUser);
                fragment.setFragmentTitleResId(R.string.text_chat);
                break;
            case CONST.SETTINGS_FRAGMENT_CODE:
                fragment = SettingsFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_settings);
                break;
            case CONST.USER_PROFILE_FRAGMENT_CODE:
                fragment = UserProfileFragment.newInstance(selectedUserId);
                fragment.setFragmentTitleResId(R.string.text_profile);
                break;
            case CONST.USER_STATUS_FRAGMENT_CODE:
                fragment = UserStatusFragment.newInstance();
                fragment.setFragmentTitleResId(R.string.text_set_status);
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

    @Override
    public void onBackPressed() {
        Log.e("LOG", "InternalActivity: onBackPressed()");

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
                setCurrentFragmentCode(CONST.TABS_FRAGMENT_CODE);
                break;
            case CONST.CHAT_FRAGMENT_CODE:
            case CONST.SETTINGS_FRAGMENT_CODE:
            case CONST.USER_PROFILE_FRAGMENT_CODE:
            case CONST.USER_STATUS_FRAGMENT_CODE:
                showToolbarBackButton();
                break;
            default:
                Log.e("LOG", "InternalActivity: showOrHideToolbarBackButton(): undefined fragment code: " +fragmentCode);
        }
    }

    private void showToolbarBackButton() {
        Log.e("LOG", "InternalActivity: showToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void hideToolbarBackButton() {
        Log.e("LOG", "InternalActivity: hideToolbarBackButton()");

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener currentUserDataChangeListener = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            //Log.e("LOG", "InternalActivity: currentUserDataChangeListener: onDataChange()");

/*            if(dataSnapshot != null) {

                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).onDisconnect().setValue(false);
                currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
            }*/
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
}
