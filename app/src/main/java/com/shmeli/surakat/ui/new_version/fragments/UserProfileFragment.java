package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shmeli.surakat.R;

/**
 * Created by Serghei Ostrovschi on 11/16/17.
 */

public class UserProfileFragment extends ParentFragment {

    private static UserProfileFragment  instance;

    private View            view;

    private String          selectedUserId = "";

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfileFragment.
     */
    public static UserProfileFragment newInstance(String selectedUserId) {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new UserProfileFragment();
        }

        Log.e("LOG", "UserProfileFragment: newInstance(): selectedUserId= " +selectedUserId);

        if(!TextUtils.isEmpty(selectedUserId)) {
            args.putString("selectedUserId", selectedUserId);
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "UserProfileFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_user_profile,
                                container,
                                false);

//        Log.e("LOG", "UserProfileFragment: onCreateView(): getArguments().containsKey(\"selectedUserId\"): " +(getArguments().containsKey("selectedUserId")));

        if(getArguments().containsKey("selectedUserId")) {

            this.selectedUserId = getArguments().getString("selectedUserId");

//            Log.e("LOG", "UserProfileFragment: onCreateView(): selectedUserId= " +selectedUserId);
        }

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

}
