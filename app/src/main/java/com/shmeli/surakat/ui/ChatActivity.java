package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;

import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.adapters.MessageAdapter;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.Message;
import com.shmeli.surakat.utils.GetTimeAgo;
import com.shmeli.surakat.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class ChatActivity extends AppCompatActivity {

    private ActionBar               actionBar;

    private Toolbar                 chatPageToolbar;
    private TextView                chatAppToolbarUserName;
    private TextView                chatAppToolbarLastSeen;


    private CircleImageView         chatAppToolbarAvatar;
    private EditText                messageEditText;
    private TextView                messageLeftCharsBodyTextView;
    private View                    chatTopDividerView;

    private Button                  sendButton;
    private RecyclerView            messagesRecyclerView;

    private SwipeRefreshLayout      chatSwipeRefreshLayout;

    private RelativeLayout          chatContainer;

    //private Firebase                fbRef;
    private FirebaseAuth            fbAuth;
    //private FirebaseUser            fbCurrentUser;

//    private FirebaseRecyclerAdapter<Message, MessageViewHolder> fbAdapter;

    private DatabaseReference       chatFBDatabaseRef;
    private DatabaseReference       messagesDatabaseRef;
    private DatabaseReference       recipientFBDatabaseRef;
    private DatabaseReference       rootFBDatabaseRef;
    private DatabaseReference       senderFBDatabaseRef;
    private DatabaseReference       usersFBDatabaseRef;

    private Query                   messagesQuery;

//    private ArrayList<String>       messageList = new ArrayList<>();

    private Intent                  intent;

    private ProgressDialog          progressDialog;

    private List<Message>           messagesList = new ArrayList<>();
    private LinearLayoutManager     linearLayoutManager;
    private MessageAdapter          messageAdapter;

    private boolean chatTopDividerIsVisible;
    private boolean canIncreaseMessagesSum = true;

    //private String  selectedRecipientKey = "";

    private String senderId             = "";

    private String recipientId          = "";
    private String recipientLastSeen    = "";
    private String recipientName        = "";
    private String recipientThumbImage  = "";

    private String lastLoadedMessageKey = "";

    private boolean recipientIsOnline   = false;

    private int chatMessagesSum = 0;

    private int currentPageCount = 1;

    private int loadMoreItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setChatToolbar();

        init();

//        chatPageToolbar     = UiUtils.findView(this, R.id.chatPageToolbar);
//        setSupportActionBar(chatPageToolbar);
//        getSupportActionBar().setTitle(R.string.text_chat);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fbRef                           = new Firebase(CONST.FIREBASE_MESSAGES_LINK);
        //fbAuth                          = FirebaseAuth.getInstance();
        //fbCurrentUser                   = fbAuth.getCurrentUser();

        progressDialog                  = new ProgressDialog(this);

//        messagesDatabaseRef             = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_MESSAGES_CHILD);
//        usersFBDatabaseRef              = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(fbCurrentUser.getUid());

        //messagesQuery                   = messagesDatabaseRef.orderByChild("messageAuthorKey").equalTo(fbAuth.getCurrentUser().getUid());
        //messagesQuery                   = messagesDatabaseRef.orderByChild("messageDateAndTime").orderByValue();

        messageEditText                 = UiUtils.findView(this, R.id.messageEditText);
        messageEditText.addTextChangedListener(onTextChangedListener);

        messageLeftCharsBodyTextView    = UiUtils.findView(this, R.id.messageLeftCharsBodyTextView);
        messageLeftCharsBodyTextView.setText("" +CONST.PUBLICATION_MAX_LENGTH);

        chatTopDividerView              = UiUtils.findView(this, R.id.chatTopDividerView);

        sendButton                      = UiUtils.findView(this, R.id.sendButton);
        sendButton.setOnClickListener(sendClickListener);

        messageAdapter                  = null; //new MessageAdapter(   getApplicationContext(),messagesList);

        messagesRecyclerView            = UiUtils.findView(this, R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        //messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        linearLayoutManager             = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);

        chatSwipeRefreshLayout          = UiUtils.findView(this, R.id.chatSwipeRefreshLayout);
        chatSwipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        chatContainer                   = UiUtils.findView(this, R.id.chatContainer);

//        fbRef.addChildEventListener(childEventListener);

//        Intent intent = getIntent();
//
//        if(intent != null) {
//            selectedRecipientKey = intent.getStringExtra("userKey");
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUser();

        //init();

//        Intent intent = getIntent();
//
//        if(intent != null) {
//            //selectedRecipientKey = intent.getStringExtra("userKey");
//
//            selectedRecipientId = intent.getStringExtra(CONST.USER_ID);
//
//            //Log.e("LOG", "ChatActivity: onStart(): selectedRecipientKey= " +selectedRecipientKey);
//            Log.e("LOG", "ChatActivity: onStart(): selectedRecipientId= " +selectedRecipientId);
//
//            //chatMessagesSum = 0;
//
//            showChatMessages();

//            if (!TextUtils.isEmpty(selectedRecipientKey)) {



//                fbAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
//                        R.layout.message_row,
//                        MessageViewHolder.class,
//                        messagesDatabaseRef) {
//                    @Override
//                    protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
//
//                        String messageAuthorKey = model.getMessageAuthorKey();
//                        String messageRecipientKey = model.getMessageRecipientKey();
//
//                        Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): currentUserKey= " + currentUserKey);
//                        Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): selectedRecipientKey= " + selectedRecipientKey);
//                        Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): messageAuthorKey= " + messageAuthorKey);
//                        Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): messageRecipientKey= " + messageRecipientKey);
//
//                        // show message
//                        if ((messageAuthorKey.equals(currentUserKey) && messageRecipientKey.equals(selectedRecipientKey)) ||
//                                (messageAuthorKey.equals(selectedRecipientKey) && messageRecipientKey.equals(currentUserKey))) {
//
//                            Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): SHOW MESSAGE");
//
//                            if (!chatTopDividerIsVisible) {
//
//                                //Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): SHOW DIVIDER");
//
//                                chatTopDividerView.setVisibility(View.VISIBLE);
//                                chatTopDividerIsVisible = true;
//                            }
//
//                            viewHolder.setMessageAuthorName(model.getMessageAuthorName());
//                            viewHolder.setMessageText(model.getMessageText());
//                        }
//                        // hide message
//                        else {
//                            Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): HIDE MESSAGE");
//
//                            viewHolder.hideItem();
//                        }
//                    }
//                };

//                messagesRecyclerView.setAdapter(fbAdapter);
//            } else {
//
//                Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): RETURN BACK_1");
//            }
//        }
//        else {
//
//            Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): RETURN BACK_2");
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("LOG", "ChatActivity: onStop()");

        FirebaseUser currentUser = fbAuth.getCurrentUser();

        if(currentUser != null) {

            if(senderFBDatabaseRef != null) {

                senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(false);
                senderFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
            }
            else {
                goToSetAccountActivity();
            }
        }

//        Log.e("LOG", "ChatActivity: onStop()");
//
//        Log.e("LOG", "ChatActivity: onStop(): fbAuth.getCurrentUser() is null: " + (fbAuth.getCurrentUser() == null));
//
//        //if(!TextUtils.isEmpty(currentUserId)) {
//        if(fbAuth.getCurrentUser() != null) {
//        //if(senderFBDatabaseRef != null) {
//            senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(false);
//            senderFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
//        }
    }

    // ----------------------- OTHER ------------------------------------ //

    private void setChatToolbar() {

        chatPageToolbar     = UiUtils.findView(this, R.id.chatPageToolbar);
        setSupportActionBar(chatPageToolbar);

        //getSupportActionBar().setTitle(R.string.text_chat);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(  R.layout.chat_app_bar,
                                                null);

        chatAppToolbarUserName  = UiUtils.findView(actionBarView, R.id.chatAppToolbarUserName);
        chatAppToolbarUserName.setText(R.string.text_chat);

        chatAppToolbarLastSeen  = UiUtils.findView(actionBarView, R.id.chatAppToolbarLastSeen);

        chatAppToolbarAvatar    = UiUtils.findView(actionBarView, R.id.chatAppToolbarAvatar);

        actionBar.setCustomView(actionBarView);
        //actionBar.setTitle(R.string.text_chat);

    }

    private void showToolbarLastSeen() {

//        if(!TextUtils.isEmpty(recipientName)) {
//            chatAppToolbarUserName.setText(recipientName);
//        }
//        else {
//            chatAppToolbarUserName.setText(R.string.text_chat);
//        }

        chatAppToolbarLastSeen.setText(recipientLastSeen);

        //chatAppToolbarLastSeen.setVisibility(View.VISIBLE);
    }

    Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

            Picasso.with(ChatActivity.this)
                    .load(recipientThumbImage)
                    .placeholder(R.drawable.default_avatar)
                    .into(chatAppToolbarAvatar);
        }
    };

