package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

public class SetAccountActivity extends AppCompatActivity {

    private EditText            setAccountNameEditText;
//    private EditText setAccountEmailEditText;
//    private EditText setAccountPasswordEditText;

    private Button              setAccountButton;
    private ProgressDialog      progressDialog;

    private FirebaseAuth        fbAuth;
    private DatabaseReference   usersFBDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account);

        usersFBDatabaseRef  = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);
        fbAuth              = FirebaseAuth.getInstance();

        progressDialog      = new ProgressDialog(this);

        setAccountNameEditText      = UiUtils.findView(this, R.id.setAccountNameEditText);
//        setAccountEmailEditText     = UiUtils.findView(this, R.id.setAccountEmailEditText);
//        setAccountPasswordEditText  = UiUtils.findView(this, R.id.setAccountPasswordEditText);

        setAccountButton = UiUtils.findView(this, R.id.setAccountButton);
        setAccountButton.setOnClickListener(onSetAccountClick);
    }

    private void startSetAccount() {

        String name     = setAccountNameEditText.getText().toString().trim();
//        String email    = setAccountNameEditText.getText().toString().trim();
//        String password = setAccountNameEditText.getText().toString().trim();

        String userId = fbAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name)) {

            progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
            progressDialog.show();

            usersFBDatabaseRef.child(userId).child("userName").setValue(name);

            progressDialog.dismiss();

            startActivity(new Intent(   SetAccountActivity.this,
                                        MainActivity.class));
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener onSetAccountClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSetAccount();
        }
    };
}
