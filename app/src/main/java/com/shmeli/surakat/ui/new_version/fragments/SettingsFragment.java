package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Serghei Ostrovschi on 12/19/17.
 */
public class SettingsFragment extends ParentFragment {

    private static SettingsFragment  instance;

    private View                view;

    private LinearLayout        settingsContainer;

    private CircleImageView     avatarImageView;

    private TextView            nameTextView;
    private TextView            statusTextView;

    private Button              changeImageButton;
    private Button              changeStatusButton;

    private DatabaseReference   currentUserFBDatabaseRef;

    private String              currentUserImageUrl = "";

    private InternalActivity    internalActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {

        if(instance == null) {
            instance = new SettingsFragment();
        }

        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "SettingsFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_settings,
                                container,
                                false);

        init();

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
        Log.e("LOG", "SettingsFragment: init()");

        internalActivity = (InternalActivity) getActivity();

        settingsContainer   = UiUtils.findView( view,
                                                R.id.settingsContainer);

        avatarImageView     = UiUtils.findView( view,
                                                R.id.settingsAvatar);

        nameTextView        = UiUtils.findView( view,
                                                R.id.settingsName);

        statusTextView      = UiUtils.findView( view,
                                                R.id.settingsStatus);

        changeImageButton   = UiUtils.findView( view,
                                                R.id.settingsChangeImage);
        changeImageButton.setOnClickListener(changeImageClickListener);

        changeStatusButton  = UiUtils.findView( view,
                                                R.id.settingsChangeStatus);
        changeStatusButton.setOnClickListener(changeStatusClickListener);

        currentUserFBDatabaseRef = internalActivity.getCurrentUserFBDatabaseRef();

        if(currentUserFBDatabaseRef != null) {

            currentUserFBDatabaseRef.keepSynced(true);
            currentUserFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);
        }
        else {
            Log.e("LOG", "SettingsFragment: init(): currentUserFBDatabaseRef is null");
        }
    }

    // ------------------------------ VALUE EVENT LISTENERS ----------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                //Log.e("LOG", "SettingsFragment: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

                String currentUserName     = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                String currentUserStatus   = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                currentUserImageUrl        = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

                Log.e("LOG", "UserProfileFragment: currentUserName: " +currentUserName);
                if( (!TextUtils.isEmpty(currentUserName)) &&
                    (!currentUserName.equals(CONST.DEFAULT_VALUE))) {

                    nameTextView.setText(currentUserName);
                }

                Log.e("LOG", "UserProfileFragment: currentUserStatus: " +currentUserStatus);
                if( (!TextUtils.isEmpty(currentUserStatus)) &&
                    (!currentUserStatus.equals(CONST.DEFAULT_VALUE))) {

                    statusTextView.setText(currentUserStatus);
                }

                if (!currentUserImageUrl.equals(CONST.DEFAULT_VALUE)) {
                    Picasso.with(getActivity())
                            .load(currentUserImageUrl)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(  avatarImageView,
                                    loadImageCallback);
                }
            }
            else {
                Log.e("LOG", "UserProfileFragment: selectedUserProfileValueListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };



    // ------------------------------ BUTTON CLICK LISTENER ------------------------------------- //

    View.OnClickListener changeImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "SettingsFragment: changeImageButton Click ");
        }
    };

    View.OnClickListener changeStatusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "SettingsFragment: changeStatusButton Click ");
        }
    };

    // ----------------------------------- OTHER ----------------------------------------------//

    private Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

            Picasso.with(getActivity())
                    .load(currentUserImageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(avatarImageView);
        }
    };
}
