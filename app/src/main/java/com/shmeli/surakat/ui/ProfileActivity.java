package com.shmeli.surakat.ui;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar             profilePageToolbar;
    //private ProgressDialog      progressDialog;

    private RelativeLayout      profileContainer;

    private CircleImageView     profilePageAvatar;

    private TextView            profilePageUserName;
    private TextView            profilePageUserStatus;
    private TextView            profilePageFriendsCount;

    private Button              profilePageSendRequest;
    private Button              profilePageDeclineRequest;

    private DatabaseReference   friendsFBDatabaseRef;
    private DatabaseReference   friendRequestFBDatabaseRef;
    //private DatabaseReference   notificationsFBDatabaseRef;
    private DatabaseReference   rootFBDatabaseRef;
    private DatabaseReference   userFBDatabaseRef;

    //private FirebaseUser        currentFBUser;

    private String currentUserId    = "";
    private String selectedUserId   = "";
    private String userImageUrl     = "";

    private int friendshipState     = -1;

    //private boolean canChangeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();

        if(profileIntent != null) {

            //currentFBUser   = FirebaseAuth.getInstance().getCurrentUser();

            currentUserId   = FirebaseAuth.getInstance().getCurrentUser().getUid();
            selectedUserId  = profileIntent.getStringExtra(CONST.USER_ID);

            friendshipState = CONST.IS_NOT_A_FRIEND_STATE;

            //Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId= " +selectedUserId);
        }
        else {
            Log.e("LOG", "ProfileActivity: onCreate(): profileIntent is null");
        }

