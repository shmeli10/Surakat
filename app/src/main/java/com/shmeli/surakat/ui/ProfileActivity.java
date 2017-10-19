package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar             profilePageToolbar;
    private ProgressDialog      progressDialog;

    private CircleImageView     profilePageAvatar;

    private TextView            profilePageUserName;
    private TextView            profilePageUserStatus;
    private TextView            profilePageFriendsCount;

    private Button              profilePageSendRequest;

    private DatabaseReference   userFBDatabaseRef;

    private String selectedUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();

        if(profileIntent != null) {
            selectedUserId = profileIntent.getStringExtra(CONST.USER_ID);

            //Log.e("LOG", "ProfileActivity: onCreate(): selectedUserId= " +selectedUserId);
        }

        userFBDatabaseRef       = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(selectedUserId);
        userFBDatabaseRef.addValueEventListener(valueEventListener);

        profilePageToolbar = UiUtils.findView(this, R.id.profilePageToolbar);
        setSupportActionBar(profilePageToolbar);
        getSupportActionBar().setTitle(R.string.text_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.message_loading_profile_data));
        progressDialog.setMessage(getResources().getString(R.string.message_loading_profile_data_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        profilePageAvatar       = UiUtils.findView(this, R.id.profilePageAvatar);
        profilePageUserName     = UiUtils.findView(this, R.id.profilePageUserName);
        profilePageUserStatus   = UiUtils.findView(this, R.id.profilePageUserStatus);
        profilePageFriendsCount = UiUtils.findView(this, R.id.profilePageFriendsCount);
        profilePageSendRequest  = UiUtils.findView(this, R.id.profilePageSendRequest);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //Log.e("LOG", "SettingsActivity: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

            String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
            String userStatus           = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();
            String userImageUrl         = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

            profilePageUserName.setText(userName);
            profilePageUserStatus.setText(userStatus);

            if(!userImageUrl.equals(CONST.DEFAULT_VALUE))
                Picasso.with(ProfileActivity.this)
                        .load(userImageUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(profilePageAvatar);

            progressDialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
}
