package com.shmeli.surakat.ui.new_version.fragments;


import android.app.AlertDialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.Query;

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

    private Query                   friendsQuery;

    private TransferSelectedUser    transferSelectedUserListener;

    private FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter;

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

        //Log.e("LOG", "FriendsFragment: onCreateView(): currentUser Id: " +internalActivity.getCurrentUserId());

        friendsQuery = internalActivity.getUsersFBDatabaseRef()
                                        .orderByChild(CONST.FIREBASE_FRIENDS_CHILD + "/" + internalActivity.getCurrentUserId())
                                        .equalTo(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        populateFriendsList();
    }

    // ----------------------------------- OTHER ----------------------------------------- //

    private void populateFriendsList() {
        //Log.e("LOG", "FriendsFragment: populateFriendsList()");

        fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(  User.class,
                                                                        R.layout.user_row,
                                                                        UserViewHolder.class,
                                                                        friendsQuery) {

            @Override
            protected void populateViewHolder(UserViewHolder    viewHolder,
                                              final User        model,
                                              int               position) {

                viewHolder.setName(model.getUserName());
                viewHolder.setStatus(model.getUserStatus());
                viewHolder.setAvatar(model.getUserThumbImageUrl());
                viewHolder.setOnlineStatus(model.getUserIsOnline());

                final String selectedUserId = getRef(position).getKey();

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
                                                                                                                    model);
                                                    }
                                                    else {
                                                        Log.e("LOG", "FriendsFragment: populateFriendsList(): transferSelectedUserListener is null");
                                                    }
                                                    break;
                                                case CONST.SEND_MESSAGE_TYPE:

                                                    transferSelectedUserListener.onTransferSelectedUserSuccess( CONST.CHAT_FRAGMENT_CODE,
                                                                                                                selectedUserId,
                                                                                                                model);
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
        };

        friendsList.setAdapter(fbAdapter);
    }
}
