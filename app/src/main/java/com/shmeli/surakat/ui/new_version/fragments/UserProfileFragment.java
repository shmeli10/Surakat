package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.text.TextUtils;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Serghei Ostrovschi on 11/16/17.
 */

public class UserProfileFragment extends ParentFragment {

    private static UserProfileFragment  instance;

    private View                view;

    private RelativeLayout      userProfileContainer;

    private CircleImageView     avatarImageView;

    private TextView            nameTextView;
    private TextView            statusTextView;
    private TextView            friendsCountTitleTextView;
    private TextView            friendsCountValueTextView;

    private Button              sendRequestButton;
    private Button              declineRequestButton;

    private DatabaseReference   friendRequestFBDatabaseRef;
    private DatabaseReference   friendsFBDatabaseRef;
    private DatabaseReference   notificationsFBDatabaseRef;
    private DatabaseReference   selectedUserFBDatabaseRef;

    private InternalActivity    internalActivity;

    private String              selectedUserId          = "";
    private String              selectedUserImageUrl    = "";

    private int     friendshipState = -1;

    private int     friendsCountValue = 0;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfileFragment.
     */
    public static UserProfileFragment newInstance(String selectedUserId) {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new UserProfileFragment();
        }

//        Log.e("LOG", "UserProfileFragment: newInstance(): selectedUserId= " +selectedUserId);

        if(!TextUtils.isEmpty(selectedUserId)) {
            args.putString( CONST.USER_ID,
                            selectedUserId);
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "UserProfileFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_user_profile,
                                container,
                                false);

//        Log.e("LOG", "UserProfileFragment: onCreateView(): getArguments().containsKey(\"selectedUserId\"): " +(getArguments().containsKey("selectedUserId")));

        if(getArguments().containsKey(CONST.USER_ID)) {

            this.selectedUserId = getArguments().getString(CONST.USER_ID);

            userProfileContainer    = UiUtils.findView( view,
                                                        R.id.profileContainer);

            avatarImageView         = UiUtils.findView( view,
                                                        R.id.userProfileAvatar);

            nameTextView            = UiUtils.findView( view,
                                                        R.id.userProfileName);

            statusTextView          = UiUtils.findView( view,
                                                        R.id.userProfileStatus);

            friendsCountTitleTextView = UiUtils.findView(   view,
                                                            R.id.userProfileFriendsCountTitle);

            friendsCountValueTextView = UiUtils.findView(   view,
                                                            R.id.userProfileFriendsCountValue);

            sendRequestButton       = UiUtils.findView( view,
                                                        R.id.userProfileSendRequest);
            sendRequestButton.setOnClickListener(sendRequestClickListener);

            declineRequestButton    = UiUtils.findView( view,
                                                        R.id.userProfileDeclineRequest);
            declineRequestButton.setOnClickListener(declineRequestClickListener);

            init();

//            Log.e("LOG", "UserProfileFragment: onCreateView(): selectedUserId= " +selectedUserId);
        }
        else {
            Log.e("LOG", "UserProfileFragment: onCreateView(): parameter selectedUserId does not exist in Bundle");
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // ----------------------------------- INIT ----------------------------------------- //

    private void init() {
        Log.e("LOG", "UserProfileFragment: init()");

        internalActivity    = (InternalActivity) getActivity();

        friendshipState     = CONST.IS_NOT_A_FRIEND_STATE;

        if(!internalActivity.getCurrentUserId().equals(selectedUserId)) {
            showSendRequestButton(R.string.text_send_friend_request);
        }

        friendsFBDatabaseRef        = internalActivity.getRootFBDatabaseRef()
                                                        .child(CONST.FIREBASE_FRIENDS_CHILD);

        friendRequestFBDatabaseRef  = internalActivity.getRootFBDatabaseRef()
                                                        .child(CONST.FIREBASE_FRIEND_REQUEST_CHILD);

        selectedUserFBDatabaseRef   = internalActivity.getRootFBDatabaseRef()
                                                        .child(CONST.FIREBASE_USERS_CHILD)
                                                        .child(selectedUserId);

        if(selectedUserFBDatabaseRef != null) {

            selectedUserFBDatabaseRef.keepSynced(true);
            selectedUserFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);
        }
        else {
            Log.e("LOG", "UserProfileFragment: init(): selectedUserFBDatabaseRef is null");
        }

        notificationsFBDatabaseRef  = internalActivity.getRootFBDatabaseRef()
                                                        .child(CONST.FIREBASE_NOTIFICATIONS_CHILD)
                                                        .child(selectedUserId);
    }

    // ------------------------------ VALUE EVENT LISTENERS ----------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                //Log.e("LOG", "UserProfileFragment: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

