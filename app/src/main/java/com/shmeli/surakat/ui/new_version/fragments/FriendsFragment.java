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
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.holders.UserViewHolder;
import com.shmeli.surakat.interfaces.TransferSelectedUser;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/22/17.
 */

public class FriendsFragment extends Fragment {

    private static FriendsFragment  instance;

    private View                    view;
    private RecyclerView            friendsList;

    private InternalActivity        internalActivity;

    private TransferSelectedUser    transferSelectedUserListener;

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

        populateFriendsList();
    }

    private void populateFriendsList() {

        FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                                                                                                                    R.layout.user_row,
                                                                                                                    UserViewHolder.class,
                                                                                                                    internalActivity.getCurrentUserFriendsFBDatabaseRef()) {
            @Override
            protected void populateViewHolder(final UserViewHolder  viewHolder,
                                              final User            model,
                                              int                   position) {

                final String selectedUserId = getRef(position).getKey();

                internalActivity.getUsersFBDatabaseRef().child(selectedUserId)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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
                });
            }
        };

        friendsList.setAdapter(fbAdapter);
    }
}
