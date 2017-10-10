package com.shmeli.surakat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;

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

    private Firebase                fbRef;
    private FirebaseAuth            fbAuth;
    private FirebaseUser            fbCurrentUser;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> fbAdapter;

    private DatabaseReference       messagesDatabaseRef;
    private DatabaseReference       usersFBDatabaseRef;

    private ArrayList<String>       messageList = new ArrayList<>();

    private ProgressDialog          progressDialog;

    private boolean chatTopDividerIsVisible;

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

        messageEditText                 = UiUtils.findView(this, R.id.messageEditText);
        messageEditText.addTextChangedListener(onTextChangedListener);

        messageLeftCharsBodyTextView    = UiUtils.findView(this, R.id.messageLeftCharsBodyTextView);
        messageLeftCharsBodyTextView.setText("" +CONST.PUBLICATION_MAX_LENGTH);

        chatTopDividerView              = UiUtils.findView(this, R.id.chatTopDividerView);

        sendButton                      = UiUtils.findView(this, R.id.sendButton);
        sendButton.setOnClickListener(sendClickListener);

        messagesRecyclerView            = UiUtils.findView(this, R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fbRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fbAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                                                                            R.layout.message_row,
                                                                            MessageViewHolder.class,
                                                                            messagesDatabaseRef) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {

                if(!chatTopDividerIsVisible) {

                    Log.e("LOG", "ChatActivity: onStart(): populateViewHolder(): SHOW DIVIDER");

                    chatTopDividerView.setVisibility(View.VISIBLE);
                    chatTopDividerIsVisible = true;
                }

                viewHolder.setMessageAuthorName(model.getMessageAuthorName());
                viewHolder.setMessageText(model.getMessageText());
            }
        };

        messagesRecyclerView.setAdapter(fbAdapter);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public MessageViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            CardView messageCardView = UiUtils.findView(view, R.id.messageCardView);
            messageCardView.setBackgroundResource(R.drawable.input_outline);
        }

        public void setMessageAuthorName(String messageAuthorName) {

            TextView messageAuthorNameTextView = UiUtils.findView(view, R.id.messageAuthorNameTextView);
            messageAuthorNameTextView.setText(messageAuthorName);
        }

        public void setMessageText(String messageText) {

            TextView messageTextView = UiUtils.findView(view, R.id.messageTextView);
            messageTextView.setText(messageText);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    ChildEventListener childEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Message message = dataSnapshot.getValue(Message.class);

            messageList.add(message.getMessageText());

            fbAdapter.notifyDataSetChanged();
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
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startSendMessage();
        }

        private void startSendMessage() {

            final String messageText = messageEditText.getText().toString();

            if(!TextUtils.isEmpty(messageText.trim())) {

                progressDialog.setMessage(getResources().getString(R.string.message_setting_account));
                progressDialog.show();

                final DatabaseReference newMessage = messagesDatabaseRef.push();

                usersFBDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                        newMessage.child("messageId").setValue("" +1);
                        newMessage.child("messageAuthorId").setValue("" +fbAuth.getCurrentUser().getUid());
                        newMessage.child("messageAuthorName").setValue(dataSnapshot.child("userName").getValue()); //"" +fbAuth.getCurrentUser().getUid());
                        newMessage.child("messageRecipientId").setValue("" +2);
                        newMessage.child("messageDateAndTime").setValue("" +new Date().getTime());
                        newMessage.child("messageText").setValue(messageText);
                        newMessage.child("isMessageUnread").setValue("" +true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                messageEditText.setText("");

                progressDialog.dismiss();

//                newMessage.child("id").setValue(1);
//                newMessage.child("text").setValue(messageText);
//                newMessage.child("authorId").setValue(1);
//                newMessage.child("recipientId").setValue(2);
//                newMessage.child("dateAndTimInMillis").setValue(new Date().getTime());
//                newMessage.child("isUnread").setValue(true);

            }
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
