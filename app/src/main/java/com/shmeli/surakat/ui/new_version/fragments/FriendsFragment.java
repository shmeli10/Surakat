package com.shmeli.surakat.ui.new_version.fragments;


import android.app.AlertDialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.holders.UserViewHolder;
import com.shmeli.surakat.interfaces.TransferSelectedUser;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Serghei Ostrovschi on 11/22/17.
 */

public class FriendsFragment extends Fragment {

    private static FriendsFragment  instance;

    private View                    view;
    private RecyclerView            friendsList;

    private InternalActivity        internalActivity;

    private TransferSelectedUser    transferSelectedUserListener;

//    private Query                   friendsQuery;

    private List<String>            friendsIdsList = new ArrayList<>();

    private FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter;

    private int                     nonFriendsSum;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new FriendsFragment();
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        view                = inflater.inflate( R.layout.fragment_friends,
                                                container,
                                                false);

        internalActivity    = (InternalActivity) getActivity();

        if(internalActivity != null) {
            transferSelectedUserListener = (TransferSelectedUser) internalActivity;
        }

        friendsList = UiUtils.findView( view,
                                        R.id.friendsRecyclerVIew);
        friendsList.setHasFixedSize(true);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        populateFriendsIdsList();

        //friendsQuery = internalActivity.getUsersFBDatabaseRef().orderByChild("height").equalTo(25);

        /*if(!friendsIdsList.isEmpty()) {
            Log.e("LOG", "FriendsFragment: onStart(): start populateFriendsList");

            populateFriendsList();
        }
        else {
            Log.e("LOG", "FriendsFragment: onStart(): friendsIdsList.isEmpty");
        }*/

        //internalActivity.getCurrentUserFBDatabaseRef().addListenerForSingleValueEvent(currentUserDataChangeListener);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener friendsIdsValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //Log.e("LOG", "FriendsFragment: friendsIdsValueListener: dataSnapshot: " +dataSnapshot.toString());

            HashMap<String, Object> friendsMap = (HashMap<String, Object>) dataSnapshot.getValue();

            //Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap size= " +friendsMap.size());

            if( friendsMap != null &&
                !friendsMap.isEmpty()) {

                for (Map.Entry<String, Object> friend : friendsMap.entrySet()) {

                    if (!friendsIdsList.contains(friend.getKey())) {
                        friendsIdsList.add(friend.getKey());

                        Log.e("LOG", "FriendsFragment: friendsIdsValueListener: added friend with key: " + friend.getKey());
                    }

//                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friend: key: " +friend.getKey());
//                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friend: value: " +friend.getValue());
                }

                if (!friendsIdsList.isEmpty()) {
                    Log.e("LOG", "FriendsFragment: friendsIdsValueListener: start populateFriendsList");

                    populateFriendsList();

                    //hideNonFriends();
                } else {
                    Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsIdsList.isEmpty");
                }
            }
            else {
                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap.isEmpty");
            }

            //String friendId = dataSnapshot.getValue().toString();
            //Iterator<DataSnapshot> friends = dataSnapshot.getChildren().iterator();

            //Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendId: " +friendId);

/*            while(friends.hasNext()) {

                DataSnapshot friend = friends.next();

                HashMap<String, String> dataMap = (HashMap<String, String>) friend.getValue();

                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: dataMap: " +dataMap.toString());
            }*/


            //dataSnapshot.getKey()

            //JSONObject friendJSONObj = (JSONObject) dataSnapshot.getValue();
//            HashMap<String, String> friendsMap = (HashMap<String, String>) dataSnapshot.getValue();

