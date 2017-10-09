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
    private DatabaseReference   usersFBDatabaseRef;

//    private FirebaseAuth.AuthStateListener fbAuthListener;

    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbAuth              = FirebaseAuth.getInstance();

        usersFBDatabaseRef  = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);
        usersFBDatabaseRef.keepSynced(true);

//        fbAuthListener      = authStateListener;

        progressDialog      = new ProgressDialog(this);

        signInContainer     = UiUtils.findView(this, R.id.signInContainer);
        emailEditText       = UiUtils.findView(this, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(this, R.id.passwordEditText);

        singInButton        = UiUtils.findView(this, R.id.signInButton);
        singInButton.setOnClickListener(signInClickListener);

        registerButton      = UiUtils.findView(this, R.id.registerButton);
        registerButton.setOnClickListener(registerClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        fbAuth.addAuthStateListener(fbAuthListener);
    }

    // ----------------------------------------------------------------------- //

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

            progressDialog.setMessage(getResources().getString(R.string.message_checking_sign_in));
            progressDialog.show();

            fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
        }
    }

    private void checkUserExists() {
        //Log.e("LOG", "LoginActivity: checkUserExists()");

        userId = fbAuth.getCurrentUser().getUid();

        usersFBDatabaseRef.addValueEventListener(valueEventListener);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener signInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSignIn();
        }
    };

    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(   LoginActivity.this,
                                        RegisterActivity.class));
        }
    };

    OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Log.e("LOG", "LoginActivity: onCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            progressDialog.dismiss();

            if(task.isSuccessful()) {
                checkUserExists();
            }
            else {
                Snackbar.make(  signInContainer,
                                R.string.error_sign_in,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

//    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//            if(firebaseAuth.getCurrentUser() != null) {
//
//                // startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                Log.e("LOG", "LoginActivity: valueEventListener: move to MainActivity");
//            }
//        }
//    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.e("LOG", "LoginActivity: valueEventListener: (dataSnapshot.hasChild(" +userId+ ")): " +(dataSnapshot.hasChild(userId)));

            if(dataSnapshot.hasChild(userId)) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            else {
                startActivity(new Intent(LoginActivity.this, SetAccountActivity.class));

//                Snackbar.make(  signInContainer,
//                                R.string.error_account,
//                                Snackbar.LENGTH_LONG).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
