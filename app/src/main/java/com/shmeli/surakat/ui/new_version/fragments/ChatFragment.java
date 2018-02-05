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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.shmeli.surakat.R;
import com.shmeli.surakat.adapters.MessageAdapter;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.Message;
import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Serghei Ostrovschi on 11/22/17.
 */

public class ChatFragment extends FragmentWithInfoInToolbar {

    private static ChatFragment     instance;

    private View                    view;

    private EditText                chatMessageText;
    private TextView                chatMessageLeftCharsBodyText;
    private View                    chatTopDividerView;
    private Button                  chatSendButton;

    private SwipeRefreshLayout      chatSwipeRefreshLayout;
    private RecyclerView            chatMessagesList;

    private InternalActivity        internalActivity;

    private LinearLayoutManager     linearLayoutManager;
    private List<Message>           messagesList    = new ArrayList<>();
    private List<String>            messagesKeyList = new ArrayList<>();
    private MessageAdapter          messageAdapter;

    private String                  senderId                = "";
    private String                  recipientId             = "";
    private String                  recipientName           = "";

    private String                  lastLoadedMessageKey    = "";

    private StringBuilder           newHintSB = new StringBuilder("");

    private int                     currentPageCount        = 1;
    private int                     loadMoreItemPosition    = 0;

    private long                    allMessagesSum          = 0;
    private long                    loadedMessagesSum       = 0;

    private boolean                 isFragmentInitiated;

//    private DatabaseReference       chatFBDatabaseRef;
    private DatabaseReference       messagesDatabaseRef;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
//    public static ChatFragment newInstance(String selectedUserId,
//                                           String selectedUserName) {
    public static ChatFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new ChatFragment();
        }

//        if(!TextUtils.isEmpty(selectedUserId)) {
//            args.putString( CONST.USER_ID,
//                            selectedUserId);
//        }
//
//        if(!TextUtils.isEmpty(selectedUserName)) {
//            args.putString( CONST.USER_NAME,
//                            selectedUserName);
//        }

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

        internalActivity    = (InternalActivity) getActivity();

//        if(getArguments().containsKey(CONST.USER_ID)) {

//            this.recipientId            = getArguments().getString(CONST.USER_ID);
//
//            if( getArguments().containsKey(CONST.USER_NAME) &&
//                (!TextUtils.isEmpty(getArguments().getString(CONST.USER_NAME)))) {
//
//                this.recipientName          = getArguments().getString(CONST.USER_NAME);
//            }

            Log.e("LOG", "ChatFragment: onCreateView(): recipientId: "      +recipientId);
            Log.e("LOG", "ChatFragment: onCreateView(): recipientName: "    +recipientName);

            chatMessageText             = UiUtils.findView( view,
                                                            R.id.chatMessageText);
            chatMessageText.addTextChangedListener(onTextChangedListener);

            chatMessageLeftCharsBodyText = UiUtils.findView(view,
                                                            R.id.chatMessageLeftCharsBody);
            chatMessageLeftCharsBodyText.setText(String.valueOf(CONST.PUBLICATION_MAX_LENGTH));

            chatTopDividerView          = UiUtils.findView( view,
                                                            R.id.chatTopDividerView);

            chatSendButton              = UiUtils.findView( view,
                                                            R.id.chatSendButton);
            chatSendButton.setOnClickListener(sendClickListener);

            chatSwipeRefreshLayout      = UiUtils.findView( view,
                                                            R.id.chatSwipeRefreshLayout);
            chatSwipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

            messageAdapter              = new MessageAdapter(   getActivity(),
                                                                messagesList,
                                                                recipientName);

            linearLayoutManager         = new LinearLayoutManager(getActivity());

            chatMessagesList            = UiUtils.findView( view,
                                                            R.id.chatMessagesList);
            chatMessagesList.setHasFixedSize(true);
            chatMessagesList.setLayoutManager(linearLayoutManager);
            chatMessagesList.setAdapter(messageAdapter);

            if(internalActivity.getCurrentFragment() instanceof FragmentWithInfoInToolbar) {
                internalActivity.setToolbarInfo(getFragmentInfoHeadResId(),
                                                getFragmentInfoBodyText());
            }

//            setRecipientNameAtHint();

//            if(!isFragmentInitiated) {

                init();

