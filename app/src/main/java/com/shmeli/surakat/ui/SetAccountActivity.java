package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import java.util.HashMap;

public class SetAccountActivity extends AppCompatActivity {

    private RelativeLayout      setAccountContainer;
    private EditText            setAccountNameEditText;
//    private EditText setAccountEmailEditText;
//    private EditText setAccountPasswordEditText;

    private Button              setAccountButton;
    private ProgressDialog      progressDialog;

    private FirebaseAuth        fbAuth;
    private FirebaseUser        currentFBUser;

    private DatabaseReference   currentUserFBDatabaseRef;
    private DatabaseReference   rootFBDatabaseRef;
    private DatabaseReference   usersFBDatabaseRef;

    private String              currentUserDeviceToken  = "";
    //private String              currentUserEmail        = "";
    private String              currentUserId           = "";
    private String              currentUserName         = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account);

        fbAuth              = FirebaseAuth.getInstance();
        currentFBUser       = fbAuth.getCurrentUser();

        rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
        usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

        progressDialog      = new ProgressDialog(this);

        setAccountContainer         = UiUtils.findView(this, R.id.setAccountContainer);

        setAccountNameEditText      = UiUtils.findView(this, R.id.setAccountNameEditText);
//        setAccountEmailEditText     = UiUtils.findView(this, R.id.setAccountEmailEditText);
//        setAccountPasswordEditText  = UiUtils.findView(this, R.id.setAccountPasswordEditText);

        setAccountButton = UiUtils.findView(this, R.id.setAccountButton);
        setAccountButton.setOnClickListener(onSetAccountClick);
    }

    private void startSetAccount() {

        if(currentFBUser != null) {

            currentUserId = currentFBUser.getUid();

            if(!TextUtils.isEmpty(currentUserId)) {

                currentUserFBDatabaseRef    = usersFBDatabaseRef.child(currentUserId);

                currentUserName             = setAccountNameEditText.getText().toString().trim();
                //currentUserEmail            = currentFBUser.getEmail();
                currentUserDeviceToken      = FirebaseInstanceId.getInstance().getToken();

                if(!TextUtils.isEmpty(currentUserName)) {

                    progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
                    progressDialog.show();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put(CONST.USER_IMAGE, CONST.DEFAULT_VALUE);
                    userMap.put(CONST.USER_NAME, currentUserName);
                    userMap.put(CONST.USER_DEVICE_TOKEN, currentUserDeviceToken);
                    userMap.put(CONST.USER_STATUS, CONST.USER_ONLINE_STATUS);
                    userMap.put(CONST.USER_THUMB_IMAGE, CONST.DEFAULT_VALUE);

                    currentUserFBDatabaseRef.setValue(userMap).addOnCompleteListener(createUserInDBCompleteListener);
                }
                else {

                    Log.e("LOG", "SetAccountActivity: startSetAccount(): current user name is empty or null");
                }
            }
            else {

                Log.e("LOG", "SetAccountActivity: startSetAccount(): get current user id error");
            }
        }

//        String userName     = setAccountNameEditText.getText().toString().trim();
//        String email    = setAccountNameEditText.getText().toString().trim();
//        String password = setAccountNameEditText.getText().toString().trim();

//        String userId       = fbAuth.getCurrentUser().getUid();
//        String deviceToken  = FirebaseInstanceId.getInstance().getToken();

//        databaseReference   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(userId);

//        HashMap<String, String> userMap = new HashMap<>();
//        userMap.put(CONST.USER_IMAGE,           CONST.DEFAULT_VALUE);
//        userMap.put(CONST.USER_NAME,            userName);
//        userMap.put(CONST.USER_DEVICE_TOKEN,    deviceToken);
//        userMap.put(CONST.USER_STATUS,          CONST.USER_ONLINE_STATUS);
//        userMap.put(CONST.USER_THUMB_IMAGE,     CONST.DEFAULT_VALUE);
//
//        databaseReference.setValue(userMap).addOnCompleteListener(onCreateUserInDBCompleteListener);
//
//
//        Log.e("LOG", "SetAccountActivity: startSetAccount(): email(1): " +fbAuth.getCurrentUser().getEmail());
//
//        if(!TextUtils.isEmpty(name)) {
//
//            progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
//            progressDialog.show();
//
//            usersFBDatabaseRef.child(userId).child(CONST.USER_NAME).setValue(name);
//            //usersFBDatabaseRef.child(userId).child("userName").setValue(name);
//
//            progressDialog.dismiss();
//
//            startActivity(new Intent(   SetAccountActivity.this,
//                                        MainActivity.class));
//        }
    }

    // ------------------------------  LISTENERS ----------------------------------------- //

    OnCompleteListener<Void> createUserInDBCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "SetAccountActivity: createUserInDBCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                progressDialog.dismiss();

                Intent mainIntent = new Intent( SetAccountActivity.this,
                                                MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

                finish();
            }
            else {
                progressDialog.hide();

                Snackbar.make(  setAccountContainer,
                                R.string.error_set_account_data,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    // ------------------------------ CLICK LISTENERS ----------------------------------------- //

    View.OnClickListener onSetAccountClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSetAccount();
        }
    };
}
