package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText            nameEditText;
    private EditText            emailEditText;
    private EditText            passwordEditText;

    private Button              createButton;
//    private Button              backButton;

    private RelativeLayout      registerContainer;

    private Toolbar             registerPageToolbar;

    private FirebaseAuth        fbAuth;
    private DatabaseReference   databaseReference;

    private ProgressDialog      progressDialog;

    private String              name        = "";
    private String              email       = "";
    private String              password    = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fbAuth              = FirebaseAuth.getInstance();
        //databaseReference   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);

        progressDialog      = new ProgressDialog(this);

        nameEditText        = UiUtils.findView(this, R.id.nameEditText);
        emailEditText       = UiUtils.findView(this, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(this, R.id.passwordEditText);

        createButton        = UiUtils.findView(this, R.id.createButton);
        createButton.setOnClickListener(createClickListener);

//        backButton          = UiUtils.findView(this, R.id.backButton);
//        backButton.setOnClickListener(backClickListener);

        registerContainer   = UiUtils.findView(this, R.id.registerContainer);
        registerPageToolbar = UiUtils.findView(this, R.id.registerPageToolbar);
        setSupportActionBar(registerPageToolbar);
        getSupportActionBar().setTitle(R.string.text_create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void startRegister() {

        name         = nameEditText.getText().toString();
        email        = emailEditText.getText().toString();
        password     = passwordEditText.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressDialog.setTitle(getResources().getString(R.string.message_creating_account));
            progressDialog.setMessage(getResources().getString(R.string.message_create_account_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCreateUserCompleteListener);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startRegister();
        }
    };

    /*View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent loginIntent = new Intent(RegisterActivity.this,
                                            LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
    };*/

    OnCompleteListener<AuthResult> onCreateUserCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if(task.isSuccessful()) {

                progressDialog.dismiss();

                String userId       = fbAuth.getCurrentUser().getUid();
                String deviceToken  = FirebaseInstanceId.getInstance().getToken();

                databaseReference   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(userId);

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put(CONST.USER_IMAGE,           CONST.DEFAULT_VALUE);
                userMap.put(CONST.USER_NAME,            name);
                userMap.put(CONST.USER_DEVICE_TOKEN,    deviceToken);
                userMap.put(CONST.USER_STATUS,          CONST.USER_ONLINE_STATUS);
                userMap.put(CONST.USER_THUMB_IMAGE,     CONST.DEFAULT_VALUE);

//                userMap.put("userName",     name);
//                userMap.put("userStatus",   CONST.USER_ONLINE_STATUS);
//                userMap.put("userImage",    CONST.DEFAULT_VALUE);
//                userMap.put("thumbImage",   CONST.DEFAULT_VALUE);

                databaseReference.setValue(userMap).addOnCompleteListener(onCreateUserInDBCompleteListener);


                        /*DatabaseReference createUser = databaseReference.child(userId);

                        //createUser.child("userId").setValue();
                        createUser.child("userName").setValue(name);*/

                        /*Intent mainIntent = new Intent( RegisterActivity.this,
                                                        MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);

                        finish();*/
            }
            else {

                progressDialog.hide();

                Snackbar.make(  registerContainer,
                        R.string.error_register_user,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> onCreateUserInDBCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "RegisterActivity: onCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                progressDialog.dismiss();

                Intent mainIntent = new Intent( RegisterActivity.this,
                                                MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

                finish();
            }
            else {
                progressDialog.hide();

                Snackbar.make(  registerContainer,
                                R.string.error_register_user,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