//        progressDialog = new ProgressDialog(ProfileActivity.this);
//        progressDialog.setTitle(getResources().getString(R.string.message_loading_profile_data));
//        progressDialog.setMessage(getResources().getString(R.string.message_loading_profile_data_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        rootFBDatabaseRef           = FirebaseDatabase.getInstance().getReference();

        friendsFBDatabaseRef        = rootFBDatabaseRef.child(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequestFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        //notificationsFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_NOTIFICATIONS_CHILD);

        Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId is null: " +(selectedUserId == null));

        if(selectedUserId != null) {

            Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId= " + selectedUserId);

            userFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD).child(selectedUserId);

            if(userFBDatabaseRef != null) {

                userFBDatabaseRef.keepSynced(true);
                userFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);
            }
            else {
                Log.e("LOG", "ProfileActivity: onCreate(): userFBDatabaseRef is null: " +(userFBDatabaseRef == null));
            }
        }
        else {
            Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId is null");
        }

        profilePageToolbar = UiUtils.findView(this, R.id.profilePageToolbar);
        setSupportActionBar(profilePageToolbar);
        getSupportActionBar().setTitle(R.string.text_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profilePageAvatar       = UiUtils.findView(this, R.id.profilePageAvatar);
        profilePageUserName     = UiUtils.findView(this, R.id.profilePageUserName);
        profilePageUserStatus   = UiUtils.findView(this, R.id.profilePageUserStatus);
        profilePageFriendsCount = UiUtils.findView(this, R.id.profilePageFriendsCount);

        profilePageSendRequest  = UiUtils.findView(this, R.id.profilePageSendRequest);
        profilePageSendRequest.setOnClickListener(sendRequestClickListener);
        //showSendRequestButton();

        profilePageDeclineRequest = UiUtils.findView(this, R.id.profilePageDeclineRequest);
        profilePageDeclineRequest.setOnClickListener(declineRequestClickListener);
        hideDeclineButton();

        profileContainer        = UiUtils.findView(this, R.id.profileContainer);
    }

    private void sendFriendRequest() {

        DatabaseReference getNotificationPushKeyFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_NOTIFICATIONS_CHILD)
                .child(selectedUserId).push();

        String notificationId = getNotificationPushKeyFBDatabaseRef.getKey();

        StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        Map requestMap = new HashMap();
        //requestMap.put(currentUserId + "/" + selectedUserId + CONST.NOTIFICATION_REQUEST_TYPE,  CONST.SENT_REQUEST_TEXT);
        requestMap.put(friendRequest.toString(),  CONST.SENT_REQUEST_TEXT);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_ID);

        //requestMap.put(currentUserId + "/" + selectedUserId + CONST.REQUEST_TYPE_ID,            CONST.SENT_REQUEST_STATE);
        requestMap.put(friendRequest.toString(),  CONST.SENT_REQUEST_STATE);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.NOTIFICATION_ID);

        //requestMap.put(currentUserId + "/" + selectedUserId + CONST.REQUEST_TYPE_ID,            CONST.SENT_REQUEST_STATE);
        requestMap.put(friendRequest.toString(),  notificationId);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        //requestMap.put(selectedUserId + "/" + currentUserId + CONST.NOTIFICATION_REQUEST_TYPE,  CONST.RECEIVED_REQUEST_TEXT);
        requestMap.put(friendRequest.toString(),  CONST.RECEIVED_REQUEST_TEXT);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_ID);

        //requestMap.put(selectedUserId + "/" + currentUserId + CONST.REQUEST_TYPE_ID,            CONST.RECEIVED_REQUEST_STATE);
        requestMap.put(friendRequest.toString(),  CONST.RECEIVED_REQUEST_STATE);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        HashMap<String, String> notificationDataMap = new HashMap<>();
        //notificationDataMap.put(CONST.NOTIFICATION_SENDER_ID,   currentFBUser.getUid());
        notificationDataMap.put(CONST.NOTIFICATION_SENDER_ID,   currentUserId);
        notificationDataMap.put(CONST.NOTIFICATION_TYPE,        CONST.NOTIFICATION_REQUEST_TYPE);

        friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(notificationId);

        //requestMap.put(selectedUserId + "/" + currentUserId + CONST.REQUEST_TYPE_ID,            CONST.RECEIVED_REQUEST_STATE);
        requestMap.put(friendRequest.toString(),  notificationDataMap);
        friendRequest.setLength(0);

        rootFBDatabaseRef.updateChildren(requestMap, sendFriendRequestCompletionListener);
    }

    private void cancelSentFriendRequest() {

        final StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);

        final Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(selectedUserId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

                //Log.e("LOG", "ProfileActivity: cancelSentFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

                    //Log.e("LOG", "ProfileActivity: cancelSentFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(selectedUserId);
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    rootFBDatabaseRef.updateChildren(requestMap, declineFriendRequestCompletionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

//        friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
//        friendRequest.append("/");
//        friendRequest.append(selectedUserId);
//        friendRequest.append("/");
//        friendRequest.append(currentUserId);
//
//        requestMap.put(friendRequest.toString(),  null);
//        friendRequest.setLength(0);
//
//        rootFBDatabaseRef.updateChildren(requestMap, cancelSentFriendRequestCompletionListener);
    }

    private void sendUnFriendRequest(){

        StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);

        Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        rootFBDatabaseRef.updateChildren(requestMap, unFriendRequestCompletionListener);
    }

    private void acceptFriendRequest() {

        final StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.FRIENDSHIP_START_DATE);

        final Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  getCurrentDate());
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.FRIENDSHIP_START_DATE);

        requestMap.put(friendRequest.toString(),  getCurrentDate());
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
//        friendRequest.append("/");
//        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
//        friendRequest.append("/");
//        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // rootFBDatabaseRef.updateChildren(requestMap, acceptRequestCompletionListener);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(currentUserId).child(selectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

                //Log.e("LOG", "ProfileActivity: acceptFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

                    //Log.e("LOG", "ProfileActivity: acceptFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(currentUserId);
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    rootFBDatabaseRef.updateChildren(requestMap, acceptRequestCompletionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void declineFriendRequest() {

        final StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(currentUserId);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);

        final Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(currentUserId);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        //rootFBDatabaseRef.updateChildren(requestMap, declineFriendRequestCompletionListener);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(currentUserId).child(selectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

                Log.e("LOG", "ProfileActivity: declineFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

                    Log.e("LOG", "ProfileActivity: declineFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(currentUserId);
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    rootFBDatabaseRef.updateChildren(requestMap, declineFriendRequestCompletionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private String getCurrentDate() {

        SimpleDateFormat simpleDateFormat   = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        String currentDate                  = simpleDateFormat.format(new Date());

        return currentDate;
    }

//    private void removeFriendRequestFromFBDatabase() {
//
//        //friendRequestFBDatabaseRef.child(currentFBUser.getUid())
//        friendRequestFBDatabaseRef.child(currentUserId)
//                .child(selectedUserId)
//                .removeValue()
//                .addOnCompleteListener(onCancelSentFriendRequestMyPartCompleteListener);
//    }

    private void showDeclineButton() {

        // enable and show decline button
        profilePageDeclineRequest.setVisibility(View.VISIBLE);
        profilePageDeclineRequest.setEnabled(true);
    }

    private void hideDeclineButton() {

        // disable and hide decline button
        profilePageDeclineRequest.setVisibility(View.INVISIBLE);
        profilePageDeclineRequest.setEnabled(false);
    }

    private void showSendRequestButton() {

        // enable and show send request button
        profilePageSendRequest.setVisibility(View.VISIBLE);
        profilePageSendRequest.setEnabled(true);
    }

    private void hideSendRequestButton() {

        // disable and hide send request button
        profilePageSendRequest.setVisibility(View.INVISIBLE);
        profilePageSendRequest.setEnabled(false);
    }

    // ------------------------------ VALUE EVENT LISTENERS ----------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                //Log.e("LOG", "ProfileActivity: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

                String userName = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                String userStatus = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();
                userImageUrl = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

                profilePageUserName.setText(userName);
                profilePageUserStatus.setText(userStatus);

                if (!userImageUrl.equals(CONST.DEFAULT_VALUE)) {
                    Picasso.with(ProfileActivity.this)
                            .load(userImageUrl)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(profilePageAvatar,
                                    loadImageCallback);

                /*Picasso.with(ProfileActivity.this)
                        .load(userImageUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(profilePageAvatar);*/
                }

                // GET LIST OF FRIEND REQUESTS
                //friendRequestFBDatabaseRef.child(currentFBUser.getUid())
                friendRequestFBDatabaseRef.child(currentUserId)
                        .addListenerForSingleValueEvent(friendRequestsListValueListener);
            }
            else {
                Log.e("LOG", "ProfileActivity: valueEventListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener friendRequestsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot.hasChild(selectedUserId)) {

                int requestTypeId = Integer.valueOf(dataSnapshot.child(selectedUserId).child(CONST.REQUEST_TYPE_ID).getValue().toString());

                switch (requestTypeId) {

                    // ACCEPT FRIEND REQUEST
                    case CONST.RECEIVED_REQUEST_STATE:
                        friendshipState = CONST.RECEIVED_REQUEST_STATE;
                        profilePageSendRequest.setText(R.string.text_accept_friend_request);

                        showDeclineButton();
                        break;
                    // CANCEL SENT FRIEND REQUEST
                    case CONST.SENT_REQUEST_STATE:
                        friendshipState = CONST.SENT_REQUEST_STATE;
                        profilePageSendRequest.setText(R.string.text_cancel_friend_request);

                        hideDeclineButton();
                        break;
                }

                //showSendRequestButton();

                //progressDialog.dismiss();
            }
            else {

                //friendsFBDatabaseRef.child(currentFBUser.getUid())
                friendsFBDatabaseRef.child(currentUserId)
                        .addListenerForSingleValueEvent(currentUserFriendsListValueListener);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener currentUserFriendsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot.hasChild(selectedUserId)) {

                friendshipState = CONST.IS_A_FRIEND_STATE;
                profilePageSendRequest.setText(R.string.text_unfriend_request);

                hideDeclineButton();
                //showSendRequestButton();
            }
//            else {
//
//                hideSendRequestButton();
//            }

            //hideDeclineButton();
            //progressDialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //progressDialog.dismiss();
        }
    };

    // ------------------------------ ON CLICK LISTENER ------------------------------------- //

    View.OnClickListener sendRequestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            profilePageSendRequest.setEnabled(false);

            //Log.e("LOG", "ProfileActivity: sendRequestClickListener: friendshipState= " +friendshipState);

            switch(friendshipState) {

                // NOT FRIENDS, SEND FRIEND REQUEST
                case CONST.IS_NOT_A_FRIEND_STATE:
                    //Log.e("LOG", "ProfileActivity: sendRequestClickListener: IS_NOT_A_FRIEND_STATE");

                    sendFriendRequest();
                    break;
                // NOT FRIENDS, CANCEL SENT FRIEND REQUEST
                case CONST.SENT_REQUEST_STATE:
                    //Log.e("LOG", "ProfileActivity: sendRequestClickListener: SENT_REQUEST_STATE");

                    cancelSentFriendRequest();

//                    canChangeState = true;
//                    removeFriendRequestFromFBDatabase();
                    break;
                // NOT FRIENDS, ACCEPT FRIEND REQUEST
                case CONST.RECEIVED_REQUEST_STATE:
                    //Log.e("LOG", "ProfileActivity: sendRequestClickListener: RECEIVED_REQUEST_STATE");

                    acceptFriendRequest();

//                    friendsFBDatabaseRef.child(currentUserId)
//                            .child(selectedUserId)
//                            .setValue(getCurrentDate())
//                            .addOnCompleteListener(onAcceptFriendRequestMyPartCompleteListener);
                    break;
                // FRIENDS, SEND UN FRIEND REQUEST
                case CONST.IS_A_FRIEND_STATE:
                    //Log.e("LOG", "ProfileActivity: sendRequestClickListener: IS_A_FRIEND_STATE");

                    sendUnFriendRequest();

                    //friendsFBDatabaseRef.child(currentFBUser.getUid())
//                    friendsFBDatabaseRef.child(currentUserId)
//                            .child(selectedUserId)
//                            .removeValue()
//                            .addOnCompleteListener(onUnfriendRequestMyPartCompleteListener);

                    break;
            }
        }
    };

    View.OnClickListener declineRequestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            declineFriendRequest();