                String selectedUserName     = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                String selectedUserStatus   = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                selectedUserImageUrl        = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

                if( (!TextUtils.isEmpty(selectedUserName)) &&
                    (!selectedUserName.equals(CONST.DEFAULT_VALUE))) {

                    nameTextView.setText(selectedUserName);
                }

                if( (!TextUtils.isEmpty(selectedUserStatus)) &&
                        (!selectedUserStatus.equals(CONST.DEFAULT_VALUE))) {

                    statusTextView.setText(selectedUserStatus);
                }

                if (!selectedUserImageUrl.equals(CONST.DEFAULT_VALUE)) {
                    Picasso.with(getActivity())
                            .load(selectedUserImageUrl)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(  avatarImageView,
                                    loadImageCallback);
                }

                // GET LIST OF FRIEND REQUESTS
                friendRequestFBDatabaseRef.child(internalActivity.getCurrentUserId())
                                            .addListenerForSingleValueEvent(friendRequestsListValueListener);
            }
            else {
                Log.e("LOG", "UserProfileFragment: selectedUserProfileValueListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener friendRequestsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

//            Log.e("LOG", "UserProfileFragment: friendRequestsListValueListener: dataSnapshot.hasChild(" +selectedUserId+"): " +(dataSnapshot.hasChild(selectedUserId)));

            if(dataSnapshot.hasChild(selectedUserId)) {

                int requestTypeId = Integer.valueOf(dataSnapshot.child(selectedUserId).child(CONST.REQUEST_TYPE_ID).getValue().toString());

                switch (requestTypeId) {

                    // ACCEPT FRIEND REQUEST
                    case CONST.RECEIVED_REQUEST_STATE:

                        friendshipState = CONST.RECEIVED_REQUEST_STATE;

                        showSendRequestButton(R.string.text_accept_friend_request);

                        showDeclineButton();
                        break;
                    // CANCEL SENT FRIEND REQUEST
                    case CONST.SENT_REQUEST_STATE:

                        friendshipState = CONST.SENT_REQUEST_STATE;

                        showSendRequestButton(R.string.text_cancel_friend_request);
//                        profilePageSendRequest.setText(R.string.text_cancel_friend_request);

                        hideDeclineButton();
                        break;
                }

                friendsFBDatabaseRef.child(selectedUserId)
                        .addListenerForSingleValueEvent(selectedUserFriendsListValueListener);
            }
            else {

//                Log.e("LOG", "UserProfileFragment: friendRequestsListValueListener: friendsFBDatabaseRef.child(" +internalActivity.getCurrentUserId()+ ") is null: " +(friendsFBDatabaseRef.child(internalActivity.getCurrentUserId()) == null));

                if(friendsFBDatabaseRef.child(internalActivity.getCurrentUserId()) != null) {
                    friendsFBDatabaseRef.child(internalActivity.getCurrentUserId())
                            .addListenerForSingleValueEvent(currentUserFriendsListValueListener);
                }
                else {

                    friendsCountValue = 0;
                    friendsCountValueTextView.setText(String.valueOf(friendsCountValue));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener currentUserFriendsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

//            Log.e("LOG", "UserProfileFragment: currentUserFriendsListValueListener: friends sum = " +dataSnapshot.getChildrenCount());

            if(dataSnapshot.getChildrenCount() >= 0) {

                friendsCountValue = (int) dataSnapshot.getChildrenCount();
            }
            else{

                friendsCountValue = 0;
            }
            friendsCountValueTextView.setText(String.valueOf(friendsCountValue));

            if(dataSnapshot.hasChild(selectedUserId)) {

                friendshipState = CONST.IS_A_FRIEND_STATE;

                showSendRequestButton(R.string.text_unfriend_request);

                hideDeclineButton();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //progressDialog.dismiss();
        }
    };

    ValueEventListener selectedUserFriendsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

//            Log.e("LOG", "UserProfileFragment: selectedUserFriendsListValueListener: friends sum = " +dataSnapshot.getChildrenCount());

            if(dataSnapshot.getChildrenCount() >= 0) {

                friendsCountValue = (int) dataSnapshot.getChildrenCount();
            }
            else{

                friendsCountValue = 0;
            }
            friendsCountValueTextView.setText(String.valueOf(friendsCountValue));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //progressDialog.dismiss();
        }
    };

    // ------------------------------ BUTTON CLICK LISTENER ------------------------------------- //

    View.OnClickListener sendRequestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            disableSendRequestButton();

//            Log.e("LOG", "UserProfileFragment: sendRequestClickListener: friendshipState= " +friendshipState);

            switch(friendshipState) {

                // NOT FRIENDS, SEND FRIEND REQUEST
                case CONST.IS_NOT_A_FRIEND_STATE:
//                    Log.e("LOG", "UserProfileFragment: sendRequestClickListener: IS_NOT_A_FRIEND_STATE");

                    sendFriendRequest();
                    break;
                // NOT FRIENDS, CANCEL SENT FRIEND REQUEST
                case CONST.SENT_REQUEST_STATE:
//                    Log.e("LOG", "UserProfileFragment: sendRequestClickListener: SENT_REQUEST_STATE");

                    cancelSentFriendRequest();
                    break;
                // NOT FRIENDS, ACCEPT FRIEND REQUEST
                case CONST.RECEIVED_REQUEST_STATE:
//                    Log.e("LOG", "UserProfileFragment: sendRequestClickListener: RECEIVED_REQUEST_STATE");

                    acceptFriendRequest();
                    break;
                // FRIENDS, SEND UN FRIEND REQUEST
                case CONST.IS_A_FRIEND_STATE:
//                    Log.e("LOG", "UserProfileFragment: sendRequestClickListener: IS_A_FRIEND_STATE");

                    sendUnFriendRequest();
                    break;
            }
        }
    };

    View.OnClickListener declineRequestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            declineFriendRequest();
        }
    };

    // ----------------------------------- FRIEND REQUEST ------------------------------------- //

    private void sendFriendRequest() {

        Log.e("LOG", "UserProfileFragment: sendFriendRequest()");

        DatabaseReference getNotificationPushKeyFBDatabaseRef = notificationsFBDatabaseRef.push();

        String notificationId = getNotificationPushKeyFBDatabaseRef.getKey();

//        DatabaseReference getNotificationPushKeyFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_NOTIFICATIONS_CHILD)
//                .child(selectedUserId).push();

        StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  CONST.SENT_REQUEST_TEXT);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_ID);

        requestMap.put(friendRequest.toString(),  CONST.SENT_REQUEST_STATE);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(CONST.NOTIFICATION_ID);

        requestMap.put(friendRequest.toString(),  notificationId);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_TEXT);

        requestMap.put(friendRequest.toString(),  CONST.RECEIVED_REQUEST_TEXT);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(CONST.REQUEST_TYPE_ID);

        requestMap.put(friendRequest.toString(),  CONST.RECEIVED_REQUEST_STATE);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        HashMap<String, String> notificationDataMap = new HashMap<>();
        notificationDataMap.put(CONST.NOTIFICATION_SENDER_ID,   internalActivity.getCurrentUserId());
        notificationDataMap.put(CONST.NOTIFICATION_TYPE,        CONST.NOTIFICATION_REQUEST_TYPE);

        friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(notificationId);

        requestMap.put(friendRequest.toString(),  notificationDataMap);
        friendRequest.setLength(0);

        internalActivity.getRootFBDatabaseRef().updateChildren( requestMap,
                                                                sendFriendRequestCompletionListener);
    }

    private void cancelSentFriendRequest() {

        final StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
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
        friendRequest.append(internalActivity.getCurrentUserId());

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(selectedUserId)
                                    .child(internalActivity.getCurrentUserId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

                //Log.e("LOG", "UserProfileFragment: cancelSentFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

//                    Log.e("LOG", "UserProfileFragment: cancelSentFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(selectedUserId);
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    internalActivity.getRootFBDatabaseRef().updateChildren( requestMap,
                                                                            declineFriendRequestCompletionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void sendUnFriendRequest(){

        StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
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
        friendRequest.append(internalActivity.getCurrentUserId());

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        internalActivity.getRootFBDatabaseRef().updateChildren( requestMap,
                                                                unFriendRequestCompletionListener);
    }

    private void acceptFriendRequest() {

        final StringBuilder friendRequest = new StringBuilder("");
        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(CONST.FRIENDSHIP_START_DATE);

        final Map requestMap = new HashMap();
        requestMap.put( friendRequest.toString(),
                        internalActivity.getCurrentDate());
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIENDS_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(CONST.FRIENDSHIP_START_DATE);

        requestMap.put( friendRequest.toString(),
                        internalActivity.getCurrentDate());
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(selectedUserId);

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // rootFBDatabaseRef.updateChildren(requestMap, acceptRequestCompletionListener);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(internalActivity.getCurrentUserId())
                                    .child(selectedUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

                //Log.e("LOG", "UserProfileFragment: acceptFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

                    //Log.e("LOG", "UserProfileFragment: acceptFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(internalActivity.getCurrentUserId());
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    internalActivity.getRootFBDatabaseRef().updateChildren( requestMap,
                                                                            acceptRequestCompletionListener);
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
        friendRequest.append(internalActivity.getCurrentUserId());
        friendRequest.append("/");
        friendRequest.append(selectedUserId);

        final Map requestMap = new HashMap();
        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        friendRequest.append(CONST.FIREBASE_FRIEND_REQUEST_CHILD);
        friendRequest.append("/");
        friendRequest.append(selectedUserId);
        friendRequest.append("/");
        friendRequest.append(internalActivity.getCurrentUserId());

        requestMap.put(friendRequest.toString(),  null);
        friendRequest.setLength(0);

        // --------------------------------------------------------------------- //

        friendRequestFBDatabaseRef.child(internalActivity.getCurrentUserId())
                                    .child(selectedUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String notificationId = dataSnapshot.child(CONST.NOTIFICATION_ID).getValue().toString();

//                Log.e("LOG", "UserProfileFragment: declineFriendRequest(): notificationId= " +notificationId);

                if(!TextUtils.isEmpty(notificationId)) {

//                    Log.e("LOG", "UserProfileFragment: declineFriendRequest(): remove notification!");

                    friendRequest.append(CONST.FIREBASE_NOTIFICATIONS_CHILD);
                    friendRequest.append("/");
                    friendRequest.append(internalActivity.getCurrentUserId());
                    friendRequest.append("/");
                    friendRequest.append(notificationId);

                    requestMap.put(friendRequest.toString(),  null);
                    friendRequest.setLength(0);

                    internalActivity.getRootFBDatabaseRef().updateChildren( requestMap,
                                                                            declineFriendRequestCompletionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    DatabaseReference.CompletionListener sendFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.SENT_REQUEST_STATE;

                showSendRequestButton(R.string.text_cancel_friend_request);
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    error,
                                                    Snackbar.LENGTH_LONG);
                }
                else {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    R.string.error_send_friend_request,
                                                    Snackbar.LENGTH_LONG);
                }

                showSendRequestButton(R.string.text_send_friend_request);
            }
        }
    };

    DatabaseReference.CompletionListener unFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;

                showSendRequestButton(R.string.text_send_friend_request);

                hideDeclineButton();

                friendsFBDatabaseRef.child(selectedUserId)
                        .addListenerForSingleValueEvent(selectedUserFriendsListValueListener);
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    error,
                                                    Snackbar.LENGTH_LONG);
                }
                else {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    R.string.error_unfriend_request,
                                                    Snackbar.LENGTH_LONG);
                }

                showSendRequestButton(R.string.text_unfriend_request);
            }
        }
    };

    DatabaseReference.CompletionListener acceptRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.IS_A_FRIEND_STATE;

                showSendRequestButton(R.string.text_unfriend_request);

                hideDeclineButton();

                friendsFBDatabaseRef.child(selectedUserId)
                        .addListenerForSingleValueEvent(selectedUserFriendsListValueListener);
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    error,
                                                    Snackbar.LENGTH_LONG);
                }
                else {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    R.string.error_accept_friend_request,
                                                    Snackbar.LENGTH_LONG);
                }

                showSendRequestButton(R.string.text_accept_friend_request);
            }
        }
    };

    DatabaseReference.CompletionListener declineFriendRequestCompletionListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError        databaseError,
                               DatabaseReference    databaseReference) {

            if(databaseError == null) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;

                showSendRequestButton(R.string.text_send_friend_request);

                hideDeclineButton();
            }
            else {

                String error  = databaseError.getMessage();

                if(!TextUtils.isEmpty(error)) {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    error,
                                                    Snackbar.LENGTH_LONG);
                }
                else {

                    internalActivity.showSnackBar(  userProfileContainer,
                                                    R.string.error_decline_friend_request,
                                                    Snackbar.LENGTH_LONG);
                }

                showSendRequestButton(R.string.text_cancel_friend_request);
            }
        }
    };

    // ----------------------------------- OTHER ----------------------------------------------//

    private void showDeclineButton() {

        // enable and show decline button
        declineRequestButton.setVisibility(View.VISIBLE);
        declineRequestButton.setEnabled(true);
    }

    private void hideDeclineButton() {

        // disable and hide decline button
        declineRequestButton.setVisibility(View.INVISIBLE);
        declineRequestButton.setEnabled(false);
    }

    private void showSendRequestButton(int titleResId) {

        // enable and show send request button
        sendRequestButton.setText(titleResId);
        sendRequestButton.setVisibility(View.VISIBLE);
        sendRequestButton.setEnabled(true);
    }

    private void disableSendRequestButton() {

        // disable send request button
        sendRequestButton.setEnabled(false);
    }

    private Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

            Picasso.with(getActivity())
                    .load(selectedUserImageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(avatarImageView);
        }
    };
}
