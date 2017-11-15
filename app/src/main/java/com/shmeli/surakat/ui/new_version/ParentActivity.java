package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
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
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.fragments.FillAccountFragment;
import com.shmeli.surakat.ui.new_version.fragments.RegisterFragment;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public abstract class ParentActivity extends AppCompatActivity {

    private String currentUserId    = "";

    private String deviceToken      = "";

    private DatabaseReference rootFBDatabaseRef;
    private DatabaseReference usersFBDatabaseRef;
    private DatabaseReference currentUserFBDatabaseRef;

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

    public DatabaseReference getRootFBDatabaseRef() {
        return rootFBDatabaseRef;
    }

    public DatabaseReference getUsersFBDatabaseRef() {
        return usersFBDatabaseRef;
    }

    public DatabaseReference getCurrentUserFBDatabaseRef() {
        return currentUserFBDatabaseRef;
    }

    public FirebaseAuth getFBAuth() {

        if(fbAuth == null)
            fbAuth = FirebaseAuth.getInstance();

        return fbAuth;
    }

    // ------------------------------ SETTERS -------------------------------- //

    protected void changeFragmentTo(Fragment    fragment,
                                    boolean     animate,
                                    boolean     addToBackStack) {
        Log.e("LOG", "ParentActivity: changeFragmentTo()");

//        if(fragment != null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (animate) {
                transaction.setCustomAnimations(R.animator.slide_in,
                        R.animator.slide_out,
                        R.animator.slide_in,
                        R.animator.slide_out);
            } else {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }

            transaction.replace(R.id.externalActivityFragmentsContainer,
                    fragment,
                    fragment.getClass().getName());

            if (addToBackStack)
                transaction.addToBackStack(fragment.getClass().getName());

            transaction.commit();
//        }
//        else {
//            Log.e("LOG", "ParentActivity: changeFragmentTo(): fragment is null");
//        }
    }

    public abstract void setFragment(int         fragmentCode,
                                     boolean     animate,
                                     boolean     addToBackStack);

//    public void setFragment(Fragment    fragment,
//    public void setFragment(int         fragmentCode,
//                            boolean     animate,
//                            boolean     addToBackStack) {
//
//        Fragment fragment = null;
//
//        switch(fragmentCode) {
//
//            case CONST.FILL_ACCOUNT_FRAGMENT:
//                fragment = FillAccountFragment.newInstance();
//                break;
//            case CONST.REGISTER_FRAGMENT:
//                fragment = RegisterFragment.newInstance();
//                break;
//            case CONST.SIGN_IN_FRAGMENT:
//                fragment = SignInFragment.newInstance();
//                break;
//            default:
//                Log.e("LOG", "ParentActivity: setFragment(): undefined fragment code: " +fragmentCode);
//        }
//
//        if(fragment != null) {
//
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//            if (animate) {
//                transaction.setCustomAnimations(R.animator.slide_in,
//                        R.animator.slide_out,
//                        R.animator.slide_in,
//                        R.animator.slide_out);
//            } else {
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            }
//
//            transaction.replace(R.id.externalActivityFragmentsContainer,
//                                fragment,
//                                fragment.getClass().getName());
//
//            if (addToBackStack)
//                transaction.addToBackStack(fragment.getClass().getName());
//
//            transaction.commit();
//        }
//        else {
//            Log.e("LOG", "ParentActivity: setFragment(): fragment is null");
//        }
//    }

    public abstract void setToolbarTitle(final int titleResId);

    public void setCurrentUserIsOnline(boolean isOnline) {

        if(currentUserFBDatabaseRef != null) {

            // user is offline
            if (!isOnline) {
                currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
            }

            currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(isOnline);
        }
        else {
            Log.e("LOG", "ParentActivity; setUserIsOnline(): currentUserFBDatabaseRef is null");
        }
    }

    // ------------------------------ OTHER ---------------------------------- //

    public abstract void changeFragment(Fragment fragment);

//    public abstract void toggleLoadingProgress(final boolean show);

    public boolean initCurrentUser() {
        Log.e("LOG", "ParentActivity: initCurrentUser()");

        boolean result = false;

        if(getFBAuth() != null) {

            rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
            usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

            FirebaseUser currentUser = getFBAuth().getCurrentUser();

            if (currentUser != null) {

                currentUserId = currentUser.getUid();

                if(!TextUtils.isEmpty(currentUserId)) {

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
                                int  resId,
                                int  duration) {

        if( (view != null)  &&
            (resId > 0)     &&
            (duration >= 0)) {

            Snackbar.make(view,
                    resId,
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

            return (currentUserFBDatabaseRef != null);
        }
        else
            return false;
    }
}