//            canChangeState = true;
//            removeFriendRequestFromFBDatabase();

//            //removeFriendRequestFromFBDatabase();
//
//            friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
//            profilePageSendRequest.setEnabled(true);
//            profilePageSendRequest.setText(R.string.text_unfriend_request);
//
//            hideDeclineButton();
        }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    DatabaseReference.CompletionListener sendFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError != null) {

                Snackbar.make(  profileContainer,
                        R.string.error_send_friend_request,
                        Snackbar.LENGTH_LONG).show();
            }

            showSendRequestButton();

            friendshipState = CONST.SENT_REQUEST_STATE;
            profilePageSendRequest.setText(R.string.text_cancel_friend_request);
        }
    };

    DatabaseReference.CompletionListener cancelSentFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                showSendRequestButton();

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
                profilePageSendRequest.setText(R.string.text_send_friend_request);

                //hideDeclineButton();
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    Snackbar.make(profileContainer,
                            error,
                            Snackbar.LENGTH_LONG).show();
                }
                else {

                    Snackbar.make(  profileContainer,
                            R.string.error_cancel_sent_friend_request,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    DatabaseReference.CompletionListener unFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
                profilePageSendRequest.setText(R.string.text_send_friend_request);

                hideDeclineButton();
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    Snackbar.make(profileContainer,
                            error,
                            Snackbar.LENGTH_LONG).show();
                }
                else {

                    Snackbar.make(  profileContainer,
                            R.string.error_unfriend_request,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            showSendRequestButton();
        }
    };

    DatabaseReference.CompletionListener acceptRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                showSendRequestButton();

                friendshipState = CONST.IS_A_FRIEND_STATE;
                profilePageSendRequest.setText(R.string.text_unfriend_request);

                hideDeclineButton();
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    Snackbar.make(profileContainer,
                            error,
                            Snackbar.LENGTH_LONG).show();
                }
                else {

                    Snackbar.make(  profileContainer,
                            R.string.error_accept_friend_request,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    DatabaseReference.CompletionListener declineFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
                profilePageSendRequest.setText(R.string.text_send_friend_request);

                hideDeclineButton();
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    Snackbar.make(profileContainer,
                            error,
                            Snackbar.LENGTH_LONG).show();
                }
                else {

                    Snackbar.make(  profileContainer,
                            R.string.error_decline_friend_request,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            showSendRequestButton();
        }
    };

    Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

            Picasso.with(ProfileActivity.this)
                    .load(userImageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(profilePageAvatar);
        }
    };

