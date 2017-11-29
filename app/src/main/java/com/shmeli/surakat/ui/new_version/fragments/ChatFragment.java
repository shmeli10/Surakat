package com.shmeli.surakat.ui.new_version.fragments;


import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/22/17.
 */

public class ChatFragment extends ParentFragment {

    private static ChatFragment     instance;

    private View                    view;

    private LinearLayout            chatContainer;
    private EditText                chatMessageText;
    private TextView                chatMessageLeftCharsBodyText;
    private View                    chatTopDividerView;
    private Button                  chatSendButton;

    private SwipeRefreshLayout      chatSwipeRefreshLayout;
    private RecyclerView            chatMessagesList;

    private InternalActivity        internalActivity;

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

            this.selectedUserId = getArguments().getString(CONST.USER_ID);

            chatContainer       = UiUtils.findView( view,
                                                    R.id.chatContainer);

            chatMessageText     = UiUtils.findView( view,
                                                    R.id.chatMessageText);
            chatMessageText.addTextChangedListener(onTextChangedListener);

            chatMessageLeftCharsBodyText = UiUtils.findView(view,
                                                            R.id.chatMessageLeftCharsBody);
            chatMessageLeftCharsBodyText.setText(String.valueOf(CONST.PUBLICATION_MAX_LENGTH));

            chatTopDividerView  = UiUtils.findView( view,
                                                    R.id.chatTopDividerView);

            chatSendButton      = UiUtils.findView( view,
                                                    R.id.chatSendButton);
            chatSendButton.setOnClickListener(sendClickListener);

            chatSwipeRefreshLayout  = UiUtils.findView( view,
                                                        R.id.chatSwipeRefreshLayout);
            chatSwipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

            chatMessagesList    = UiUtils.findView( view,
                                                    R.id.chatMessagesList);
            chatMessagesList.setHasFixedSize(true);
            chatMessagesList.setLayoutManager(new LinearLayoutManager(getActivity()));

            init();
        }
        else {
            Log.e("LOG", "ChatFragment: onCreateView(): parameter selectedUserId does not exist in Bundle");
        }

        // Inflate the layout for this fragment
        return view;
    }

    // ----------------------------------- INIT ----------------------------------------- //

    private void init() {
        Log.e("LOG", "ChatFragment: init()");

        internalActivity = (InternalActivity) getActivity();

    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence  s,
                                      int           start,
                                      int           count,
                                      int           after) { }

        @Override
        public void onTextChanged(CharSequence  s,
                                  int           start,
                                  int           before,
                                  int           count) {

            // set available amount of characters
            chatMessageLeftCharsBodyText.setText(String.valueOf(CONST.PUBLICATION_MAX_LENGTH - chatMessageText.length()));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e("LOG", "ChatFragment: send button clicked");

//            startSendMessage();
        }
    };

    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            Log.e("LOG", "ChatFragment: swipe refreshed");

            /*currentPageCount++;

            loadMoreItemPosition = 0;

//            messagesList.clear();

            loadMoreMessages();*/
        }
    };

}