//                isFragmentInitiated = true;
//            }
//        }
//        else {
//            Log.e("LOG", "ChatFragment: onCreateView(): error: parameter selectedUserId does not exist in Bundle");
//        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.e("LOG", "ChatFragment: onStop()");

        // hide info container
        internalActivity.setToolbarInfo(0, 0);
    }

    // ----------------------------------- INIT ----------------------------------------- //

    private void init() {
        Log.e("LOG", "ChatFragment: init()");

        setRecipientNameAtHint();

        senderId = internalActivity.getCurrentUserId();

        if( (!TextUtils.isEmpty(senderId)) &&
            (!TextUtils.isEmpty(recipientId))) {

//            chatFBDatabaseRef = internalActivity.getRootFBDatabaseRef().child(CONST.FIREBASE_CHAT_CHILD)
//                                                                        .child(senderId);
//            chatFBDatabaseRef.addValueEventListener(chatDataListener);

            loadMessages();
        }
    }

    // ------------------------------ SETTERS ----------------------------------------------- //

    public void setRecipientData(String recipientId,
                                 String recipientName) {
        //Log.e("LOG", "ChatFragment: setRecipientData()");

        if(!TextUtils.isEmpty(recipientId)) {
            this.recipientId = recipientId;
        }

        if(!TextUtils.isEmpty(recipientName)) {

            // prepare for userName
            newHintSB = new StringBuilder("");

            this.recipientName = recipientName;
        }

        //Log.e("LOG", "ChatFragment: setRecipientData(): new recipientId: " +this.recipientId);
        //Log.e("LOG", "ChatFragment: setRecipientData(): new recipientName: " +this.recipientName);
    }

    // ------------------------------ OTHER ----------------------------------------------- //

    private void startSendMessage() {

        String messageText = chatMessageText.getText().toString();

        if (!TextUtils.isEmpty(messageText)) {

            DatabaseReference userMessagePush = internalActivity.getRootFBDatabaseRef().child(CONST.FIREBASE_MESSAGES_CHILD)
                                                                                        .child(senderId)
                                                                                        .child(recipientId).push();
            String pushedMessageId = userMessagePush.getKey();

            if (!TextUtils.isEmpty(pushedMessageId)) {

                Map messageMap = new HashMap();
                messageMap.put(CONST.MESSAGE_TEXT, messageText);
                messageMap.put(CONST.MESSAGE_IS_SEEN, false);
                messageMap.put(CONST.MESSAGE_TYPE, CONST.MESSAGE_TEXT_TYPE);
                messageMap.put(CONST.MESSAGE_CREATE_TIME, ServerValue.TIMESTAMP);
                messageMap.put(CONST.MESSAGE_AUTHOR_ID, senderId);

                StringBuilder mapKeySB = new StringBuilder();
                Map messageUserMap = new HashMap();

                mapKeySB.append(CONST.MESSAGES_KEY);
                mapKeySB.append(senderId);
                mapKeySB.append("/");
                mapKeySB.append(recipientId);
                mapKeySB.append("/");
                messageUserMap.put(mapKeySB + pushedMessageId, messageMap);

                mapKeySB.setLength(0);

                mapKeySB.append(CONST.MESSAGES_KEY);
                mapKeySB.append(recipientId);
                mapKeySB.append("/");
                mapKeySB.append(senderId);
                mapKeySB.append("/");
                messageUserMap.put(mapKeySB + pushedMessageId, messageMap);

                chatMessageText.setText("");

                internalActivity.getRootFBDatabaseRef().updateChildren( messageUserMap,
                                                                        new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError != null) {

                            Log.e("LOG", "ChatFragment: startSendMessage(): rootFBDatabaseRef.updateChildren.onComplete error: " + databaseError.getMessage().toString());
                        }
                    }
                });
            }
        }
    }

    private void addMessageInArray( Message message,
                                    int     position,
                                    String  messageKey) {

        if( (message != null) &&
            (position >= 0)) {

            messagesList.add(   position,
                                message);

            messagesKeyList.add(messageKey);
        }
    }

    private long getAllMessagesCount() {
        //Log.e("LOG", "ChatFragment: getAllMessagesCount()");

        messagesDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null) {
                    allMessagesSum = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return allMessagesSum;
    }

    private void setRecipientNameAtHint() {
        //Log.e("LOG", "ChatFragment: setRecipientNameAtHint()");

        //Log.e("LOG", "ChatFragment: setRecipientNameAtHint(): recipientName: " +recipientName);

        if(!TextUtils.isEmpty(recipientName) &&
            newHintSB.length() == 0) {

            String currentHint = chatMessageText.getHint().toString();

            int threeDotsPosition = currentHint.indexOf("...");

            //newHintSB = new StringBuilder("");
            newHintSB.append(currentHint.substring(0, threeDotsPosition));
            newHintSB.append(" to ");
            newHintSB.append(recipientName);
            newHintSB.append(currentHint.substring(threeDotsPosition, currentHint.length()));
        }

        //Log.e("LOG", "ChatFragment: setRecipientNameAtHint(): newHintSB: " +newHintSB.toString());

        chatMessageText.setHint(newHintSB.toString());
    }

    // ------------------------------ LOAD MESSAGES --------------------------------------- //

    private void loadMessages() {
        Log.e("LOG", "ChatFragment: loadMessages()");

        Log.e("LOG", "ChatFragment: loadMessages(): senderId: "     +senderId);
        Log.e("LOG", "ChatFragment: loadMessages(): recipientId: "  +recipientId);

        messagesDatabaseRef = internalActivity.getRootFBDatabaseRef().child(CONST.FIREBASE_MESSAGES_CHILD)
                                                                        .child(senderId)
                                                                        .child(recipientId);
        if(messagesDatabaseRef != null) {
            messagesList.clear();

            Query loadMessageQuery = messagesDatabaseRef.limitToLast(currentPageCount * CONST.LOAD_MESSAGES_COUNT);
            loadMessageQuery.addChildEventListener(loadMessagesEventListener);
        }
        else
            Log.e("LOG", "ChatFragment: loadMessages(): error: messagesDatabaseRef is null");
    }

    private void loadMoreMessages() {
        Log.e("LOG", "ChatFragment: loadMoreMessages()");

        if(messagesDatabaseRef != null) {

            Query loadMoreMessageQuery = messagesDatabaseRef.orderByKey().endAt(lastLoadedMessageKey).limitToLast(CONST.LOAD_MESSAGES_COUNT);
            loadMoreMessageQuery.addChildEventListener(loadMoreMessagesEventListener);
        }
        else
            Log.e("LOG", "ChatFragment: loadMessages(): messagesDatabaseRef is null");
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

            if(chatMessageText.length() <= 0) {
                setRecipientNameAtHint();
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSendMessage();
        }
    };

    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            loadMoreItemPosition = 0;

            if(getAllMessagesCount() > loadedMessagesSum) {

                currentPageCount++;

                loadMoreMessages();
            }
            else {

                chatSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

//    ValueEventListener chatDataListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//            //Log.e("LOG", "ChatFragment: chatDataListener.onDataChange(): dataSnapshot doesn't have child(" +recipientId+ "): " +(!dataSnapshot.hasChild(recipientId)));
//
//            if(!dataSnapshot.hasChild(recipientId)) {
//
//                Log.e("LOG", "ChatFragment: chatDataListener.onDataChange(): chat init");
//
//                Map chatAddMap = new HashMap();
//                chatAddMap.put(CONST.CHAT_SEEN,        false);
//                chatAddMap.put(CONST.CHAT_TIMESTAMP,   ServerValue.TIMESTAMP);
//
//                // ------------------------------------------------------------------- //
//
//                Map chatUserMap         = new HashMap();
//                StringBuilder chatKeySB = new StringBuilder();
//
//                chatKeySB.append(CONST.CHAT_KEY);
//                chatKeySB.append(senderId);
//                chatKeySB.append("/");
//                chatKeySB.append(recipientId);
//                chatUserMap.put(chatKeySB.toString(), chatAddMap);
//
//                chatKeySB.setLength(0);
//
//                chatKeySB.append(CONST.CHAT_KEY);
//                chatKeySB.append(recipientId);
//                chatKeySB.append("/");
//                chatKeySB.append(senderId);
//                chatUserMap.put(chatKeySB.toString(), chatAddMap);
//
//                internalActivity.getRootFBDatabaseRef().updateChildren( chatUserMap,
//                                                                        new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                        if(databaseError != null) {
//
//                            Log.e("LOG", "ChatFragment: chatDataListener.onDataChange(): rootFBDatabaseRef.updateChildren.onComplete error: " +databaseError.getMessage().toString());
//                        }
//                    }
//                });
//            }
//            else {
//                Log.e("LOG", "ChatFragment: chatDataListener.onDataChange(): chat second enter");
//            }
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) { }
//    };

    ChildEventListener loadMessagesEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot   dataSnapshot,
                                 String         s) {

            Message message = dataSnapshot.getValue(Message.class);

            loadMoreItemPosition++;

            // show divider only if it is not visible
            if(chatTopDividerView.getVisibility() == View.INVISIBLE) {
                chatTopDividerView.setVisibility(View.VISIBLE);
            }

            // increase count of loaded messages only if it is not bigger than sum of all messages
            if(loadedMessagesSum < getAllMessagesCount()) {
                loadedMessagesSum++;
            }

            // if this is a message which key will be as start load new portion of messages, then save it is key
            if(loadMoreItemPosition == 1) {
                lastLoadedMessageKey = dataSnapshot.getKey();
            }

            addMessageInArray(  message,
                                messagesList.size(),
                                dataSnapshot.getKey());

            messageAdapter.notifyDataSetChanged();

            chatMessagesList.scrollToPosition(messagesList.size() - 1);

            chatSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    ChildEventListener loadMoreMessagesEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot   dataSnapshot,
                                 String         s) {

            Message message = dataSnapshot.getValue(Message.class);

            String messageKey = dataSnapshot.getKey();

            if( (!TextUtils.isEmpty(messageKey)) &&
                (!messagesKeyList.contains(messageKey))) {

                addMessageInArray(  message,
                                    loadMoreItemPosition++,
                                    messageKey);
            }

            if(loadedMessagesSum < getAllMessagesCount())
                loadedMessagesSum++;

            if(loadMoreItemPosition == 1) {
                lastLoadedMessageKey = dataSnapshot.getKey();
            }

            messageAdapter.notifyDataSetChanged();

            chatSwipeRefreshLayout.setRefreshing(false);

            linearLayoutManager.scrollToPositionWithOffset( CONST.LOAD_MESSAGES_COUNT,
                                                            0);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
