package com.shmeli.surakat.ui.new_version.fragments;


import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.holders.UserViewHolder;
import com.shmeli.surakat.interfaces.TransferSelectedUser;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/16/17.
 */

public class AllUsersFragment extends Fragment {

    private static AllUsersFragment  instance;

    private View                    view;
    private RecyclerView            allUsersRecyclerVIew;

    private InternalActivity        internalActivity;
    private TransferSelectedUser    transferSelectedUserListener;

    public AllUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AllUsersFragment.
     */
    public static AllUsersFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new AllUsersFragment();
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        view                = inflater.inflate( R.layout.fragment_all_users,
                                                container,
                                                false);

        internalActivity    = (InternalActivity) getActivity();

        if(internalActivity != null) {
            transferSelectedUserListener = (TransferSelectedUser) internalActivity;
        }

        allUsersRecyclerVIew = UiUtils.findView(view,
                                                R.id.allUsersRecyclerVIew);
        allUsersRecyclerVIew.setHasFixedSize(true);
        allUsersRecyclerVIew.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        populateAllUsersList();
    }

    private void populateAllUsersList() {
        Log.e("LOG", "AllUsersFragment: populateAllUsersList()");

        FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                                                                                                                    R.layout.user_row,
                                                                                                                    UserViewHolder.class,
                                                                                                                    internalActivity.getUsersFBDatabaseRef()) {
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

                        //Log.e("LOG", "AllUsersFragment: userClickListener: selectedUserId= " +selectedUserId);

                        //Log.e("LOG", "AllUsersFragment: userClickListener: currentFragment code= " +internalActivity.getCurrentFragmentCode());

                        if( (internalActivity.getCurrentFragmentCode() > 0) &&
                            (internalActivity.getCurrentFragmentCode() == CONST.TABS_FRAGMENT_CODE)) {

                            //Log.e("LOG", "AllUsersFragment: populateFriendsList(): can react on click");

                            if(transferSelectedUserListener != null) {

                                transferSelectedUserListener.onTransferSelectedUserSuccess( CONST.USER_PROFILE_FRAGMENT_CODE,
                                        selectedUserId,
                                        model);
                            }
                            else {
                                Log.e("LOG", "AllUsersFragment: userClickListener: transferSelectedUserListener is null");
                            }
                        }
//                        else {
//
//                            Log.e("LOG", "AllUsersFragment: userClickListener: can not react on click");
//                        }
                    }
                });
            }
        };

        allUsersRecyclerVIew.setAdapter(fbAdapter);
    }
}
