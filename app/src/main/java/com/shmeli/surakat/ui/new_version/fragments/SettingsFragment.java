package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by Serghei Ostrovschi on 12/19/17.
 */
public class SettingsFragment extends ParentFragment {

    private static SettingsFragment  instance;

    private static final int    aspectRatioX = 1;
    private static final int    aspectRatioY = 1;

    private static final int    minCropWindowWidth  = 500;
    private static final int    minCropWindowHeight = 500;

    private static final int    imageMaxWidth           = 200;
    private static final int    imageMaxHeight          = 200;
    private static final int    imageQuality            = 75;
    private static final int    imageCompressQuality    = 100;

    private static final int    GALLERY_PICK = 1;

    private View                view;

    private LinearLayout        settingsContainer;

    private CircleImageView     avatarImageView;

    private TextView            nameTextView;
    private TextView            statusTextView;

    private Button              changeImageButton;
    private Button              changeStatusButton;

    private DatabaseReference   currentUserFBDatabaseRef;

    private Uri                 croppedImageUri;
    private StringBuilder       croppedImageIdSB = new StringBuilder("");

    private String              uploadedImageUrl = "";
    private String              uploadedThumbUrl = "";

    private String              currentUserImageUrl     = "";
    private String              changeImageChooserTitle = "";

    private InternalActivity    internalActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {

        if(instance == null) {
            instance = new SettingsFragment();
        }

        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "SettingsFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_settings,
                                container,
                                false);

