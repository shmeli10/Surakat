package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;

import android.util.Log;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.User;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public abstract class ParentActivity extends AppCompatActivity {

    private String currentUserId    = "";

    private String deviceToken      = "";

    private int currentFragmentCode;

    private DatabaseReference currentUserFBDatabaseRef;
    private DatabaseReference friendsFBDatabaseRef;
    private DatabaseReference rootFBDatabaseRef;
    private DatabaseReference usersFBDatabaseRef;

    private StorageReference  imagesFBStorageRef;

    private FirebaseAuth fbAuth;

    private ProgressDialog progressDialog;

    // ------------------------------ GETTERS -------------------------------- //

    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getDeviceToken() {

        if(TextUtils.isEmpty(deviceToken))
            deviceToken = FirebaseInstanceId.getInstance().getToken();

        return deviceToken;
    }

    public int getCurrentFragmentCode() {

        if(currentFragmentCode > 0)
            return currentFragmentCode;
        else
            return 0;
    }

    public DatabaseReference getCurrentUserFBDatabaseRef() {
        return currentUserFBDatabaseRef;
    }

    public DatabaseReference getCurrentUserFriendsFBDatabaseRef() {
        return friendsFBDatabaseRef;
    }

    public DatabaseReference getRootFBDatabaseRef() {
        return rootFBDatabaseRef;
    }

    public DatabaseReference getUsersFBDatabaseRef() {
        return usersFBDatabaseRef;
    }

    public FirebaseAuth getFBAuth() {

        if(fbAuth == null) {
            fbAuth = FirebaseAuth.getInstance();
        }

        return fbAuth;
    }

    public StorageReference getImagesFBStorageRef() {

        if(imagesFBStorageRef == null) {
            imagesFBStorageRef = FirebaseStorage.getInstance().getReference().child("images");
        }

        return imagesFBStorageRef;
    }

    // ------------------------------ SETTERS -------------------------------- //

    public abstract void setSecondLayerFragment(int     fragmentCode,
                                                String  selectedUserId,
                                                User    selectedUser);

    public void setCurrentFragmentCode(int currentFragmentCode) {
        //Log.e("LOG", "ParentActivity: setCurrentFragmentCode()");

        //Log.e("LOG", "ParentActivity: setCurrentFragmentCode(): currentFragmentCode= " +currentFragmentCode);

        if(currentFragmentCode > 0)
            this.currentFragmentCode = currentFragmentCode;
    }

    public abstract void setToolbarTitle(final int titleResId);

    public abstract void setToolbarInfo(final int infoHeadResId,
                                        final int infoBodyResId);

    public abstract void setToolbarInfo(final int infoHeadResId,
                                        final String infoBodyText);

//    public void setCurrentUserIsOnline(boolean isOnline) {
//
//        if(currentUserFBDatabaseRef != null) {
//
//            // user is offline
//            if (!isOnline) {
//                currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
//            }
//
//            currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(isOnline);
//        }
//        else {
//            Log.e("LOG", "ParentActivity; setUserIsOnline(): currentUserFBDatabaseRef is null");
//        }
//    }

    // ------------------------------ OTHER ---------------------------------- //

    protected void replaceFirstLayerFragment(Fragment    fragment) {

        Log.e("LOG", "ParentActivity: replaceFirstLayerFragment()");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.replace(R.id.fragmentsContainer,
                            fragment,
                            fragment.getClass().getName());

        transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();
    }

    protected void addSecondLayerFragment(Fragment fragment) {

        Log.e("LOG", "ParentActivity: addSecondLayerFragment()");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(R.animator.slide_in,
                                        R.animator.slide_out,
                                        R.animator.slide_in,
                                        R.animator.slide_out);

        transaction.add(R.id.fragmentsContainer,
                        fragment,
                        fragment.getClass().getName());

        transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();
    }

    public boolean initCurrentUser() {
        Log.e("LOG", "ParentActivity: initCurrentUser()");

        boolean result = false;

        if(getFBAuth() != null) {

            rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
            usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
            usersFBDatabaseRef.keepSynced(true);

            FirebaseUser currentUser = getFBAuth().getCurrentUser();

            if (currentUser != null) {

                currentUserId = currentUser.getUid();

                if(!TextUtils.isEmpty(currentUserId)) {

                    friendsFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_FRIENDS_CHILD)
                                                                .child(currentUserId);

                    result = true;

                    //return setUserIsOnline(true);
                }
                else {

                    Log.e("LOG", "ParentActivity; initCurrentUser(): currentUserId is empty or null");
                }
            }
            else {

                Log.e("LOG", "ParentActivity; initCurrentUser(): currentUser is null");
            }
        }
        else {

            Log.e("LOG", "ParentActivity; initCurrentUser(): fbAuth is null");
        }

        return result;
    }

    public void showSnackBar(   View view,
                                int  messageResId,
                                int  duration) {

        if( (view != null)  &&
            (messageResId > 0)     &&
            (duration >= 0)) {

            Snackbar.make(view,
                    messageResId,
                    duration).show();
        }
    }

    public void showSnackBar(   View    view,
                                String  messageText,
                                int     duration) {

        if( (view != null)                          &&
                (!TextUtils.isEmpty(messageText))   &&
                (duration >= 0)) {

            Snackbar.make(  view,
                            messageText,
                            duration).show();
        }
    }

    public void showProgressDialog( String title,
                                    String message) {

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this,
                                                R.style.Theme_Sphinx_Dialog_Alert);

            progressDialog.setCanceledOnTouchOutside(false);
        }

        if(!TextUtils.isEmpty(title))
            progressDialog.setTitle(title);

        if(!TextUtils.isEmpty(message))
            progressDialog.setMessage(message);

        progressDialog.show();

        // ------------------------- STYLIZE EXISTING PROGRESS DIALOG ----------------------- //

        // Set title divider color
        int titleDividerId = getResources().getIdentifier(  "titleDivider",
                                                            "id",
                                                            "android");
        View titleDivider = progressDialog.findViewById(titleDividerId);

        if (titleDivider != null)
            titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public void hideProgressDialog() {
        progressDialog.hide();
    }

    public boolean currentUserExistsInFBDB() {

        if(!TextUtils.isEmpty(currentUserId)) {

            currentUserFBDatabaseRef = usersFBDatabaseRef.child(currentUserId);
            currentUserFBDatabaseRef.keepSynced(true);

            return (currentUserFBDatabaseRef != null);
        }
        else
            return false;
    }
}
