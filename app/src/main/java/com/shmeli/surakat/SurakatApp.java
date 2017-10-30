package com.shmeli.surakat;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.SetAccountActivity;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Serghei Ostrovschi on 10/4/17.
 */

public class SurakatApp extends Application {

    private FirebaseAuth        fbAuth;
    private DatabaseReference   currentUserFBDatabaseRef;

    //private FirebaseUser        fbUser;

    private String              currentUserId = "";

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this,
                                                    Integer.MAX_VALUE));

            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);

            Picasso.setSingletonInstance(built);

//            fbAuth = FirebaseAuth.getInstance();
//
//            if(fbAuth.getCurrentUser() != null) {
//
//                currentUserId = fbAuth.getCurrentUser().getUid();
//
//                if(!TextUtils.isEmpty(currentUserId)) {
//
//                    currentUserFBDatabaseRef = FirebaseDatabase.getInstance().getReference().child(currentUserId);
//                    currentUserFBDatabaseRef.addListenerForSingleValueEvent(currentUserDataListener);
//                }
//                else
//                    Log.e("LOG", "SurakatApp: onCreate(): get current user id error");
//            }

//            fbUser = FirebaseAuth.getInstance().getCurrentUser();
//
//            if(fbUser != null) {
//
//                currentUserId       = fbUser.getUid();
//
//                if(!TextUtils.isEmpty(currentUserId)) {
//
//                    userFBDatabaseRef = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(currentUserId);
//
//                    userFBDatabaseRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            if (dataSnapshot != null) {
//
//                                userFBDatabaseRef.child(CONST.USER_IS_ONLINE).onDisconnect().setValue(false);
//                                //userFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener currentUserDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            //Log.e("LOG", "SurakatApp: currentUserDataListener: dataSnapshot is null: " +(dataSnapshot  == null));

            if(dataSnapshot != null) {

                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).onDisconnect().setValue(false);
                currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };


}
