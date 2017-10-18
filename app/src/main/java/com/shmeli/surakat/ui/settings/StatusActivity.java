package com.shmeli.surakat.ui.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

public class StatusActivity extends AppCompatActivity {

    private Toolbar             statusPageToolbar;

    private Button              setStatusButton;
    private EditText            statusEditText;

    private RelativeLayout      statusContainer;

    private ProgressDialog      progressDialog;

    private DatabaseReference   databaseReference;

    private String              currentStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent statusIntent = getIntent();

        if(statusIntent != null) {
            currentStatus = statusIntent.getStringExtra("currentStatus");
        }

        statusContainer     = UiUtils.findView(this, R.id.statusContainer);

        statusPageToolbar   = UiUtils.findView(this, R.id.statusPageToolbar);
        setSupportActionBar(statusPageToolbar);
        getSupportActionBar().setTitle(R.string.text_set_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStatusButton     = UiUtils.findView(this, R.id.setStatusButton);
        setStatusButton.setOnClickListener(setStatusOnClickListener);

        statusEditText      = UiUtils.findView(this, R.id.statusEditText);
        if(!TextUtils.isEmpty(currentStatus))
            statusEditText.setText(currentStatus);

        String userId       = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference   = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(userId);

        progressDialog      = new ProgressDialog(this);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener setStatusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String newStatus = statusEditText.getText().toString();

            if(!TextUtils.isEmpty(newStatus)) {

                progressDialog.setTitle(getResources().getString(R.string.text_saving_changes));
                progressDialog.setMessage(getResources().getString(R.string.message_saving_changes));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                databaseReference.child("userStatus").setValue(newStatus).addOnCompleteListener(onChangeStatusCompleteListener);
            }
            else {
                Snackbar.make(  statusContainer,
                                R.string.error_empty_field,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

    OnCompleteListener<Void> onChangeStatusCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            if(task.isSuccessful()) {

                progressDialog.dismiss();



//                Intent mainIntent = new Intent( RegisterActivity.this,
//                        MainActivity.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mainIntent);
//
//                finish();
            }
            else {
                progressDialog.hide();

                Snackbar.make(  statusContainer,
                                R.string.error_save_changes,
                                Snackbar.LENGTH_LONG).show();
            }
        }
    };

}
