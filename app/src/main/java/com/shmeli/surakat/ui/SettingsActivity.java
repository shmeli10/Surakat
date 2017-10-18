package com.shmeli.surakat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView     settingPageAvatar;
    private TextView            settingPageUserName;
    private TextView            settingPageUserStatus;

    private DatabaseReference   userFBDatabaseRef;

    private FirebaseUser        fbCurrentUser;

    private String              currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingPageAvatar       = UiUtils.findView(this, R.id.settingPageAvatar);
        settingPageUserName     = UiUtils.findView(this, R.id.settingPageUserName);
        settingPageUserStatus   = UiUtils.findView(this, R.id.settingPageUserStatus);

        fbCurrentUser           = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId           = fbCurrentUser.getUid();

        userFBDatabaseRef   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(currentUserId);
        userFBDatabaseRef.addValueEventListener(valueEventListener);
    }


    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //Log.e("LOG", "SettingsActivity: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

            String userName     = dataSnapshot.child("userName").getValue().toString();
            String userStatus   = dataSnapshot.child("userStatus").getValue().toString();
            String userImage    = dataSnapshot.child("userImage").getValue().toString();
            String thumbImage   = dataSnapshot.child("thumbImage").getValue().toString();

            settingPageUserName.setText(userName);
            settingPageUserStatus.setText(userStatus);

/*            if(dataSnapshot.hasChild(currentUserId)) {


            }
            else {

                Intent setAccountIntent = new Intent(   LoginActivity.this,
                        SetAccountActivity.class);
                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(setAccountIntent);

                finish();
            }*/
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
}
