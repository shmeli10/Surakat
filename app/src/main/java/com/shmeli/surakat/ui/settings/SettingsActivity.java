package com.shmeli.surakat.ui.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import id.zelory.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    private Button              settingPageChangeImage;
    private Button              settingPageChangeStatus;

    private CircleImageView     settingPageAvatar;
    private TextView            settingPageUserName;
    private TextView            settingPageUserStatus;

    private ProgressDialog      progressDialog;
    private Toolbar             settingsPageToolbar;

    private DatabaseReference   userFBDatabaseRef;

    private FirebaseUser        fbCurrentUser;

    private StorageReference    fbStorageReference;

    private Uri                 croppedImageUri;
    private StringBuilder       croppedImageIdSB = new StringBuilder("");

    private String              uploadedImageUrl = "";
    private String              uploadedThumbUrl = "";

    private String              currentUserId;

    private static final int    GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsPageToolbar     = UiUtils.findView(this, R.id.settingsPageToolbar);
        setSupportActionBar(settingsPageToolbar);
        getSupportActionBar().setTitle(R.string.text_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingPageChangeImage  = UiUtils.findView(this, R.id.settingsPageChangeImage);
        settingPageChangeImage.setOnClickListener(changeImageOnClickListener);

        settingPageChangeStatus = UiUtils.findView(this, R.id.settingsPageChangeStatus);
        settingPageChangeStatus.setOnClickListener(changeStatusOnClickListener);

        settingPageAvatar       = UiUtils.findView(this, R.id.settingsPageAvatar);
        settingPageUserName     = UiUtils.findView(this, R.id.settingsPageUserName);
        settingPageUserStatus   = UiUtils.findView(this, R.id.settingsPageUserStatus);

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
                    .setMinCropWindowSize(500, 500)
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

                croppedImageUri = result.getUri();

                croppedImageIdSB.append(currentUserId);
                croppedImageIdSB.append(".jpg");

                StorageReference filePath = fbStorageReference.child("images").child(croppedImageIdSB.toString());

                filePath.putFile(croppedImageUri).addOnCompleteListener(uploadImageOnCompleteListener);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();

                Log.e("LOG", "SettingsActivity: onActivityResult(): Crop Image error: " +result.getError().getMessage().toString());
            }
        }
    }

    private void uploadThumb() {

        File thumbFile = new File(croppedImageUri.getPath());

        Bitmap thumbBitmap = new Compressor(this)
                .setMaxWidth(200)
                .setMaxHeight(200)
                .setQuality(75)
                .compressToBitmap(thumbFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbBitmap.compress(   Bitmap.CompressFormat.JPEG,
                100,
                byteArrayOutputStream);

        byte[] thumbDataArray = byteArrayOutputStream.toByteArray();

        StorageReference thumbFilePath = fbStorageReference.child("images").child("thumbs").child(croppedImageIdSB.toString());

        UploadTask uploadTask = thumbFilePath.putBytes(thumbDataArray);
        uploadTask.addOnCompleteListener(uploadThumbOnCompleteListener);
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

            String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
            String userStatus           = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();
            String userImageUrl         = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();
            String userThumbImageUrl    = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();

//            String userName     = dataSnapshot.child("userName").getValue().toString();
//            String userStatus   = dataSnapshot.child("userStatus").getValue().toString();
//            String userImage    = dataSnapshot.child("userImage").getValue().toString();
//            String thumbImage   = dataSnapshot.child("thumbImage").getValue().toString();

            settingPageUserName.setText(userName);
            settingPageUserStatus.setText(userStatus);

            if(!userImageUrl.equals(CONST.DEFAULT_VALUE))
                Picasso.with(SettingsActivity.this)
                        .load(userImageUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(settingPageAvatar);

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

    OnCompleteListener uploadImageOnCompleteListener = new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> imageTask) {

            if(imageTask.isSuccessful()) {

                uploadedImageUrl = imageTask.getResult().getDownloadUrl().toString();

                uploadThumb();

                Log.e("LOG", "SettingsActivity: uploadImageOnCompleteListener: upload image success: url: " +uploadedImageUrl);
            }
            else {

                Log.e("LOG", "SettingsActivity: uploadImageOnCompleteListener: upload image error");
            }
        }
    };

    OnCompleteListener uploadThumbOnCompleteListener = new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

            if(thumbTask.isSuccessful()) {

                uploadedThumbUrl = thumbTask.getResult().getDownloadUrl().toString();
                Log.e("LOG", "SettingsActivity: uploadThumbOnCompleteListener: upload thumb success: uploadedThumbUrl: " +uploadedThumbUrl);

                Map uploadMap = new HashMap<>();
                uploadMap.put(CONST.USER_IMAGE,         uploadedImageUrl);
                uploadMap.put(CONST.USER_THUMB_IMAGE,   uploadedThumbUrl);

                userFBDatabaseRef.updateChildren(uploadMap).addOnCompleteListener(saveImageUrlCompleteListener);
                //userFBDatabaseRef.child(CONST.USER_IMAGE).setValue(uploadedImageUrl).addOnCompleteListener(saveImageUrlCompleteListener);
                //userFBDatabaseRef.child("userImage").setValue(uploadedImageUrl).addOnCompleteListener(saveImageUrlCompleteListener);
            }
            else {

                Log.e("LOG", "SettingsActivity: uploadThumbOnCompleteListener: upload thumb error");
            }
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
