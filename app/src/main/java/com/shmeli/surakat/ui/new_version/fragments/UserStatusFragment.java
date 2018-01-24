package com.shmeli.surakat.ui.new_version.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 01/24/18.
 */
public class UserStatusFragment extends ParentFragment {

    private static UserStatusFragment   instance;

    private View                        view;

    private TextView                    nameTextView;
    private TextView                    statusTextView;

    private DatabaseReference           currentUserFBDatabaseRef;

    private InternalActivity            internalActivity;

    public UserStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserStatusFragment.
     */
    public static UserStatusFragment newInstance() {

        if(instance == null) {
            instance = new UserStatusFragment();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "UserStatusFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_user_status,
                                container,
                                false);

        init();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        //Log.e("LOG", "UserStatusFragment: onCreateOptionsMenu()");

        // hide and disable menu_settings in app bar
        menu.getItem(0).setVisible(false);
        menu.getItem(0).setEnabled(false);

        super.onCreateOptionsMenu(  menu,
                                    inflater);
    }

    // ----------------------------------- INIT ----------------------------------------- //

    private void init() {
        Log.e("LOG", "UserStatusFragment: init()");

        internalActivity    = (InternalActivity) getActivity();

        nameTextView        = UiUtils.findView( view,
                                                R.id.userStatusFragmentNameTextView);

        statusTextView      = UiUtils.findView( view,
                                                R.id.userStatusFragmentStatusEditText);
        statusTextView.requestFocus();

        currentUserFBDatabaseRef = internalActivity.getCurrentUserFBDatabaseRef();

        if(currentUserFBDatabaseRef != null) {

            currentUserFBDatabaseRef.keepSynced(true);
            currentUserFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);
        }
        else {
            Log.e("LOG", "UserStatusFragment: init(): currentUserFBDatabaseRef is null");
        }

        // without this line, onCreateOptionsMenu() will not be invoked
        setHasOptionsMenu(true);
    }

    // ------------------------------ VALUE EVENT LISTENERS ----------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                //Log.e("LOG", "UserStatusFragment: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

                String currentUserName     = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                String currentUserStatus   = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                Log.e("LOG", "UserStatusFragment: currentUserName: " +currentUserName);
                if( (!TextUtils.isEmpty(currentUserName)) &&
                    (!currentUserName.equals(CONST.DEFAULT_VALUE))) {

                    nameTextView.setText(currentUserName);
                }

                Log.e("LOG", "UserStatusFragment: currentUserStatus: " +currentUserStatus);
                if( (!TextUtils.isEmpty(currentUserStatus)) &&
                    (!currentUserStatus.equals(CONST.DEFAULT_VALUE))) {

                    statusTextView.setText(currentUserStatus);
                }
            }
            else {
                Log.e("LOG", "UserStatusFragment: selectedUserProfileValueListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

}
