package com.shmeli.surakat.ui.new_version.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private LinearLayout                userStatusContainer;

    private TextView                    nameTextView;
    private EditText                    statusEditText;
    private TextView                    leftCharsBodyText;

    private Button                      setButton;

    private DatabaseReference           currentUserFBDatabaseRef;

    private String                      currentUserStatus    = "";

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

        userStatusContainer = UiUtils.findView( view,
                                                R.id.userStatusContainer);

        nameTextView        = UiUtils.findView( view,
                                                R.id.userStatusFragmentNameTextView);

        statusEditText      = UiUtils.findView( view,
                                                R.id.userStatusFragmentStatusEditText);
        statusEditText.addTextChangedListener(onTextChangedListener);
        statusEditText.requestFocus();

        leftCharsBodyText   = UiUtils.findView( view,
                                                R.id.userStatusFragmentLeftCharsBody);
        leftCharsBodyText.setText(String.valueOf(CONST.USER_STATUS_MAX_LENGTH));

        setButton           = UiUtils.findView( view,
                                                R.id.userStatusFragmentSetButton);
        setButton.setOnClickListener(setClickListener);

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
                currentUserStatus   = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                if( (!TextUtils.isEmpty(currentUserName)) &&
                    (!currentUserName.equals(CONST.DEFAULT_VALUE))) {

                    nameTextView.setText(currentUserName);
                }

                if( (!TextUtils.isEmpty(currentUserStatus)) &&
                    (!currentUserStatus.equals(CONST.DEFAULT_VALUE))) {

                    statusEditText.setText(currentUserStatus);
                }
            }
            else {
                Log.e("LOG", "UserStatusFragment: selectedUserProfileValueListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener<Void> onChangeStatusCompleteListener = new OnCompleteListener<Void>() {

        @Override
        public void onComplete(Task<Void> task) {

            if(task.isSuccessful()) {

                internalActivity.dismissProgressDialog();

                Snackbar.make(  userStatusContainer,
                        R.string.success_saving_changes,
                        Snackbar.LENGTH_LONG).show();
            }
            else {

                internalActivity.hideProgressDialog();

                Snackbar.make(  userStatusContainer,
                                R.string.error_save_changes,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    // ------------------------------ BUTTON CLICK LISTENER ------------------------------------- //

    View.OnClickListener setClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String newUserStatus = statusEditText.getText().toString();

            if(TextUtils.isEmpty(newUserStatus)) {
                newUserStatus = CONST.DEFAULT_VALUE;
            }

            if(!currentUserStatus.equals(newUserStatus)) {

                internalActivity.showProgressDialog(getResources().getString(R.string.text_saving_changes),
                                                    getResources().getString(R.string.message_saving_changes));

                currentUserFBDatabaseRef.child("userStatus").setValue(newUserStatus).addOnCompleteListener(onChangeStatusCompleteListener);
            }
            else {

                Log.e("LOG", "UserStatusFragment: setClickListener: no changes in status.");
            }
        }
    };

    // ---------------------------- TEXT CHANGED LISTENER -------------------------------------- //

    TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence  s,
                                      int           start,
                                      int           count,
                                      int           after) { }

        @Override
        public void onTextChanged(CharSequence  s,
                                  int           start,
                                  int           before,
                                  int           count) {

            // set available amount of characters
            leftCharsBodyText.setText(String.valueOf(CONST.USER_STATUS_MAX_LENGTH - statusEditText.length()));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };
}