        init();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu            menu,
                                    MenuInflater    inflater) {
        //Log.e("LOG", "SettingsFragment: onCreateOptionsMenu()");

        // hide and disable menu_settings in app bar
        menu.getItem(0).setVisible(false);
        menu.getItem(0).setEnabled(false);

        super.onCreateOptionsMenu(  menu,
                                    inflater);
    }

    @Override
    public void onActivityResult(   int     requestCode,
                                    int     resultCode,
                                    Intent  data) {
        super.onActivityResult( requestCode,
                                resultCode,
                                data);

        if( requestCode == GALLERY_PICK &&
            resultCode == internalActivity.RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(aspectRatioX,
                                    aspectRatioY)
                    .setMinCropWindowSize(  minCropWindowWidth,
                                            minCropWindowHeight)
                    .start( internalActivity,
                            this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == internalActivity.RESULT_OK) {

                internalActivity.showProgressDialog(getResources().getString(R.string.message_uploading_image),
                                                    getResources().getString(R.string.message_uploading_image_wait));

                croppedImageUri = result.getUri();

                croppedImageIdSB.append(internalActivity.getCurrentUserId());
                croppedImageIdSB.append(".jpg");

                StorageReference filePath = internalActivity.getImagesFBStorageRef().child(croppedImageIdSB.toString()); // fbStorageReference.child("images").child(croppedImageIdSB.toString());

                filePath.putFile(croppedImageUri).addOnCompleteListener(uploadImageOnCompleteListener);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();

                Log.e("LOG", "SettingsFragment: onActivityResult(): Crop Image error: " +result.getError().getMessage().toString());
            }
        }
    }

    // ----------------------------------- INIT ----------------------------------------- //

    private void init() {
        Log.e("LOG", "SettingsFragment: init()");

        internalActivity = (InternalActivity) getActivity();

        settingsContainer   = UiUtils.findView( view,
                                                R.id.settingsContainer);

        avatarImageView     = UiUtils.findView( view,
                                                R.id.settingsAvatar);

        nameTextView        = UiUtils.findView( view,
                                                R.id.settingsName);

        statusTextView      = UiUtils.findView( view,
                                                R.id.settingsStatus);

        changeImageButton   = UiUtils.findView( view,
                                                R.id.settingsChangeImage);
        changeImageButton.setOnClickListener(changeImageClickListener);

        changeStatusButton  = UiUtils.findView( view,
                                                R.id.settingsChangeStatus);
        changeStatusButton.setOnClickListener(changeStatusClickListener);

        currentUserFBDatabaseRef = internalActivity.getCurrentUserFBDatabaseRef();

        if(currentUserFBDatabaseRef != null) {

            currentUserFBDatabaseRef.keepSynced(true);
            currentUserFBDatabaseRef.addValueEventListener(selectedUserProfileValueListener);
        }
        else {
            Log.e("LOG", "SettingsFragment: init(): currentUserFBDatabaseRef is null");
        }

        // without this line, onCreateOptionsMenu() will not be invoked
        setHasOptionsMenu(true);

        changeImageChooserTitle = getActivity().getResources().getString(R.string.text_select_image);
    }

    // ------------------------------ VALUE EVENT LISTENERS ----------------------------------- //

    ValueEventListener selectedUserProfileValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                //Log.e("LOG", "SettingsFragment: valueEventListener: dataSnapshot: " +dataSnapshot.toString());

                String currentUserName     = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                String currentUserStatus   = dataSnapshot.child(CONST.USER_STATUS).getValue().toString();

                currentUserImageUrl        = dataSnapshot.child(CONST.USER_IMAGE).getValue().toString();

                Log.e("LOG", "UserProfileFragment: currentUserName: " +currentUserName);
                if( (!TextUtils.isEmpty(currentUserName)) &&
                    (!currentUserName.equals(CONST.DEFAULT_VALUE))) {

                    nameTextView.setText(currentUserName);
                }

                Log.e("LOG", "UserProfileFragment: currentUserStatus: " +currentUserStatus);
                if( (!TextUtils.isEmpty(currentUserStatus)) &&
                    (!currentUserStatus.equals(CONST.DEFAULT_VALUE))) {

                    statusTextView.setText(currentUserStatus);
                }

                if (!currentUserImageUrl.equals(CONST.DEFAULT_VALUE)) {
                    Picasso.with(getActivity())
                            .load(currentUserImageUrl)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(  avatarImageView,
                                    loadImageCallback);
                }
            }
            else {
                Log.e("LOG", "UserProfileFragment: selectedUserProfileValueListener: dataSnapshot is null");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener uploadImageOnCompleteListener = new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> imageTask) {

            //Log.e("LOG", "SettingsFragment: uploadImageOnCompleteListener(): imageTask.isSuccessful(): " +imageTask.isSuccessful());

            if(imageTask.isSuccessful()) {

                uploadedImageUrl = imageTask.getResult().getDownloadUrl().toString();

                uploadThumb();

                //Log.e("LOG", "SettingsFragment: uploadImageOnCompleteListener: upload image success: url: " +uploadedImageUrl);
            }
            else {

                Log.e("LOG", "SettingsFragment: uploadImageOnCompleteListener: upload image error");
            }
        }
    };

    OnCompleteListener uploadThumbOnCompleteListener = new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

            //Log.e("LOG", "SettingsFragment: uploadThumbOnCompleteListener(): thumbTask.isSuccessful(): " +thumbTask.isSuccessful());

            if(thumbTask.isSuccessful()) {

                uploadedThumbUrl = thumbTask.getResult().getDownloadUrl().toString();
                //Log.e("LOG", "SettingsFragment: uploadThumbOnCompleteListener: upload thumb success: uploadedThumbUrl: " +uploadedThumbUrl);

                Map uploadMap = new HashMap<>();
                uploadMap.put(CONST.USER_IMAGE,         uploadedImageUrl);
                uploadMap.put(CONST.USER_THUMB_IMAGE,   uploadedThumbUrl);

                //internalActivity.getCurrentUserFBDatabaseRef().updateChildren(uploadMap).addOnCompleteListener(saveImageUrlCompleteListener);
                currentUserFBDatabaseRef.updateChildren(uploadMap).addOnCompleteListener(saveImageUrlCompleteListener);
            }
            else {

                Log.e("LOG", "SettingsFragment: uploadThumbOnCompleteListener: upload thumb error");
            }
        }
    };

    OnCompleteListener saveImageUrlCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {

            //Log.e("LOG", "SettingsFragment: saveImageUrlCompleteListener(): task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                internalActivity.dismissProgressDialog();

                //Log.e("LOG", "SettingsFragment: saveImageUrlCompleteListener: save image url success");
            }
            else {
                Log.e("LOG", "SettingsFragment: saveImageUrlCompleteListener: save image url error");
            }
        }
    };

    // ------------------------------ BUTTON CLICK LISTENER ------------------------------------- //

    View.OnClickListener changeImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Log.e("LOG", "SettingsFragment: changeImageButton Click ");

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult( Intent.createChooser(   galleryIntent,
                                                            changeImageChooserTitle.toUpperCase()),
                                    GALLERY_PICK);
        }
    };

    View.OnClickListener changeStatusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "SettingsFragment: changeStatusButton Click ");
        }
    };

    // ----------------------------------- OTHER ----------------------------------------------//

    private void uploadThumb() {
        //Log.e("LOG", "SettingsFragment: uploadThumb()");

        File thumbFile = new File(croppedImageUri.getPath());

        Bitmap thumbBitmap = new Compressor(internalActivity)
                .setMaxWidth(imageMaxWidth)
                .setMaxHeight(imageMaxHeight)
                .setQuality(imageQuality)
                .compressToBitmap(thumbFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbBitmap.compress(   Bitmap.CompressFormat.JPEG,
                                imageCompressQuality,
                                byteArrayOutputStream);

        byte[] thumbDataArray = byteArrayOutputStream.toByteArray();

        StorageReference thumbFilePath = internalActivity.getImagesFBStorageRef().child("thumbs").child(croppedImageIdSB.toString());

        UploadTask uploadTask = thumbFilePath.putBytes(thumbDataArray);
        uploadTask.addOnCompleteListener(uploadThumbOnCompleteListener);
    }

    private Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() { }

        @Override
        public void onError() {

            Picasso.with(getActivity())
                    .load(currentUserImageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(avatarImageView);
        }
    };
}
