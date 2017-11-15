package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;

import android.os.Bundle;

import android.app.Fragment;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.MainActivity;
import com.shmeli.surakat.ui.RegisterActivity;
import com.shmeli.surakat.ui.new_version.ExternalActivity;
import com.shmeli.surakat.utils.UiUtils;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends ParentFragment {

    private static RegisterFragment  instance;

    private View            view;

    private RelativeLayout  registerContainer;

    private EditText        emailEditText;
    private EditText        nameEditText;
    private EditText        passwordEditText;

    private Button          createButton;

    private ExternalActivity externalActivity;

    private String          name        = "";
    private String          email       = "";
    private String          password    = "";

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new RegisterFragment();
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_register,
                                container,
                                false);

        externalActivity    = (ExternalActivity) getActivity();
        externalActivity.setToolbarTitle(R.string.text_create_an_account);

        registerContainer   = UiUtils.findView( view,
                                                R.id.registerContainer);

        emailEditText       = UiUtils.findView( view,
                                                R.id.registerFragmentEmailEditText);

        nameEditText        = UiUtils.findView( view,
                                                R.id.registerFragmentNameEditText);

        passwordEditText    = UiUtils.findView( view,
                                                R.id.registerFragmentPasswordEditText);

        createButton        = UiUtils.findView( view,
                                                R.id.registerFragmentCreateButton);
        createButton.setOnClickListener(createClickListener);

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

    // ------------------------------ ON CLICK LISTENERS -------------------------------------- //

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startRegister();
        }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener<AuthResult> createAccountCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Log.e("LOG", "RegisterFragment: createAccountCompleteListener: task isSuccessful: " +(task.isSuccessful()));

            if(task.isSuccessful()) {

                externalActivity.dismissProgressDialog();

                // user initialized successfully
                if(externalActivity.initCurrentUser()) {
                    Log.e("LOG", "RegisterFragment: createAccountCompleteListener: user initialized successfully");

                    // user exists in DB
                    if(externalActivity.currentUserExistsInFBDB()) {

                        DatabaseReference currentUserFBDBRef = externalActivity.getCurrentUserFBDatabaseRef();

                        // if current user Firebase DB reference link is not null
                        if(currentUserFBDBRef != null) {

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(CONST.USER_IMAGE,           CONST.DEFAULT_VALUE);
                            userMap.put(CONST.USER_NAME,            name);
                            userMap.put(CONST.USER_STATUS,          CONST.DEFAULT_VALUE);
                            userMap.put(CONST.USER_THUMB_IMAGE,     CONST.DEFAULT_VALUE);

                            // --------------------------------------------------------------- //

                            String deviceToken = externalActivity.getDeviceToken();

                            if(!TextUtils.isEmpty(deviceToken))
                                userMap.put(CONST.USER_DEVICE_TOKEN,    deviceToken);

                            // --------------------------------------------------------------- //

                            currentUserFBDBRef.setValue(userMap)
                                    .addOnCompleteListener(createUserInFBDBCompleteListener);

                        }
                        // if current user Firebase DB reference link is null
                        else {
                            Log.e("LOG", "RegisterFragment: createAccountCompleteListener: move to FillAccountFragment(1)");
                        }
                    }
                    // user does not exist in DB
                    else {
                        Log.e("LOG", "RegisterFragment: createAccountCompleteListener: move to FillAccountFragment(0)");
                    }
                }
                // user initialize error
                else {
                    Log.e("LOG", "RegisterFragment: createAccountCompleteListener: user initialize error");

                    externalActivity.showSnackBar(  registerContainer,
                                                    R.string.error_create_an_account,
                                                    Snackbar.LENGTH_LONG);
                }
            }
            else {

                externalActivity.hideProgressDialog();

                externalActivity.showSnackBar(  registerContainer,
                                                R.string.error_create_an_account,
                                                Snackbar.LENGTH_LONG);
            }
        }
    };

    OnCompleteListener<Void> createUserInFBDBCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Log.e("LOG", "RegisterFragment: createUserInFBDBCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                externalActivity.dismissProgressDialog();

                Log.e("LOG", "RegisterFragment: createUserInFBDBCompleteListener: move to InternalActivity");
                externalActivity.moveToInternalActivity();
            }
            else {

                externalActivity.hideProgressDialog();

                externalActivity.showSnackBar(  registerContainer,
                                                R.string.error_create_an_account,
                                                Snackbar.LENGTH_LONG);
            }
        }
    };

    // ------------------------------ OTHER --------------------------------------------------- //

    private void startRegister() {
        Log.e("LOG", "RegisterFragment: startRegister()");

        name        = nameEditText.getText().toString();
        email       = emailEditText.getText().toString();
        password    = passwordEditText.getText().toString();

        if( TextUtils.isEmpty(name)    ||
            TextUtils.isEmpty(email)   ||
            TextUtils.isEmpty(password)) {

            Log.e("LOG", "RegisterFragment: startRegister(): create new account...");

            externalActivity.showSnackBar(  registerContainer,
                                            R.string.error_empty_field,
                                            Snackbar.LENGTH_LONG);
        }
        else {

            externalActivity.showProgressDialog(getResources().getString(R.string.message_creating_account),
                                                getResources().getString(R.string.message_create_account_wait));

            Log.e("LOG", "RegisterFragment: startRegister(): FBAuth is null: " +(externalActivity.getFBAuth() == null));

            if(externalActivity.getFBAuth() != null) {

                externalActivity.getFBAuth().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(createAccountCompleteListener);
            }

//            progressDialog.setTitle(getResources().getString(R.string.message_creating_account));
//            progressDialog.setMessage(getResources().getString(R.string.message_create_account_wait));
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
//
//            fbAuth.createUserWithEmailAndPassword(userEmail, userPassword)
//                    .addOnCompleteListener(onCreateUserCompleteListener);
        }
    }
}
