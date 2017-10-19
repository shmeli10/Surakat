package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;

import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class ChatActivity extends AppCompatActivity {

    private EditText                messageEditText;
    private TextView                messageLeftCharsBodyTextView;
    private View                    chatTopDividerView;

    private Button                  sendButton;
    private RecyclerView            messagesRecyclerView;

    private RelativeLayout          chatContainer;

    private Firebase                fbRef;
    private FirebaseAuth            fbAuth;
    private FirebaseUser            fbCurrentUser;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> fbAdapter;

    private DatabaseReference       messagesDatabaseRef;
    private DatabaseReference       usersFBDatabaseRef;

    private Query                   messagesQuery;

//    private ArrayList<String>       messageList = new ArrayList<>();

    private ProgressDialog          progressDialog;

    private boolean chatTopDividerIsVisible;
    private boolean canIncreaseMessagesSum = true;

    private String  selectedRecipientKey = "";

    private int chatMessagesSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fbRef                           = new Firebase(CONST.FIREBASE_MESSAGES_LINK);
        fbAuth                          = FirebaseAuth.getInstance();
        fbCurrentUser                   = fbAuth.getCurrentUser();

        progressDialog                  = new ProgressDialog(this);

        messagesDatabaseRef             = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_MESSAGES_CHILD);
        usersFBDatabaseRef              = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD).child(fbCurrentUser.getUid());

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

        Intent intent = getIntent();

        if(intent != null) {
            selectedRecipientKey = intent.getStringExtra("userKey");

            Log.e("LOG", "ChatActivity: onStart(): selectedRecipientKey= " +selectedRecipientKey);

            //chatMessagesSum = 0;

            showChatMessages();

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
        }
        else {

            Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): RETURN BACK_2");
        }
    }

    private void showChatMessages() {

        final String currentUserKey = fbAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(selectedRecipientKey)) {
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
                         ( (messageAuthorKey.equals(currentUserKey) && messageRecipientKey.equals(selectedRecipientKey)) ||
                           (messageAuthorKey.equals(selectedRecipientKey) && messageRecipientKey.equals(currentUserKey)) ) ) {

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
        }
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

        final String messageText = messageEditText.getText().toString();

        if(!TextUtils.isEmpty(messageText.trim())) {

            progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
            progressDialog.show();

            String recipientKey = getSelectedRecipientKey();

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

        }
    }

    private String getSelectedRecipientKey() {
        return selectedRecipientKey;
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

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
