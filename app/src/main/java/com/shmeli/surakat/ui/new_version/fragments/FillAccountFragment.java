package com.shmeli.surakat.ui.new_version.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.shmeli.surakat.R;

import com.shmeli.surakat.ui.new_version.ExternalActivity;
import com.shmeli.surakat.utils.UiUtils;


/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class FillAccountFragment extends ParentFragment {

    private View                view;

    private RelativeLayout      fillAccountContainer;

    private EditText            nameEditText;

    private Button              setButton;

    private ExternalActivity    externalActivity;

    private String name = "";

    private static FillAccountFragment  instance;

    public FillAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FillAccountFragment.
     */
    public static FillAccountFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new FillAccountFragment();
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fill_account,
                                container,
                                false);

        externalActivity    = (ExternalActivity) getActivity();

        fillAccountContainer = UiUtils.findView(view,
                                                R.id.fillAccountContainer);

        nameEditText        = UiUtils.findView( view,
                                                R.id.fillAccountFragmentNameEditText);

        setButton           = UiUtils.findView( view,
                                                R.id.fillAccountFragmentSetButton);
        setButton.setOnClickListener(setClickListener);

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

    View.OnClickListener setClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startFillingAccount();
        }
    };

    // ------------------------------ OTHER --------------------------------------------------- //

    private void startFillingAccount() {
        Log.e("LOG", "FillAccountFragment: startFillingAccount()");

        name = nameEditText.getText().toString();

        if(TextUtils.isEmpty(name)) {

            Log.e("LOG", "FillAccountFragment: startFillingAccount(): filling account...");

            externalActivity.showSnackBar(  fillAccountContainer,
                                            R.string.error_empty_field,
                                            Snackbar.LENGTH_LONG);
        }
        else {

/*            externalActivity.showProgressDialog(getResources().getString(R.string.message_setting_account),
                                                getResources().getString(R.string.message_setting_account_wait));

            HashMap<String, String> userMap = new HashMap<>();
            userMap.put(CONST.USER_IMAGE, CONST.DEFAULT_VALUE);
            userMap.put(CONST.USER_NAME, currentUserName);
            userMap.put(CONST.USER_DEVICE_TOKEN, currentUserDeviceToken);
            userMap.put(CONST.USER_STATUS, CONST.USER_ONLINE_STATUS);
            userMap.put(CONST.USER_THUMB_IMAGE, CONST.DEFAULT_VALUE);

            currentUserFBDatabaseRef.setValue(userMap).addOnCompleteListener(createUserInDBCompleteListener);


            Log.e("LOG", "FillAccountFragment: startFillingAccount(): FBAuth is null: " +(externalActivity.getFBAuth() == null));

            if(externalActivity.getFBAuth() != null) {

                externalActivity.getFBAuth().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(createAccountCompleteListener);
            }*/

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
