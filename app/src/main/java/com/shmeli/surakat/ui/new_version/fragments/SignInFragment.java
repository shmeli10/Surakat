package com.shmeli.surakat.ui.new_version.fragments;

import android.app.ProgressDialog;
import android.content.Context;

import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.shmeli.surakat.R;
import com.shmeli.surakat.utils.UiUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    private View            view;

    private RelativeLayout  signInContainer;

    private EditText        emailEditText;
    private EditText        passwordEditText;

    private Button          singInButton;
    private Button          registerButton;

    private ProgressDialog  progressDialog;

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

        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
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

        signInContainer     = UiUtils.findView(view, R.id.signInContainer);
        emailEditText       = UiUtils.findView(view, R.id.emailEditText);
        passwordEditText    = UiUtils.findView(view, R.id.passwordEditText);

        singInButton        = UiUtils.findView(view, R.id.signInButton);
        singInButton.setOnClickListener(signInClickListener);

        registerButton      = UiUtils.findView(view, R.id.registerButton);
        registerButton.setOnClickListener(registerClickListener);

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    // ------------------------------ OTHER --------------------------------------------------- //

    private void moveToRegisterFragment() {
        Log.e("LOG", "SignInFragment: moveToRegisterFragment()");
    }

    private void startSignIn() {
        Log.e("LOG", "SignInFragment: startSignIn()");

//        String email    = emailEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//
//        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
//
//            Snackbar.make(  signInContainer,
//                    R.string.error_empty_field,
//                    Snackbar.LENGTH_LONG).show();
//        }
//        else {
//
//            progressDialog.setTitle(getResources().getString(R.string.message_checking_sign_in));
//            progressDialog.setMessage(getResources().getString(R.string.message_check_account_wait));
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
//
//            // -------------------------------------------------------------------------------- //
//
//            // Set title divider color
//            int titleDividerId = getResources().getIdentifier(  "titleDivider",
//                                                                "id",
//                                                                "android");
//            View titleDivider = progressDialog.findViewById(titleDividerId);
//
//            if (titleDivider != null)
//                titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//
//            // -------------------------------------------------------------------------------- //
//
//            fbAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(signInCompleteListener);
//        }
    }
}
