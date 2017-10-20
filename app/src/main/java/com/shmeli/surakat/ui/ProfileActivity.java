package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.settings.SettingsActivity;
import com.shmeli.surakat.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar             profilePageToolbar;
    private ProgressDialog      progressDialog;

    private RelativeLayout      profileContainer;

    private CircleImageView     profilePageAvatar;

    private TextView            profilePageUserName;
    private TextView            profilePageUserStatus;
    private TextView            profilePageFriendsCount;

    private Button              profilePageSendRequest;

    private DatabaseReference   userFBDatabaseRef;
    private DatabaseReference   friendRequestFBDatabaseRef;

    private FirebaseUser        currentFBUser;

    private String selectedUserId   = "";

    private int friendshipState     = CONST.IS_NOT_A_FRIEND_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();

        if(profileIntent != null) {
            selectedUserId = profileIntent.getStringExtra(CONST.USER_ID);

            //Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId= " +selectedUserId);
        }

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.message_loading_profile_data));
        progressDialog.setMessage(getResources().getString(R.string.message_loading_profile_data_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        userFBDatabaseRef       = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(selectedUserId);
        userFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);

        friendRequestFBDatabaseRef = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_FRIEND_REQUEST_CHILD);

        currentFBUser = FirebaseAuth.getInstance().getCurrentUser();

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

        profileContainer        = UiUtils.findView(this, R.id.profileContainer);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //Log.e("LOG", "SettingsActivity: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

            String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
            String userStatus           = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();
            String userImageUrl         = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

            profilePageUserName.setText(userName);
            profilePageUserStatus.setText(userStatus);

            Picasso.with(ProfileActivity.this)
                        .load(userImageUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(profilePageAvatar);

            // GET LIST OF FRIEND REQUESTS
            friendRequestFBDatabaseRef.child(currentFBUser.getUid()).addListenerForSingleValueEvent(friendRequestsListValueListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener friendRequestsListValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot.hasChild(selectedUserId)) {

                //String requestTypeText = dataSnapshot.child(selectedUserId).child(CONST.REQUEST_TYPE_ID).getValue().toString();

                //Log.e("LOG", "ProfileActivity: friendRequestsListValueListener: requestTypeText= " +requestTypeText);

                //if(!TextUtils.isEmpty(requestTypeText)) {

                    int requestTypeId = Integer.valueOf(dataSnapshot.child(selectedUserId).child(CONST.REQUEST_TYPE_ID).getValue().toString());
                    //int requestTypeId = Integer.valueOf(requestTypeText);

                    switch (requestTypeId) {

                        // ACCEPT FRIEND REQUEST
                        case CONST.RECEIVED_REQUEST_STATE:
                            friendshipState = CONST.RECEIVED_REQUEST_STATE;
                            profilePageSendRequest.setText(R.string.text_accept_friend_request);
                            break;
                        // CANCEL SENT FRIEND REQUEST
                        case CONST.SENT_REQUEST_STATE:
                            friendshipState = CONST.SENT_REQUEST_STATE;
                            profilePageSendRequest.setText(R.string.text_cancel_friend_request);
                            break;
                    }
                //}
            }

            progressDialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    View.OnClickListener sendRequestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            profilePageSendRequest.setEnabled(false);

            switch(friendshipState) {

                // NOT FRIENDS, SEND REQUEST
                case CONST.IS_NOT_A_FRIEND_STATE:

                    Map requestTypeMap = new HashMap();
                    requestTypeMap.put(CONST.REQUEST_TYPE_TEXT, CONST.SENT_REQUEST);
                    requestTypeMap.put(CONST.REQUEST_TYPE_ID,   CONST.SENT_REQUEST_STATE);
                    //requestTypeMap.put(CONST.REQUEST_TYPE_ID,   "" +CONST.SENT_REQUEST_STATE);

                    friendRequestFBDatabaseRef.child(currentFBUser.getUid())
                            .child(selectedUserId)
                            .setValue(requestTypeMap)
                            .addOnCompleteListener(onSendFriendRequestCompleteListener);

//                    friendRequestFBDatabaseRef.child(currentFBUser.getUid())
//                            .child(selectedUserId)
//                            .child(CONST.REQUEST_TYPE_TEXT)
//                            .setValue(CONST.SENT_REQUEST)
//                            .addOnCompleteListener(onSendFriendRequestCompleteListener);
                    break;
                // NOT FRIENDS, CANCEL SENT REQUEST
                case CONST.SENT_REQUEST_STATE:
                    friendRequestFBDatabaseRef.child(currentFBUser.getUid())
                            .child(selectedUserId)
                            .removeValue()
                            .addOnCompleteListener(onCancelSentFriendRequestMyPartCompleteListener);
                    break;
            }
        }
    };

    OnCompleteListener<Void> onSendFriendRequestCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "ProfileActivity: onSendFriendRequestCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                Map requestTypeMap = new HashMap();
                requestTypeMap.put(CONST.REQUEST_TYPE_TEXT, CONST.RECEIVED_REQUEST);
                requestTypeMap.put(CONST.REQUEST_TYPE_ID,   CONST.RECEIVED_REQUEST_STATE);
                //requestTypeMap.put(CONST.REQUEST_TYPE_ID,   "" +CONST.RECEIVED_REQUEST_STATE);

                friendRequestFBDatabaseRef.child(selectedUserId)
                        .child(currentFBUser.getUid())
                        .setValue(requestTypeMap)
                        .addOnCompleteListener(onFriendRequestReceiveCompleteListener);

//                friendRequestFBDatabaseRef.child(selectedUserId)
//                        .child(currentFBUser.getUid())
//                        .child(CONST.REQUEST_TYPE)
//                        .setValue(CONST.RECEIVED_REQUEST)
//                        //.addOnSuccessListener(onSendFriendRequestReceiveSuccessListener);
//                        .addOnCompleteListener(onFriendRequestReceiveCompleteListener);
            }
            else {
                progressDialog.hide();

                Snackbar.make(  profileContainer,
                                R.string.error_send_friend_request,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> onFriendRequestReceiveCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "ProfileActivity: onFriendRequestReceiveCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                friendshipState = CONST.SENT_REQUEST_STATE;
                profilePageSendRequest.setEnabled(true);
                profilePageSendRequest.setText(R.string.text_cancel_friend_request);

                //Log.e("LOG", "ProfileActivity: onFriendRequestReceiveCompleteListener: success");
            }
            else {

                Snackbar.make(  profileContainer,
                        R.string.error_receive_friend_request,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> onCancelSentFriendRequestMyPartCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "ProfileActivity: onCancelSentFriendRequestCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                friendRequestFBDatabaseRef.child(selectedUserId)
                        .child(currentFBUser.getUid())
                        .removeValue()
                        .addOnCompleteListener(onCancelSentFriendRequestSelectedUserPartCompleteListener);
            }
            else {

                Snackbar.make(  profileContainer,
                        R.string.error_cancel_sent_friend_request,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> onCancelSentFriendRequestSelectedUserPartCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "ProfileActivity: onCancelSentFriendRequestSelectedUserPartCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                friendshipState = CONST.IS_NOT_A_FRIEND_STATE;
                profilePageSendRequest.setEnabled(true);
                profilePageSendRequest.setText(R.string.text_send_friend_request);
            }
            else {

                Snackbar.make(  profileContainer,
                        R.string.error_cancel_sent_friend_request,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
