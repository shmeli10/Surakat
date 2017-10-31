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

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout      signInContainer;

    private EditText            emailEditText;
    private EditText            passwordEditText;

    private Button              singInButton;
    private Button              registerButton;

    private ProgressDialog      progressDialog;

    private FirebaseAuth        fbAuth;

    private DatabaseReference   currentUserFBDatabaseRef;
    private DatabaseReference   rootFBDatabaseRef;
    private DatabaseReference   usersFBDatabaseRef;

    private String currentUserId = "";

    private boolean canReact = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Log.e("LOG", "LoginActivity: onCreate()");

        fbAuth              = FirebaseAuth.getInstance();

        rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
        usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
        usersFBDatabaseRef.keepSynced(true);

        progressDialog      = new ProgressDialog(this);

        signInContainer     = UiUtils.findView(this, R.id.signInContainer);
        emailEditText       = UiUtils.findView(this, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(this, R.id.passwordEditText);

        singInButton        = UiUtils.findView(this, R.id.signInButton);
        singInButton.setOnClickListener(signInClickListener);

        registerButton      = UiUtils.findView(this, R.id.registerButton);
        registerButton.setOnClickListener(registerClickListener);
    }

    // ------------------------------ OTHER --------------------------------------------------- //

    private void startSignIn() {
        Log.e("LOG", "LoginActivity: startSignIn()");

        String email    = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Snackbar.make(  signInContainer,
                            R.string.error_empty_field,
                            Snackbar.LENGTH_LONG).show();
        }
        else {

            progressDialog.setTitle(getResources().getString(R.string.message_checking_sign_in));
            progressDialog.setMessage(getResources().getString(R.string.message_check_account_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            fbAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signInCompleteListener);
        }
    }

    private void checkUserExists() {
        Log.e("LOG", "LoginActivity: checkUserExists()");

        usersFBDatabaseRef.addValueEventListener(getUsersDataListener);
    }

    // ------------------------------ ON CLICK LISTENERS -------------------------------------- //

    View.OnClickListener signInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "LoginActivity: signInClickListener: onClick()");

            startSignIn();
        }
    };

    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "LoginActivity: registerClickListener: onClick()");

            startActivity(new Intent(   LoginActivity.this,
                                        RegisterActivity.class));
        }
    };

    // ------------------------------ ON GET DATA LISTENERS ----------------------------------- //

    ValueEventListener getUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.e("LOG", "LoginActivity: getUsersDataListener: onDataChange(): canReact: " +canReact);

            if(canReact) {

                canReact = false;

                currentUserId = fbAuth.getCurrentUser().getUid();

                if(!TextUtils.isEmpty(currentUserId)) {

                    Log.e("LOG", "LoginActivity: getUsersDataListener: onDataChange(): (dataSnapshot.hasChild(" + currentUserId + ")): " + (dataSnapshot.hasChild(currentUserId)));

                    if (dataSnapshot.hasChild(currentUserId)) {

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        currentUserFBDatabaseRef = usersFBDatabaseRef.child(currentUserId);
                        currentUserFBDatabaseRef.child(CONST.USER_DEVICE_TOKEN)
                                .setValue(deviceToken)
                                .addOnCompleteListener(setDeviceTokenCompleteListener);

//                    Log.e("LOG", "LoginActivity: getUsersDataListener: onDataChange(): go to MainActivity");
//
//                    Intent mainIntent = new Intent( LoginActivity.this,
//                            MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(mainIntent);
//
//                    finish();
                    } else {

                        Log.e("LOG", "LoginActivity: getUsersDataListener: onDataChange(): go to SetAccountActivity");

                        Intent setAccountIntent = new Intent(   LoginActivity.this,
                                                                SetAccountActivity.class);
                        setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(setAccountIntent);

                        finish();
                    }
                }
                else {
                    Log.e("LOG", "LoginActivity: getUsersDataListener: onDataChange():current user id is empty or null");

                    progressDialog.hide();

                    Snackbar.make(  signInContainer,
                                    R.string.error_sign_in,
                                    Snackbar.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener<AuthResult> signInCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Log.e("LOG", "LoginActivity: onSignInCompleteListener: onComplete(): task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                progressDialog.dismiss();

                checkUserExists();

//                String deviceToken  = FirebaseInstanceId.getInstance().getToken();
//                currentUserId       = fbAuth.getCurrentUser().getUid();
//
//                if(!TextUtils.isEmpty(currentUserId)) {
//
//                    currentUserFBDatabaseRef = usersFBDatabaseRef.child(currentUserId);
//                    currentUserFBDatabaseRef.child(CONST.USER_DEVICE_TOKEN)
//                            .setValue(deviceToken)
//                            .addOnCompleteListener(setDeviceTokenCompleteListener);
//                }
            }
            else {
                progressDialog.hide();

                Snackbar.make(  signInContainer,
                                R.string.error_sign_in,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> setDeviceTokenCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "LoginActivity: setDeviceTokenCompleteListener: onComplete(): task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {
                //checkUserExists();

                Log.e("LOG", "LoginActivity: setDeviceTokenCompleteListener: onComplete(): go to MainActivity");

                Intent mainIntent = new Intent( LoginActivity.this,
                                                MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

                finish();
            }
            else {


            }
        }
    };
}