/*    // SEND FRIEND REQUEST RESULT (CURRENT USER PART)
    OnCompleteListener<Void> onSendFriendRequestMyPartCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            //Log.e("LOG", "ProfileActivity: onSendFriendRequestCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                Map requestTypeMap = new HashMap();
                requestTypeMap.put(CONST.REQUEST_TYPE_TEXT, CONST.RECEIVED_REQUEST_TEXT);
                requestTypeMap.put(CONST.REQUEST_TYPE_ID,   CONST.RECEIVED_REQUEST_STATE);

                friendRequestFBDatabaseRef.child(selectedUserId)
                        //.child(currentFBUser.getUid())
                        .child(currentUserId)
                        .setValue(requestTypeMap)
                        .addOnCompleteListener(onSendFriendRequestSelectedUserPartCompleteListener);
            }
            else {
                //progressDialog.hide();

                Snackbar.make(  profileContainer,
                                R.string.error_send_friend_request,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    // SEND FRIEND REQUEST RESULT (SELECTED USER PART)
    OnCompleteListener<Void> onSendFriendRequestSelectedUserPartCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            //Log.e("LOG", "ProfileActivity: onSendFriendRequestSelectedUserPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                HashMap<String, String> notificationDataMap = new HashMap<>();
                //notificationDataMap.put(CONST.NOTIFICATION_SENDER_ID,   currentFBUser.getUid());
                notificationDataMap.put(CONST.NOTIFICATION_SENDER_ID,   currentUserId);
                notificationDataMap.put(CONST.NOTIFICATION_TYPE,        CONST.NOTIFICATION_REQUEST_TYPE);

                notificationsFBDatabaseRef.child(selectedUserId)
                        .push()
                        .setValue(notificationDataMap)
                        .addOnCompleteListener(onSendNotificationRequestCompleteListener);

                //Log.e("LOG", "ProfileActivity: onSendFriendRequestSelectedUserPartCompleteListener: success");
            }
            else {

                Snackbar.make(  profileContainer,
                        R.string.error_receive_friend_request,
                        Snackbar.LENGTH_LONG).show();
            }

            profilePageSendRequest.setEnabled(true);
        }
    };*/

    // SEND NOTIFICATION REQUEST RESULT
