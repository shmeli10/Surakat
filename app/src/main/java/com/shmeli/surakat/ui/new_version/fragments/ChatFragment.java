package com.shmeli.surakat.ui.new_version.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;

/**
 * Created by Serghei Ostrovschi on 11/22/17.
 */

public class ChatFragment extends ParentFragment {

    private static ChatFragment     instance;

    private View                    view;

    private RelativeLayout          chatContainer;

    private String                  selectedUserId = "";

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(String selectedUserId) {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new ChatFragment();
        }

        Log.e("LOG", "ChatFragment: newInstance(): selectedUserId= " +selectedUserId);

        if(!TextUtils.isEmpty(selectedUserId)) {
            args.putString( CONST.USER_ID,
                            selectedUserId);
        }

        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "ChatFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_chat,
                                container,
                                false);

        if(getArguments().containsKey(CONST.USER_ID)) {

        }
        else {
            Log.e("LOG", "ChatFragment: onCreateView(): parameter selectedUserId does not exist in Bundle");
        }

        // Inflate the layout for this fragment
        return view;
    }

}
