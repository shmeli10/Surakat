package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText        nameEditText;
    private EditText        emailEditText;
    private EditText        passwordEditText;

    private Button          createButton;
    private Button          backButton;

    private FirebaseAuth        fbAuth;
    private DatabaseReference   databaseReference;

    private ProgressDialog  progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fbAuth              = FirebaseAuth.getInstance();
        databaseReference   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);

        progressDialog      = new ProgressDialog(this);

        nameEditText        = UiUtils.findView(this, R.id.nameEditText);
        emailEditText       = UiUtils.findView(this, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(this, R.id.passwordEditText);

        createButton        = UiUtils.findView(this, R.id.createButton);
        createButton.setOnClickListener(createClickListener);

        backButton          = UiUtils.findView(this, R.id.backButton);
        backButton.setOnClickListener(backClickListener);
    }

    private void startRegister() {

        final String name   = nameEditText.getText().toString();
        String email        = emailEditText.getText().toString();
        String password     = passwordEditText.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressDialog.setMessage(getResources().getString(R.string.message_creating_account));
            progressDialog.show();

            fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {

                        String userId = fbAuth.getCurrentUser().getUid();

                        DatabaseReference createUser = databaseReference.child(userId);

                        createUser.child("userName").setValue(name);

                        progressDialog.dismiss();

                        startActivity(new Intent(   RegisterActivity.this,
                                                    MainActivity.class));
                    }
                }
            });
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startRegister();
        }
    };

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent loginIntent = new Intent(RegisterActivity.this,
                                            LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
    };
}
