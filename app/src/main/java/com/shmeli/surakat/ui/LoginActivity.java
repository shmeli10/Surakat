package com.shmeli.surakat.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shmeli.surakat.R;
import com.shmeli.surakat.utils.UiUtils;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout loginContainer;

    private EditText    emailEditText;
    private EditText    passwordEditText;

    private Button      singnInButton;
    private Button      registerButton;

    private FirebaseAuth fbAuth;

    private FirebaseAuth.AuthStateListener fbAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbAuth              = FirebaseAuth.getInstance();

        fbAuthListener      = authStateListener;

        loginContainer      = UiUtils.findView(this, R.id.loginContainer);
        emailEditText       = UiUtils.findView(this, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(this, R.id.passwordEditText);

        singnInButton       = UiUtils.findView(this, R.id.signInButton);
        singnInButton.setOnClickListener(loginClickListener);

        registerButton      = UiUtils.findView(this, R.id.registerButton);
        registerButton.setOnClickListener(registerClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fbAuth.addAuthStateListener(fbAuthListener);
    }

    // ----------------------------------------------------------------------- //

    private void startSignIn() {

        String email    = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Snackbar.make(  loginContainer,
                            R.string.error_empty_field,
                            Snackbar.LENGTH_LONG).show();
        }
        else {

            fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener loginClickListener = new View.OnClickListener() {
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

            if(!task.isSuccessful()) {

                Snackbar.make(  loginContainer,
                                R.string.error_login,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if(firebaseAuth.getCurrentUser() != null) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }
    };
}
