package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.Message;
import com.shmeli.surakat.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

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

    private RelativeLayout          chatContainer;

    //private Firebase                fbRef;
    private FirebaseAuth            fbAuth;
    //private FirebaseUser            fbCurrentUser;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> fbAdapter;

    private DatabaseReference       messagesDatabaseRef;

    private DatabaseReference       senderFBDatabaseRef;
    private DatabaseReference       rootFBDatabaseRef;
    private DatabaseReference       recipientFBDatabaseRef;
    private DatabaseReference       usersFBDatabaseRef;

    private Query                   messagesQuery;

//    private ArrayList<String>       messageList = new ArrayList<>();

    private ProgressDialog          progressDialog;

    private boolean chatTopDividerIsVisible;
    private boolean canIncreaseMessagesSum = true;

    //private String  selectedRecipientKey = "";

    private String senderId    = "";

    private String recipientId          = "";
    private String recipientLastSeen    = "";
    private String recipientName        = "";
    private String recipientThumbImage  = "";

    private boolean recipientIsOnline   = false;

    private int chatMessagesSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setChatToolbar();

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

        messagesRecyclerView            = UiUtils.findView(this, R.id.messagesRecyclerView);
        //messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        init();

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

        chatAppToolbarLastSeen.setVisibility(View.VISIBLE);
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


    private void showChatMessages() {

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
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

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
    }

    private void startSendMessage() {

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

//        boolean errorOccured = false;

        fbAuth = FirebaseAuth.getInstance();

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
                    senderFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);

                    recipientFBDatabaseRef = usersFBDatabaseRef.child(recipientId);
                    recipientFBDatabaseRef.addListenerForSingleValueEvent(recipientDataListener);

                    showChatMessages();
                }
            }
        }
        else {

            Intent loginIntent = new Intent(ChatActivity.this,
                                            LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }

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

                if(recipientIsOnline) {
                    chatAppToolbarLastSeen.setText(CONST.USER_ONLINE_STATUS);
                }
                else {

                    recipientLastSeen = dataSnapshot.child(CONST.USER_LAST_SEEN).getValue().toString();
                    if(!TextUtils.isEmpty(recipientLastSeen)) {
                        // chatAppToolbarLastSeen.setText(recipientLastSeen);
                        showToolbarLastSeen();
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
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
}
