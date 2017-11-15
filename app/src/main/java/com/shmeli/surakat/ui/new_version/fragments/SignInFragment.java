package com.shmeli.surakat.ui.new_version.fragments;

import android.content.Context;

import android.net.Uri;
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
import com.shmeli.surakat.R;
import com.shmeli.surakat.ui.new_version.ExternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    private static SignInFragment  instance;

    private View            view;

    private RelativeLayout  signInContainer;

    private EditText        emailEditText;
    private EditText        passwordEditText;

    private Button          singInButton;
    private Button          registerButton;

    private ExternalActivity externalActivity;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignInFragment.
     */
    public static SignInFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new SignInFragment();
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

        view = inflater.inflate(R.layout.fragment_sign_in,
                                container,
                                false);

        externalActivity = (ExternalActivity) getActivity();
        externalActivity.setToolbarTitle(R.string.text_sign_in);

        signInContainer     = UiUtils.findView( view,
                                                R.id.signInContainer);

        emailEditText       = UiUtils.findView( view,
                                                R.id.emailEditText);

        passwordEditText    = UiUtils.findView( view,
                                                R.id.passwordEditText);

        singInButton        = UiUtils.findView( view,
                                                R.id.signInButton);
        singInButton.setOnClickListener(signInClickListener);

        registerButton      = UiUtils.findView( view,
                                                R.id.registerButton);
        registerButton.setOnClickListener(registerClickListener);

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

    View.OnClickListener signInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "SignInFragment: signInClickListener: onClick()");

            startSignIn();
        }
    };

    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "SignInFragment: registerClickListener: onClick()");

            moveToRegisterFragment();

//            startActivity(new Intent(   LoginActivity.this,
//                                        RegisterActivity.class));
        }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener<AuthResult> signInCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Log.e("LOG", "SignInFragment: onSignInCompleteListener: onComplete(): task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                externalActivity.dismissProgressDialog();

                // user initialized successfully
                if(externalActivity.initCurrentUser()) {
                    Log.e("LOG", "SignInFragment: signInCompleteListener: user initialized successfully");

                    // user exists in DB
                    if(externalActivity.currentUserExistsInFBDB()) {
                        Log.e("LOG", "SignInFragment: signInCompleteListener: move to InternalActivity");

                        externalActivity.moveToInternalActivity();

                        /*
                        String deviceToken  = FirebaseInstanceId.getInstance().getToken();
                        currentUserId       = fbAuth.getCurrentUser().getUid();

                        if(!TextUtils.isEmpty(currentUserId)) {

                            currentUserFBDatabaseRef = usersFBDatabaseRef.child(currentUserId);
                            currentUserFBDatabaseRef.child(CONST.USER_DEVICE_TOKEN)
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(setDeviceTokenCompleteListener);
                        }*/

                        //externalActivity.setCurrentUserIsOnline(true);
                    }
                    // user does not exist in DB
                    else {
                        Log.e("LOG", "SignInFragment: signInCompleteListener: move to FillAccountFragment");
                    }
                }
                // user initialize error
                else {
                    Log.e("LOG", "SignInFragment: signInCompleteListener: user initialize error");

                    externalActivity.showSnackBar(  signInContainer,
                                                    R.string.error_sign_in,
                                                    Snackbar.LENGTH_LONG);
                }
            }
            else {
                Log.e("LOG", "SignInFragment: startSignIn(): FBAuth is null: " +(externalActivity.getFBAuth() == null));

                externalActivity.hideProgressDialog();

                externalActivity.showSnackBar(  signInContainer,
                                                R.string.error_sign_in,
                                                Snackbar.LENGTH_LONG);
            }
        }
    };

    // ------------------------------ OTHER --------------------------------------------------- //

    private void moveToRegisterFragment() {
        Log.e("LOG", "SignInFragment: moveToRegisterFragment()");

        externalActivity.setFragment(   RegisterFragment.newInstance(),
                                        true,
                                        true);
    }

    private void startSignIn() {
        Log.e("LOG", "SignInFragment: startSignIn()");

        String email    = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if( TextUtils.isEmpty(email)    ||
            TextUtils.isEmpty(password)) {

            externalActivity.showSnackBar(  signInContainer,
                                            R.string.error_empty_field,
                                            Snackbar.LENGTH_LONG);
        }
        else {

//            progressDialog.setTitle(getResources().getString(R.string.message_checking_sign_in));
//            progressDialog.setMessage(getResources().getString(R.string.message_check_account_wait));
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();

            externalActivity.showProgressDialog(getResources().getString(R.string.message_checking_sign_in),
                                                getResources().getString(R.string.message_check_account_wait));

            // -------------------------------------------------------------------------------- //

//            // Set title divider color
//            int titleDividerId = getResources().getIdentifier(  "titleDivider",
//                                                                "id",
//                                                                "android");
//            View titleDivider = progressDialog.findViewById(titleDividerId);
//
//            if (titleDivider != null)
//                titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            // -------------------------------------------------------------------------------- //

            Log.e("LOG", "SignInFragment: startSignIn(): FBAuth is null: " +(externalActivity.getFBAuth() == null));

            if(externalActivity.getFBAuth() != null) {

                externalActivity.getFBAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signInCompleteListener);
            }
        }
    }
}