//    OnCompleteListener<Void> onSendNotificationRequestCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onSendNotificationRequestCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                friendshipState = CONST.SENT_REQUEST_STATE;
//                profilePageSendRequest.setText(R.string.text_cancel_friend_request);
//
//                hideDeclineButton();
//
////                friendsFBDatabaseRef.child(selectedUserId)
////                        .child(currentFBUser.getUid())
////                        .removeValue()
////                        .addOnCompleteListener(onUnfriendRequestSelectedUserPartCompleteListener);
//
//                //Log.e("LOG", "ProfileActivity: onSendNotificationRequestCompleteListener: friendshipState=" +friendshipState);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                        R.string.error_send_notification,
//                        Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // ON CANCEL SENT FRIEND REQUEST RESULT (CURRENT USER PART)
//    OnCompleteListener<Void> onCancelSentFriendRequestMyPartCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onCancelSentFriendRequestMyPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                friendRequestFBDatabaseRef.child(selectedUserId)
//                        //.child(currentFBUser.getUid())
//                        .child(currentUserId)
//                        .removeValue()
//                        .addOnCompleteListener(onCancelSentFriendRequestSelectedUserPartCompleteListener);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                        R.string.error_cancel_sent_friend_request,
//                        Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // ON CANCEL SENT FRIEND REQUEST RESULT (SELECTED USER PART)
//    OnCompleteListener<Void> onCancelSentFriendRequestSelectedUserPartCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onCancelSentFriendRequestSelectedUserPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                if(canChangeState) {
//                    canChangeState = false;
//
//                    friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
//                    profilePageSendRequest.setEnabled(true);
//                    profilePageSendRequest.setText(R.string.text_send_friend_request);
//
//                    hideDeclineButton();
//                }
//
//                //Log.e("LOG", "ProfileActivity: onCancelSentFriendRequestSelectedUserPartCompleteListener: friendshipState=" +friendshipState);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                        R.string.error_cancel_sent_friend_request,
//                        Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // ACCEPT FRIEND REQUEST RESULT (CURRENT USER PART)
//    OnCompleteListener<Void> onAcceptFriendRequestMyPartCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onAcceptFriendRequestMyPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                friendsFBDatabaseRef.child(selectedUserId)
//                        //.child(currentFBUser.getUid())
//                        .child(currentUserId)
//                        .setValue(getCurrentDate())
//                        .addOnCompleteListener(onAcceptFriendRequestSelectedUserPartCompleteListener);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                                R.string.error_accept_friend_request,
//                                Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // ACCEPT FRIEND REQUEST RESULT (SELECTED USER PART)
//    OnCompleteListener<Void> onAcceptFriendRequestSelectedUserPartCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onAcceptFriendRequestSelectedUserPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                removeFriendRequestFromFBDatabase();
//
//                friendshipState = CONST.IS_A_FRIEND_STATE;
//                profilePageSendRequest.setEnabled(true);
//                profilePageSendRequest.setText(R.string.text_unfriend_request);
//
//                hideDeclineButton();
//
//                //Log.e("LOG", "ProfileActivity: onAcceptFriendRequestSelectedUserPartCompleteListener: friendshipState=" +friendshipState);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                        R.string.error_accept_friend_request,
//                        Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // UNFRIEND REQUEST RESULT (CURRENT USER PART)
//    OnCompleteListener<Void> onUnfriendRequestMyPartCompleteListener = new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//
//            //Log.e("LOG", "ProfileActivity: onUnfriendRequestMyPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());
//
//            if(task.isSuccessful()) {
//
//                friendsFBDatabaseRef.child(selectedUserId)
//                        //.child(currentFBUser.getUid())
//                        .child(currentUserId)
//                        .removeValue()
//                        .addOnCompleteListener(onUnfriendRequestSelectedUserPartCompleteListener);
//
//                //Log.e("LOG", "ProfileActivity: onUnfriendRequestMyPartCompleteListener: friendshipState=" +friendshipState);
//            }
//            else {
//
//                Snackbar.make(  profileContainer,
//                        R.string.error_unfriend_request,
//                        Snackbar.LENGTH_LONG).show();
//            }
//        }
//    };

    // UNFRIEND REQUEST RESULT (SELECTED USER PART)
/*    OnCompleteListener<Void> onUnfriendRequestSelectedUserPartCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            //Log.e("LOG", "ProfileActivity: onUnfriendRequestSelectedUserPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
                profilePageSendRequest.setEnabled(true);
                profilePageSendRequest.setText(R.string.text_send_friend_request);

                //Log.e("LOG", "ProfileActivity: onUnfriendRequestSelectedUserPartCompleteListener: friendshipState=" +friendshipState);
            }
            else {

                Snackbar.make(  profileContainer,
                        R.string.error_unfriend_request,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };*/
}