//    private void showChatMessages() {

        /*final String currentUserKey = fbAuth.getCurrentUser().getUid();

        //if (!TextUtils.isEmpty(selectedRecipientKey)) {
        if (!TextUtils.isEmpty(selectedRecipientId)) {
            fbAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                                                                                R.layout.message_row,
                                                                                MessageViewHolder.class,
                                                                                messagesDatabaseRef) {
                                                                                //messagesQuery) {

                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder,
                                                  Message           model,
                                                  int               position) {

                    String messageAuthorKey     = model.getMessageAuthorKey();
                    String messageRecipientKey  = model.getMessageRecipientKey();

//                    Log.e("LOG", "ChatActivity: showChatMessages(): currentUserKey= " + currentUserKey);
//                    Log.e("LOG", "ChatActivity: showChatMessages(): selectedRecipientKey= " + selectedRecipientKey);
//                    Log.e("LOG", "ChatActivity: showChatMessages(): messageAuthorKey= " + messageAuthorKey);
//                    Log.e("LOG", "ChatActivity: showChatMessages(): messageRecipientKey= " + messageRecipientKey);

                    // show message
                    if ( (!TextUtils.isEmpty(messageAuthorKey) && !TextUtils.isEmpty(messageRecipientKey)) &&
                         ( (messageAuthorKey.equals(currentUserKey) && messageRecipientKey.equals(selectedRecipientId)) ||
                           (messageAuthorKey.equals(selectedRecipientId) && messageRecipientKey.equals(currentUserKey)) ) ) {

                        if(canIncreaseMessagesSum)
                            chatMessagesSum++;

                        Log.e("LOG", "ChatActivity: showChatMessages(): SHOW MESSAGE: " +model.getMessageText());
                        Log.e("LOG", "ChatActivity: showChatMessages(): chatMessagesSum= " +chatMessagesSum);

                        if (!chatTopDividerIsVisible) {

                            //Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): SHOW DIVIDER");

                            chatTopDividerView.setVisibility(View.VISIBLE);
                            chatTopDividerIsVisible = true;
                        }

                        viewHolder.setMessageAuthorName(model.getMessageAuthorName());
                        viewHolder.setMessageText(model.getMessageText());
                    }
                    // hide message
                    else {
                        Log.e("LOG", "ChatActivity: showChatMessages(): HIDE MESSAGE: " +model.getMessageText());

                        viewHolder.hideItem();
                    }
                }
            };

            messagesRecyclerView.setAdapter(fbAdapter);

            if(canIncreaseMessagesSum)
                canIncreaseMessagesSum = false;

            Log.e("LOG", "ChatActivity: showChatMessages(): chatMessagesSum= " +chatMessagesSum);

            //Log.e("LOG", "ChatActivity: showChatMessages(): chatMessagesSum= " +messagesDatabaseRef.);

            messagesRecyclerView.smoothScrollToPosition(chatMessagesSum);

            //messagesRecyclerView.scrollToPosition(fbAdapter.getItemCount()-1);
            //messagesRecyclerView.scrollToPosition(fbAdapter.getItemCount());
        }
        else {

            Log.e("LOG", "ChatActivity: showChatMessages(): RETURN BACK");
        }*/
