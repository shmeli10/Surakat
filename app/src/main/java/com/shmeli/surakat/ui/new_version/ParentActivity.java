package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shmeli.surakat.data.CONST;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public abstract class ParentActivity extends AppCompatActivity {

    private String currentUserId = "";

    private DatabaseReference rootFBDatabaseRef;
    private DatabaseReference usersFBDatabaseRef;
    private DatabaseReference currentUserFBDatabaseRef;

    // ------------------------------ GETTERS -------------------------------- //

    public String getCurrentUserId() {
        return currentUserId;
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

    // ------------------------------ SETTERS -------------------------------- //

    public abstract void setFragment(Fragment fragment, boolean animate, boolean addToBackStack);

    public boolean setUserIsOnline(boolean isOnline) {

        if(currentUserFBDatabaseRef != null) {

            // user is offline
            if (!isOnline) {
                currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
            }

            currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(isOnline);

            return true;
        }
        else {
            Log.e("LOG", "ParentActivity; setUserIsOnline(): currentUserFBDatabaseRef is null");

            return false;
        }
    }

    // ------------------------------ OTHER ---------------------------------- //

    public abstract void changeFragment(Fragment fragment);

//    public abstract void toggleLoadingProgress(final boolean show);

    protected boolean initCurrentUser() {

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        if(fbAuth != null) {

            rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
            usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

            FirebaseUser currentUser = fbAuth.getCurrentUser();

            if (currentUser != null) {

                currentUserId = currentUser.getUid();

                if(!TextUtils.isEmpty(currentUserId)) {

                    currentUserFBDatabaseRef = usersFBDatabaseRef.child(currentUserId);

                    return setUserIsOnline(true);
                }
                else {

                    Log.e("LOG", "ParentActivity; initCurrentUser(): currentUserId is empty or null");

                    return false;
                }
            }
            else {

                Log.e("LOG", "ParentActivity; initCurrentUser(): currentUser is null");

                return false;
            }
        }
        else {

            Log.e("LOG", "ParentActivity; initCurrentUser(): fbAuth is null");

            return false;
        }
    }
}