            //Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendJSONObj: " +friendJSONObj.toString());

//            if(friendsMap.isEmpty())
//                return;
//
//            while(friendsMap.entrySet().iterator().hasNext()) {
//
//                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap key: "   +);
//
//                //Map.Entry<String, String>
//                friendsMap.entrySet().iterator();
//            }
//
//            Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap: "       +friendsMap.toString());
//            Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap key: "   +);
//            Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap value: " +friendsMap.toString());

//            Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap elements: ");
//
//            for(Map.Entry<String, String> friend: friendsMap.entrySet()) {
//                Log.e("LOG", "FriendsFragment: friendsIdsValueListener: friendsMap key: " + friend.getKey() + "\tvalue: " + friend.getValue());
//            }


//            String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
//            String userStatus           = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();
//            userImageUrl                = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();
            //String userImageUrl         = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();
//            String userThumbImageUrl    = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();

//            String userName     = dataSnapshot.child("userName").getValue().toString();
//            String userStatus   = dataSnapshot.child("userStatus").getValue().toString();
//            String userImage    = dataSnapshot.child("userImage").getValue().toString();
//            String thumbImage   = dataSnapshot.child("thumbImage").getValue().toString();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };


    // ----------------------------------- OTHER ----------------------------------------- //

    private void populateFriendsIdsList() {
        Log.e("LOG", "FriendsFragment: populateFriendsIdsList()");

        friendsIdsList.clear();

        internalActivity.getCurrentUserFriendsFBDatabaseRef().addListenerForSingleValueEvent(friendsIdsValueListener);
    }

/*    private void populateFriendsIdsList() {
        Log.e("LOG", "FriendsFragment: populateFriendsIdsList()");

        //internalActivity.getCurrentUserFriendsFBDatabaseRef()
    }*/

    private void populateFriendsList() {
        Log.e("LOG", "FriendsFragment: populateFriendsList()");

        fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                                                                                                                    R.layout.user_row,
                                                                                                                    UserViewHolder.class,
                                                                                                                    internalActivity.getUsersFBDatabaseRef()) {

            @Override
            protected void populateViewHolder(UserViewHolder    viewHolder,
                                              final User        model,
                                              int               position) {

                String userId = getRef(position).getKey();

                Log.e("LOG", "FriendsFragment: populateFriendsList(): userId: " +userId);

                if(!TextUtils.isEmpty(userId)) {

                    model.setUserId(userId);

                    if (friendsIdsList.contains(userId)) {

                        viewHolder.setName(model.getUserName());
                        viewHolder.setStatus(model.getUserStatus());
                        viewHolder.setAvatar(model.getUserThumbImageUrl());
                        viewHolder.setOnlineStatus(model.getUserIsOnline());

                        //viewHolder.itemView.setVisibility(View.VISIBLE);

                        Log.e("LOG", "FriendsFragment: populateFriendsList(): this is a friend");
                    }
                    else {

                        Log.e("LOG", "FriendsFragment: populateFriendsList(): this is not a friend");

                        //nonFriendsSum++;

                        //return;
                        //viewHolder.itemView.setVisibility(View.GONE);

                        //friendsList.removeViewAt(position);

//                        getRef(position).removeValue();

                        //notifyItemRemoved(position);
                    }
                }
                else {
                    Log.e("LOG", "FriendsFragment: populateFriendsList(): userId is incorrect");
                }

                /*final String selectedUserId = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Log.e("LOG", "FriendsFragment: userClickListener: selectedUserId= " +selectedUserId);

                        //Log.e("LOG", "FriendsFragment: userClickListener: currentFragment code= " +internalActivity.getCurrentFragmentCode());

                        if( (internalActivity.getCurrentFragmentCode() > 0) &&
                                (internalActivity.getCurrentFragmentCode() == CONST.TABS_FRAGMENT_CODE)) {

                            //Log.e("LOG", "FriendsFragment: populateFriendsList(): can react on click");

                            if(transferSelectedUserListener != null) {

                                transferSelectedUserListener.onTransferSelectedUserSuccess( CONST.USER_PROFILE_FRAGMENT_CODE,
                                        selectedUserId,
                                        model);
                            }
                            else {
                                Log.e("LOG", "FriendsFragment: userClickListener: transferSelectedUserListener is null");
                            }
                        }
//                        else {
//
//                            Log.e("LOG", "FriendsFragment: userClickListener: can not react on click");
//                        }
                    }
                });*/
            }

//            @Override
//            public int getItemCount() {
//
//                int itemsSum = (super.getItemCount() - (nonFriendsSum -1));
//
//                Log.e("LOG", "FriendsFragment: getItemCount(): elements sum= " +itemsSum);
//
//                return itemsSum;
//            }
        };