//    }

/*    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View            view;

        TextView        messageAuthorNameTextView;
        TextView        messageTextView;

        LinearLayout    messageContainer;

        CardView        messageCardView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            messageContainer            = UiUtils.findView(view, R.id.messageContainer);

            messageAuthorNameTextView   = UiUtils.findView(view, R.id.messageAuthorNameTextView);
            messageTextView             = UiUtils.findView(view, R.id.messageTextView);

            messageCardView             = UiUtils.findView(view, R.id.messageCardView);
            messageCardView.setBackgroundResource(R.drawable.input_outline);
        }

        public void setMessageAuthorName(String messageAuthorName) {
            messageAuthorNameTextView.setText(messageAuthorName);
        }

        public void setMessageText(String messageText) {
            messageTextView.setText(messageText);
        }

        public void hideItem() {
            view.setVisibility(View.GONE);
            messageAuthorNameTextView.setVisibility(View.GONE);
            messageTextView.setVisibility(View.GONE);
            messageContainer.setVisibility(View.GONE);
            messageCardView.setVisibility(View.GONE);
        }
    }*/

    private void startSendMessage() {

        String messageText = messageEditText.getText().toString();

        if(!TextUtils.isEmpty(messageText)) {

            DatabaseReference userMessagePush = rootFBDatabaseRef.child(CONST.FIREBASE_MESSAGES_CHILD)
                    .child(senderId)
                    .child(recipientId).push();

            String pushedMessageId = userMessagePush.getKey();

            if(!TextUtils.isEmpty(pushedMessageId)) {

                Map messageMap = new HashMap();
                messageMap.put(CONST.MESSAGE_TEXT,          messageText);
                messageMap.put(CONST.MESSAGE_IS_SEEN,       false);
                messageMap.put(CONST.MESSAGE_TYPE,          CONST.MESSAGE_TEXT_TYPE);
                messageMap.put(CONST.MESSAGE_CREATE_TIME,   ServerValue.TIMESTAMP);
                messageMap.put(CONST.MESSAGE_AUTHOR_ID,     senderId);

                StringBuilder mapKeySB  = new StringBuilder();
                Map messageUserMap      = new HashMap();

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

                messageEditText.setText("");

                rootFBDatabaseRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError != null) {

                            Log.e("LOG", "ChatActivity: startSendMessage(): rootFBDatabaseRef.updateChildren.onComplete error: " +databaseError.getMessage().toString());
                        }
                    }
                });
            }
        }

        /*final String messageText = messageEditText.getText().toString();

        if(!TextUtils.isEmpty(messageText.trim())) {

            progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
            progressDialog.show();

            //String recipientKey = getSelectedRecipientKey();
            String recipientKey = getSelectedRecipientId();

            Log.e("LOG", "ChatActivity: startSendMessage(): selectedRecipientKey= " +recipientKey);

            DatabaseReference newMessage = messagesDatabaseRef.push();
            newMessage.child("messageId").setValue("" + 1);
            newMessage.child("messageAuthorKey").setValue(fbAuth.getCurrentUser().getUid());
            newMessage.child("messageAuthorName").setValue("user"); // dataSnapshot.child("userName").getValue());
            newMessage.child("messageRecipientKey").setValue(recipientKey);
            newMessage.child("messageDateAndTime").setValue("" + new Date().getTime());
            newMessage.child("messageText").setValue(messageText);
            newMessage.child("isMessageUnread").setValue("" + true);

            final String newMessageKey = newMessage.getKey();

            usersFBDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                    Log.e("LOG", "ChatActivity: startSendMessage(): usersFBDatabaseRef: onDataChange(): update newMessageKey= " +newMessageKey);

                    messagesDatabaseRef.child(newMessageKey).child("messageAuthorName").setValue(dataSnapshot.child(CONST.USER_NAME).getValue());
                    //messagesDatabaseRef.child(newMessageKey).child("messageAuthorName").setValue(dataSnapshot.child("userName").getValue());

//                    String recipientKey = getSelectedRecipientKey();
//
//                    Log.e("LOG", "ChatActivity: startSendMessage(): selectedRecipientKey= " +recipientKey);
//
//                    if(!TextUtils.isEmpty(recipientKey)) {
//
//                        DatabaseReference newMessage = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_MESSAGES_CHILD);
//
//                        newMessage.child("messageId").setValue("" + 1);
//                        newMessage.child("messageAuthorKey").setValue(fbAuth.getCurrentUser().getUid());
//                        newMessage.child("messageAuthorName").setValue(dataSnapshot.child("userName").getValue());
//                        newMessage.child("messageRecipientKey").setValue(recipientKey);
//                        newMessage.child("messageDateAndTime").setValue("" + new Date().getTime());
//                        newMessage.child("messageText").setValue(messageText);
//                        newMessage.child("isMessageUnread").setValue("" + true);
//
//                        newMessage.push();
//                    }
//                    else {
//                        Snackbar.make(  chatContainer,
//                                R.string.error_send_message,
//                                Snackbar.LENGTH_LONG).show();
//                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            messageEditText.setText("");

            progressDialog.dismiss();

            fbAdapter.notifyDataSetChanged();

            // //.scrollToPosition(fbAdapter.getItemCount() - 1);

            chatMessagesSum++;

            Log.e("LOG", "ChatActivity: startSendMessage(): chatMessagesSum= " +chatMessagesSum);

            messagesRecyclerView.smoothScrollToPosition(chatMessagesSum);

            //showChatMessages();

//                newMessage.child("id").setValue(1);
//                newMessage.child("text").setValue(messageText);
//                newMessage.child("authorId").setValue(1);
//                newMessage.child("recipientId").setValue(2);
//                newMessage.child("dateAndTimInMillis").setValue(new Date().getTime());
//                newMessage.child("isUnread").setValue(true);

        }*/
    }

