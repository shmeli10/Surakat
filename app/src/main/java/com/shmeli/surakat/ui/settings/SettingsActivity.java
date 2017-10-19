package com.shmeli.surakat.ui.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Random;


public class SettingsActivity extends AppCompatActivity {

    private Button              settingPageChangeImage;
    private Button              settingPageChangeStatus;

    private CircleImageView     settingPageAvatar;
    private TextView            settingPageUserName;
    private TextView            settingPageUserStatus;

    private DatabaseReference   userFBDatabaseRef;

    private FirebaseUser        fbCurrentUser;

    private StorageReference    fbStorageReference;

    private ProgressDialog      progressDialog;

    private String              currentUserId;

    private static final int    GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingPageChangeImage  = UiUtils.findView(this, R.id.settingPageChangeImage);
        settingPageChangeImage.setOnClickListener(changeImageOnClickListener);

        settingPageChangeStatus = UiUtils.findView(this, R.id.settingPageChangeStatus);
        settingPageChangeStatus.setOnClickListener(changeStatusOnClickListener);

        settingPageAvatar       = UiUtils.findView(this, R.id.settingPageAvatar);
        settingPageUserName     = UiUtils.findView(this, R.id.settingPageUserName);
        settingPageUserStatus   = UiUtils.findView(this, R.id.settingPageUserStatus);

        fbCurrentUser           = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId           = fbCurrentUser.getUid();

        fbStorageReference      = FirebaseStorage.getInstance().getReference();

        userFBDatabaseRef       = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(currentUserId);
        userFBDatabaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle(getResources().getString(R.string.message_uploading_image));
                progressDialog.setMessage(getResources().getString(R.string.message_uploading_image_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                StringBuilder imageIdSB = new StringBuilder("");
                //imageIdSB.append(new Date().getTime());
                //imageIdSB.append(getRandomString());
                imageIdSB.append(currentUserId);
                imageIdSB.append(".jpg");

                StorageReference filePath = fbStorageReference.child("images").child(imageIdSB.toString());

                try {
                    filePath.putFile(resultUri).addOnSuccessListener(uploadImageSuccessListener).addOnFailureListener(uploadImageFailureListener);
                }
                catch (Exception exc) {
                    Log.e("LOG", "SettingsActivity: onActivityResult(): putFile error: " +exc.getMessage().toString());

                    progressDialog.dismiss();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();

                Log.e("LOG", "SettingsActivity: onActivityResult(): Crop Image error: " +result.getError().getMessage().toString());
            }
        }
    }

    private String getRandomString() {

        Random generator = new Random();

        int randomLength = generator.nextInt(10);

        StringBuilder randomSB = new StringBuilder();

        char tempChar;

        for(int i=0; i<randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);

            randomSB.append(tempChar);
        }

        return randomSB.toString();
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener changeImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult( Intent.createChooser(galleryIntent, "SELECT IMAGE"),
                                    GALLERY_PICK);
        }
    };

    View.OnClickListener changeStatusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String currentStatus = settingPageUserStatus.getText().toString();

            Intent statusIntent = new Intent(   SettingsActivity.this,
                                                StatusActivity.class);

            statusIntent.putExtra("currentStatus", currentStatus);
            startActivity(statusIntent);
        }
    };

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

            Picasso.with(SettingsActivity.this).load(userImage).into(settingPageAvatar);

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

    OnSuccessListener uploadImageSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            String downloadUrl = taskSnapshot.getDownloadUrl().toString();

            Log.e("LOG", "SettingsActivity: uploadImageSuccessListener: upload image success: url: " +downloadUrl);

            userFBDatabaseRef.child("userImage").setValue(downloadUrl).addOnCompleteListener(saveImageUrlCompleteListener);
            //userFBDatabaseRef.child("thumbImage").setValue();

            //progressDialog.dismiss();
        }
    };

    OnFailureListener uploadImageFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {

            Log.e("LOG", "SettingsActivity: uploadImageFailureListener: upload image error");

            progressDialog.dismiss();
        }
    };

    OnCompleteListener saveImageUrlCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {

            if(task.isSuccessful()) {

                progressDialog.dismiss();

                Log.e("LOG", "SettingsActivity: saveImageUrlCompleteListener: save image url success");
                //progressDialog.hide();
            }
//            else {
//                Log.e("LOG", "SettingsActivity: saveImageUrlCompleteListener: save image url error");
//                progressDialog.dismiss();
//            }
        }
    };
}
