package com.shmeli.surakat.ui.new_version.fragments;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.ExternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class SignInFragment extends ParentFragment {

    private static SignInFragment  instance;

    private View                view;

    private LinearLayout        signInContainer;

    private EditText            emailEditText;
    private EditText            passwordEditText;

    private Button              singInButton;
    private Button              registerButton;

    private ExternalActivity    externalActivity;

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
            instance.setArguments(args);
        }

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

        Log.e("LOG", "SignInFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_sign_in,
                                container,
                                false);

        externalActivity    = (ExternalActivity) getActivity();

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

            //Log.e("LOG", "SignInFragment: signInClickListener: onClick()");

            startSignIn();
        }
    };

    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Log.e("LOG", "SignInFragment: registerClickListener: onClick()");

            moveToRegisterFragment();
        }
    };

    // ------------------------------ ON COMPLETE LISTENERS ----------------------------------- //

    OnCompleteListener<AuthResult> signInCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            //Log.e("LOG", "SignInFragment: onSignInCompleteListener: onComplete(): task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {

                externalActivity.dismissProgressDialog();

                // user initialized successfully
                if(externalActivity.initCurrentUser()) {
                    //Log.e("LOG", "SignInFragment: signInCompleteListener: user initialized successfully");

                    // user exists in DB
                    if(externalActivity.currentUserExistsInFBDB()) {
                    //if(!externalActivity.currentUserExistsInFBDB()) {   // ONLY FOR TEST
                        //Log.e("LOG", "SignInFragment: signInCompleteListener: user exists in DB");

                        setDeviceToken();

                        externalActivity.moveToInternalActivity();
                    }
                    // user does not exist in DB
                    else {
                        Log.e("LOG", "SignInFragment: signInCompleteListener: user does not exist in DB");

                        externalActivity.showSnackBar(  signInContainer,
                                                        R.string.error_sign_in,
                                                        Snackbar.LENGTH_LONG);
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
                Log.e("LOG", "SignInFragment: onSignInCompleteListener: task is not successful");

                externalActivity.hideProgressDialog();

                externalActivity.showSnackBar(  signInContainer,
                                                R.string.error_sign_in,
                                                Snackbar.LENGTH_LONG);
            }
        }
    };

    OnCompleteListener<Void> setDeviceTokenCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            //Log.e("LOG", "SignInFragment: setDeviceTokenCompleteListener: task.isSuccessful(): " +task.isSuccessful());

            if(task.isSuccessful()) {
                Log.e("LOG", "LoginActivity: setDeviceTokenCompleteListener: send notifications");
            }
            else {
                Log.e("LOG", "LoginActivity: setDeviceTokenCompleteListener: do not send notifications");
            }
        }
    };

    // ------------------------------ OTHER --------------------------------------------------- //

    private void moveToRegisterFragment() {
        Log.e("LOG", "SignInFragment: moveToRegisterFragment()");

        if(canReactOnClick()) {
            externalActivity.setSecondLayerFragment(CONST.REGISTER_FRAGMENT_CODE,
                    null);
        }
    }

/*    private void moveToFillAccountFragment() {
        Log.e("LOG", "SignInFragment: moveToFillAccountFragment()");

        externalActivity.setFirstLayerFragment(   CONST.FILL_ACCOUNT_FRAGMENT,
                                        true,
                                        true);
    }*/

    private void startSignIn() {
        Log.e("LOG", "SignInFragment: startSignIn()");

        if(canReactOnClick()) {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password)) {

                externalActivity.showSnackBar(  signInContainer,
                                                R.string.error_empty_field,
                                                Snackbar.LENGTH_LONG);
            } else {

                externalActivity.showProgressDialog(getResources().getString(R.string.message_checking_sign_in),
                        getResources().getString(R.string.message_check_account_wait));

                //Log.e("LOG", "SignInFragment: startSignIn(): FBAuth is null: " +(externalActivity.getFBAuth() == null));

                if (externalActivity.getFBAuth() != null) {

                    externalActivity.getFBAuth().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signInCompleteListener);
                }
            }
        }
    }

    private void setDeviceToken() {
        Log.e("LOG", "SignInFragment: setDeviceToken()");

        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        if(!TextUtils.isEmpty(deviceToken)) {

            externalActivity.getCurrentUserFBDatabaseRef().child(CONST.USER_DEVICE_TOKEN)
                    .setValue(deviceToken)
                    .addOnCompleteListener(setDeviceTokenCompleteListener);
        }
        else {
            Log.e("LOG", "SignInFragment: setDeviceToken(): error: deviceToken is empty or null");
        }
    }

    private boolean canReactOnClick() {

        Log.e("LOG", "SignInFragment: canReactOnClick()");

        //Log.e("LOG", "SignInFragment: canReactOnClick(): currentFragment code= " +externalActivity.getCurrentFragmentCode());

        if( (externalActivity.getCurrentFragmentCode() > 0) &&
            (externalActivity.getCurrentFragmentCode() == CONST.SIGN_IN_FRAGMENT_CODE)) {
            //Log.e("LOG", "SignInFragment: canReactOnClick(): can react on click");
            return true;
        }
        else {
            //Log.e("LOG", "SignInFragment: canReactOnClick(): can not react on click");
            return false;
        }
    }
}