/*    private String getSelectedRecipientKey() {
        return selectedRecipientKey;
    }*/

/*    private String getSelectedRecipientId() {
        return recipientId;
    }*/

    private void init() {
        Log.e("LOG", "ChatActivity: init()");

        rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
        usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

        fbAuth              = FirebaseAuth.getInstance();

        intent              = getIntent();

//        boolean errorOccured = false;

        /*fbAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        Log.e("LOG", "ChatActivity: init(): fbAuth.getCurrentUser() is null: " +(fbAuth.getCurrentUser() == null));
        Log.e("LOG", "ChatActivity: init(): intent is null: " +(intent == null));

        if((fbAuth.getCurrentUser() != null) && (intent != null)) {

            senderId = fbAuth.getCurrentUser().getUid();

            Log.e("LOG", "ChatActivity: init(): senderId= " +senderId);

            if(!TextUtils.isEmpty(senderId)) {
//                errorOccured = true;
//
//            if(!errorOccured) {
                recipientId = intent.getStringExtra(CONST.USER_ID);

                Log.e("LOG", "ChatActivity: init(): recipientId= " + recipientId);

                if(!TextUtils.isEmpty(recipientId)) {

                    recipientName = intent.getStringExtra(CONST.USER_NAME);
                    if(!TextUtils.isEmpty(recipientName)) {
                        //getSupportActionBar().setTitle(recipientName);
                        chatAppToolbarUserName.setText(recipientName);
                    }

                    Log.e("LOG", "ChatActivity: init(): recipientThumbImage= " + recipientThumbImage);

                    recipientThumbImage = intent.getStringExtra(CONST.USER_THUMB_IMAGE);
                    if((!TextUtils.isEmpty(recipientThumbImage)) &&
                       (!recipientThumbImage.equals(CONST.DEFAULT_VALUE))) {

                        Log.e("LOG", "ChatActivity: init(): load avatar");

                            Picasso.with(this)
                                    .load(recipientThumbImage)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.default_avatar)
                                    .into(  chatAppToolbarAvatar,
                                            loadImageCallback);
                    }

                    //recipientLastSeen = "";
                    //showToolbarLastSeen();

                    rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
                    messagesDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_MESSAGES_CHILD);
                    usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

                    senderFBDatabaseRef = usersFBDatabaseRef.child(senderId);
                    //Log.e("LOG", "ChatActivity: init(): currentUserFBDatabaseRef is null: " + (senderFBDatabaseRef == null));

                    Log.e("LOG", "ChatActivity: onStop(): fbAuth.getCurrentUser() is null: " + (fbAuth.getCurrentUser() == null));

                    //if(senderFBDatabaseRef != null) {
                    if(fbAuth.getCurrentUser() != null) {
                        //Log.e("LOG", "ChatActivity: init(): currentUserFBDatabaseRef: " + senderFBDatabaseRef);
                        senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
                    }
                    else {

                        Log.e("LOG", "ChatActivity: init(): go to SetAccountActivity");

                        Intent setAccountIntent = new Intent(   ChatActivity.this,
                                                                SetAccountActivity.class);
                        setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(setAccountIntent);
                    }
                    //senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);

                    recipientFBDatabaseRef = usersFBDatabaseRef.child(recipientId);
                    recipientFBDatabaseRef.addListenerForSingleValueEvent(recipientDataListener);

                    chatFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_CHAT_CHILD).child(senderId);
                    chatFBDatabaseRef.addValueEventListener(chatDataListener);

                    showChatMessages();
                }
            }
            else
                Log.e("LOG", "ChatActivity: init(): sender id error!");
        }
        else {

            Intent loginIntent = new Intent(ChatActivity.this,
                                            LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }*/

//        if(intent != null) {
            //selectedRecipientKey = intent.getStringExtra("userKey");

            //Log.e("LOG", "ChatActivity: onStart(): selectedRecipientKey= " +selectedRecipientKey);

            //chatMessagesSum = 0;

            // ------------------------------------------------------- //

            //fbCurrentUser               = FirebaseAuth.getInstance().getCurrentUser();

            //senderId                    = fbCurrentUser.getUid();
            //recipientId                 = intent.getStringExtra(CONST.USER_ID);

//            rootFBDatabaseRef           = FirebaseDatabase.getInstance().getReference();
//            messagesDatabaseRef         = rootFBDatabaseRef.child(CONST.FIREBASE_MESSAGES_CHILD);
//            usersFBDatabaseRef          = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
//
//            senderFBDatabaseRef         = usersFBDatabaseRef.child(senderId);
//            recipientFBDatabaseRef      = usersFBDatabaseRef.child(recipientId);
//            recipientFBDatabaseRef.addValueEventListener(recipientDataListener);
//
//            Log.e("LOG", "ChatActivity: init(): recipientId= " + recipientId);

            // ------------------------------------------------------- //

//            showChatMessages();
//        }
    }

    private void initSender() {
        Log.e("LOG", "ChatActivity: initSender()");

        senderId = fbAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(senderId)) {

            chatFBDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_CHAT_CHILD).child(senderId);
            chatFBDatabaseRef.addValueEventListener(chatDataListener);

            senderFBDatabaseRef = usersFBDatabaseRef.child(senderId);

            if(senderFBDatabaseRef != null) {
                senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);

                initRecipient();
            }
            else {

                goToSetAccountActivity();

//                Log.e("LOG", "MainActivity: checkUser(): go to SetAccountActivity");
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                                                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//                finish();
            }

            //currentUserFBDatabaseRef.addListenerForSingleValueEvent(currentUserDataListener);

            //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef is null: " + (currentUserFBDatabaseRef == null));
            //Log.e("LOG", "MainActivity: initUser(): fbAuth.getCurrentUser() is null: " + (fbAuth.getCurrentUser() == null));