        friendsList.setAdapter(fbAdapter);
    }

//    private void hideNonFriends() {
//        Log.e("LOG", "FriendsFragment: hideNonFriends()");
//
//        if(fbAdapter != null) {
//
//            Log.e("LOG", "FriendsFragment: hideNonFriends(): fbAdapter.getItemCount: " +fbAdapter.getItemCount());
//
//            for (int i = 0; i <fbAdapter.getItemCount(); i++) {
//
//                Log.e("LOG", "FriendsFragment: hideNonFriends(): (" +i+ ") userId= " + fbAdapter.getItem(i).getUserId());
//
//                if(!friendsIdsList.contains(fbAdapter.getItem(i).getUserId())) {
//
//                    Log.e("LOG", "FriendsFragment: hideNonFriends(): (" +i+ ") user is removed");
//
//                    friendsList.removeViewAt(i);
//                    fbAdapter.notifyItemRemoved(i);
//                }
//                else {
//                    Log.e("LOG", "FriendsFragment: hideNonFriends(): (" +i+ ") user is a friend");
//                }
//            }
//        }
//        else {
//            Log.e("LOG", "FriendsFragment: hideNonFriends(): fbAdapter is null");
//        }
//    }

    /*private void populateFriendsList() {
        Log.e("LOG", "FriendsFragment: populateFriendsList()");

        FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                                                                                                                    R.layout.user_row,
                                                                                                                    UserViewHolder.class,
                                                                                                                    internalActivity.getCurrentUserFriendsFBDatabaseRef()) {
            @Override
            protected void populateViewHolder(final UserViewHolder  viewHolder,
                                              final User            model,
                                              int                   position) {

                Log.e("LOG", "FriendsFragment: populateFriendsList(): populateViewHolder()");

                viewHolder.setName(model.getUserName());
                viewHolder.setStatus(model.getUserStatus());
                viewHolder.setAvatar(model.getUserThumbImageUrl());
                viewHolder.setOnlineStatus(model.getUserIsOnline());

                *//*final String selectedUserId = getRef(position).getKey();

                internalActivity.getUsersFBDatabaseRef().child(selectedUserId)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.e("LOG", "FriendsFragment: populateFriendsList: onDataChange()");

                        final User selectedUser = new User();

                        String selectedUserDeviceToken  = "";
                        String selectedUserImageUrl     = "";
                        String selectedUserName         = "";
                        String selectedUserStatus       = "";
                        String selectedUserThumbImage   = "";

                        boolean selectedUserIsOnline    = false;

                        if(dataSnapshot.hasChild(CONST.USER_DEVICE_TOKEN)) {
                            selectedUserDeviceToken = dataSnapshot.child(CONST.USER_DEVICE_TOKEN).getValue().toString();

                            if (!TextUtils.isEmpty(selectedUserDeviceToken)) {
                                selectedUser.setUserDeviceToken(selectedUserDeviceToken);
                            }
                        }

                        if(dataSnapshot.hasChild(CONST.USER_IMAGE)) {
                            selectedUserImageUrl = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

                            if (!TextUtils.isEmpty(selectedUserImageUrl)) {
                                selectedUser.setUserImageUrl(selectedUserImageUrl);
                            }
                        }

                        if(dataSnapshot.hasChild(CONST.USER_IS_ONLINE)) {
                            String isOnlineValue = dataSnapshot.child(CONST.USER_IS_ONLINE).getValue().toString();

                            if (!TextUtils.isEmpty(isOnlineValue)) {
                                selectedUserIsOnline = Boolean.parseBoolean(isOnlineValue);
                                selectedUser.setUserIsOnline(selectedUserIsOnline);

                                viewHolder.setOnlineStatus(selectedUserIsOnline);

                                Log.e("LOG", "FriendsFragment: populateFriendsList: onDataChange(): change online status to: " +selectedUserIsOnline);
                            }
                        }

                        if(dataSnapshot.hasChild(CONST.USER_NAME)) {
                            selectedUserName = dataSnapshot.child(CONST.USER_NAME).getValue().toString();

                            if(!TextUtils.isEmpty(selectedUserName)) {
                                selectedUser.setUserName(selectedUserName);
                            }
                        }

                        if(dataSnapshot.hasChild(CONST.USER_STATUS)) {
                            selectedUserStatus = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                            if(!TextUtils.isEmpty(selectedUserStatus)) {
                                selectedUser.setUserStatus(selectedUserStatus);
                            }
                        }

                        if(dataSnapshot.hasChild(CONST.USER_THUMB_IMAGE)) {
                            selectedUserThumbImage = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();

                            if(!TextUtils.isEmpty(selectedUserThumbImage)) {
                                selectedUser.setUserThumbImageUrl(selectedUserThumbImage);
                            }
                        }

                        viewHolder.setName(selectedUserName);
                        viewHolder.setStatus(selectedUserStatus);
                        viewHolder.setAvatar(selectedUserThumbImage);

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Log.e("LOG", "FriendsFragment: populateFriendsList(): selectedUserId= " +selectedUserId);

                                //Log.e("LOG", "FriendsFragment: populateFriendsList(): currentFragment code= " +internalActivity.getCurrentFragmentCode());

                                if( (internalActivity.getCurrentFragmentCode() > 0) &&
                                    (internalActivity.getCurrentFragmentCode() == CONST.TABS_FRAGMENT_CODE)) {

                                    //Log.e("LOG", "FriendsFragment: populateFriendsList(): can react on click");

                                    String openProfileText      = getResources().getString(R.string.text_open_profile);
                                    String sendMessageText      = getResources().getString(R.string.text_send_message);
                                    String selectOptionsText    = getResources().getString(R.string.text_select_options);

                                    int alertDialogDividerColorResId        = getResources().getColor(R.color.colorAccent);

                                    CharSequence[] optionsArr               = new CharSequence[] {  openProfileText,
                                            sendMessageText};

                                    AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(  getContext(),
                                            R.style.Theme_Sphinx_Dialog_Alert);

                                    alertDialogBuilder.setTitle(selectOptionsText);

                                    alertDialogBuilder.setItems(optionsArr,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int             which) {

                                                    switch(which) {

                                                        case CONST.OPEN_PROFILE_TYPE:
                                                            if(transferSelectedUserListener != null) {

                                                                transferSelectedUserListener.onTransferSelectedUserSuccess( CONST.USER_PROFILE_FRAGMENT_CODE,
                                                                        selectedUserId,
                                                                        selectedUser);
                                                            }
                                                            else {
                                                                Log.e("LOG", "FriendsFragment: populateFriendsList(): transferSelectedUserListener is null");
                                                            }
                                                            break;
                                                        case CONST.SEND_MESSAGE_TYPE:

                                                            transferSelectedUserListener.onTransferSelectedUserSuccess( CONST.CHAT_FRAGMENT_CODE,
                                                                    selectedUserId,
                                                                    selectedUser);
                                                            break;
                                                    }
                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    // Set title divider color
                                    int titleDividerId = getResources().getIdentifier(  "titleDivider",
                                                                                        "id",
                                                                                        "android");

                                    View titleDivider = alertDialog.findViewById(titleDividerId);

                                    if (titleDivider != null)
                                        titleDivider.setBackgroundColor(alertDialogDividerColorResId);
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*//*
            }
        };

        friendsList.setAdapter(fbAdapter);
    }*/
}