//            if(currentUserFBDatabaseRef != null) {
//                //if(fbAuth.getCurrentUser() != null) {
//                //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef: " + currentUserFBDatabaseRef);
//                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
//            }
//            else {
//
//                Log.e("LOG", "MainActivity: init(): go to SetAccountActivity");
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//            }
        }
        else
            Log.e("LOG", "ChatActivity: initSender(): get current user id error!");
    }

    private void initRecipient() {
        Log.e("LOG", "ChatActivity: initRecipient()");

        if(intent != null) {

            if(intent.hasExtra(CONST.USER_ID)) {
                recipientId = intent.getStringExtra(CONST.USER_ID);

                Log.e("LOG", "ChatActivity: init(): recipientId= " +recipientId);

                if (!TextUtils.isEmpty(recipientId)) {

                    recipientFBDatabaseRef = usersFBDatabaseRef.child(recipientId);
                    recipientFBDatabaseRef.addListenerForSingleValueEvent(recipientDataListener);

                    if(intent.hasExtra(CONST.USER_NAME)) {
                        recipientName = intent.getStringExtra(CONST.USER_NAME);

                        if (!TextUtils.isEmpty(recipientName)) {
                            //getSupportActionBar().setTitle(recipientName);
                            chatAppToolbarUserName.setText(recipientName);
                        }
                        else
                            Log.e("LOG", "ChatActivity: initRecipient(): recipient name is empty or null");
                    }
                    else
                        Log.e("LOG", "ChatActivity: initRecipient(): intent has no " +CONST.USER_NAME);

                    if(intent.hasExtra(CONST.USER_THUMB_IMAGE)) {
                        recipientThumbImage = intent.getStringExtra(CONST.USER_THUMB_IMAGE);

                        if( (!TextUtils.isEmpty(recipientThumbImage)) &&
                            (!recipientThumbImage.equals(CONST.DEFAULT_VALUE))) {

                            Log.e("LOG", "ChatActivity: initRecipient(): load recipient avatar");

                            Picasso.with(this)
                                    .load(recipientThumbImage)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.default_avatar)
                                    .into(  chatAppToolbarAvatar,
                                            loadImageCallback);
                        }
                        else
                            Log.e("LOG", "ChatActivity: initRecipient(): recipient link on avatar is empty or null");
                    }
                    else
                        Log.e("LOG", "ChatActivity: initRecipient(): intent has no " +CONST.USER_THUMB_IMAGE);

                    //showChatMessages();
                    loadMessages();
                }
                else
                    Log.e("LOG", "ChatActivity: initRecipient(): recipient id is empty or null");
            }
            else
                Log.e("LOG", "ChatActivity: initRecipient(): intent has no " +CONST.USER_ID);
        }
        else {
            Log.e("LOG", "ChatActivity: initRecipient(): intent is null");
        }
    }

    private void checkUser() {
        Log.e("LOG", "ChatActivity: checkUser()");

        FirebaseUser currentUser = fbAuth.getCurrentUser();

        if(currentUser == null) {
            goToLoginActivity();
        }
        else {
            initSender();
        }
    }

    private void goToLoginActivity() {
        Log.e("LOG", "ChatActivity: goToLoginActivity()");

        Intent loginIntent = new Intent(ChatActivity.this,
                                        LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void goToSetAccountActivity() {
        Log.e("LOG", "ChatActivity: goToSetAccountActivity()");

        Intent setAccountIntent = new Intent(   ChatActivity.this,
                                                SetAccountActivity.class);
        setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setAccountIntent);
        finish();
    }

    private void loadMessages() {
        Log.e("LOG", "ChatActivity: loadMessages()");

        Log.e("LOG", "ChatActivity: loadMessages(): senderId: "     +senderId);
        Log.e("LOG", "ChatActivity: loadMessages(): recipientId: "  +recipientId);

        messagesDatabaseRef = rootFBDatabaseRef.child(CONST.FIREBASE_MESSAGES_CHILD)
                .child(senderId)
                .child(recipientId);

        if(messagesDatabaseRef != null) {

            //messagesDatabaseRef.addChildEventListener(childEventListener);

            Query loadMessageQuery = messagesDatabaseRef.limitToLast(currentPageCount * CONST.LOAD_MESSAGES_COUNT);
            loadMessageQuery.addChildEventListener(loadMessagesEventListener);
        }
        else
            Log.e("LOG", "ChatActivity: loadMessages(): messagesDatabaseRef is null");
    }

    private void loadMoreMessages() {
        Log.e("LOG", "ChatActivity: loadMoreMessages()");

        if(messagesDatabaseRef != null) {

            Query loadMoreMessageQuery = messagesDatabaseRef.orderByKey().endAt(lastLoadedMessageKey).limitToLast(CONST.LOAD_MESSAGES_COUNT);
            loadMoreMessageQuery.addChildEventListener(loadMoreMessagesEventListener);
        }
        else
            Log.e("LOG", "ChatActivity: loadMessages(): messagesDatabaseRef is null");
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ValueEventListener recipientDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {

                recipientName = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                if(!TextUtils.isEmpty(recipientName)) {
                    chatAppToolbarUserName.setText(recipientName);
                }

                recipientIsOnline = (boolean) dataSnapshot.child(CONST.USER_IS_ONLINE).getValue();

                Log.e("LOG", "ChatActivity: recipientDataListener.onDataChange(): recipientIsOnline: " +recipientIsOnline);

                if(recipientIsOnline) {
                    chatAppToolbarLastSeen.setText(CONST.USER_ONLINE_STATUS);
                }
                else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    String userLastSeenTimeInMillis = dataSnapshot.child(CONST.USER_LAST_SEEN).getValue().toString();

                    Log.e("LOG", "ChatActivity: recipientDataListener.onDataChange(): userLastSeenTimeInMillis= " +userLastSeenTimeInMillis);

                    if(!TextUtils.isEmpty(userLastSeenTimeInMillis)) {

                        long lastTime = Long.parseLong(userLastSeenTimeInMillis);

                        recipientLastSeen = getTimeAgo.getTimeAgo(  lastTime,
                                                                    getApplicationContext());

                        Log.e("LOG", "ChatActivity: recipientDataListener.onDataChange(): recipientLastSeen= " +recipientLastSeen);

                        if(!TextUtils.isEmpty(recipientLastSeen)) {
                            // chatAppToolbarLastSeen.setText(lastSeen);

                            showToolbarLastSeen();
                        }
                    }

                    /*recipientLastSeen = dataSnapshot.child(CONST.USER_LAST_SEEN).getValue().toString();
                    if(!TextUtils.isEmpty(recipientLastSeen)) {
                        // chatAppToolbarLastSeen.setText(recipientLastSeen);
                        showToolbarLastSeen();
                    }*/
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ValueEventListener chatDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            if(!dataSnapshot.hasChild(recipientId)) {

                Map chatAddMap = new HashMap();
                chatAddMap.put(CONST.CHAT_SEEN,        false);
                chatAddMap.put(CONST.CHAT_TIMESTAMP,   ServerValue.TIMESTAMP);

                // ------------------------------------------------------------------- //

                Map chatUserMap         = new HashMap();
                StringBuilder chatKeySB = new StringBuilder();

                chatKeySB.append(CONST.CHAT_KEY);
                chatKeySB.append(senderId);
                chatKeySB.append("/");
                chatKeySB.append(recipientId);
                chatUserMap.put(chatKeySB.toString(), chatAddMap);

                chatKeySB.setLength(0);

                chatKeySB.append(CONST.CHAT_KEY);
                chatKeySB.append(recipientId);
                chatKeySB.append("/");
                chatKeySB.append(senderId);
                chatUserMap.put(chatKeySB.toString(), chatAddMap);

                rootFBDatabaseRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError != null) {

                            Log.e("LOG", "ChatActivity: chatDataListener.onDataChange(): rootFBDatabaseRef.updateChildren.onComplete error: " +databaseError.getMessage().toString());
                        }
                    }
                });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    ChildEventListener loadMessagesEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            Message message = dataSnapshot.getValue(Message.class);

            Log.e("LOG", "ChatActivity: loadMessagesEventListener: onChildAdded(): message: " +message.getMessageText());

            loadMoreItemPosition++;

            if(loadMoreItemPosition == 1) {
                lastLoadedMessageKey = dataSnapshot.getKey();
            }

            messagesList.add(message);

            messageAdapter.notifyDataSetChanged();

            messagesRecyclerView.scrollToPosition(messagesList.size() - 1);

            chatSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener loadMoreMessagesEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Message message = dataSnapshot.getValue(Message.class);

            Log.e("LOG", "ChatActivity: loadMoreMessagesEventListener: onChildAdded(): message: " +message.getMessageText());

            messagesList.add(   loadMoreItemPosition++,
                                message);

            if(loadMoreItemPosition == 1) {
                lastLoadedMessageKey = dataSnapshot.getKey();
            }

            messageAdapter.notifyDataSetChanged();

            //messagesRecyclerView.scrollToPosition(messagesList.size() - 1);

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

//    ChildEventListener childEventListener = new ChildEventListener() {
//
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            Message message = dataSnapshot.getValue(Message.class);
//
//            messageList.add(message.getMessageText());
//
//            fbAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onCancelled(FirebaseError firebaseError) {
//
//        }
//    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startSendMessage();
        }
    };

    TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // set available amount of characters
            messageLeftCharsBodyTextView.setText("" + (CONST.PUBLICATION_MAX_LENGTH - messageEditText.length()));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            currentPageCount++;

            loadMoreItemPosition = 0;

//            messagesList.clear();

            loadMoreMessages();
        }
    };
}
