package com.shmeli.surakat.ui;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private EditText            messageEditText;
    private Button              sendButton;
    private RecyclerView        messagesRecyclerView;

    private Firebase            mRef;

    private DatabaseReference   databaseReference;

    private ArrayList<String>   messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //mRef = new Firebase("https://surakat-80b2e.firebaseio.com/Messages");
        mRef = new Firebase(CONST.FIREBASE_MESSAGES_LINK);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_MESSAGES_CHILD);

        messageEditText = UiUtils.findView(this, R.id.messageEditText);
        sendButton      = UiUtils.findView(this, R.id.sendButton);
        sendButton.setOnClickListener(sendClickListener);

        messagesRecyclerView = UiUtils.findView(this, R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // messageListView = UiUtils.findView(this, R.id.messageListView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                                android.R.layout.simple_list_item_1,
                                                                messageList);

        //messageListView.setAdapter(adapter);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //String messageText = dataSnapshot.getValue(String.class);

                Message message = dataSnapshot.getValue(Message.class);

//                Message newMessage = new Message(messageText, "author", false);
//                messageList.add(newMessage);

                messageList.add(message.getMessageText());

                adapter.notifyDataSetChanged();
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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Message, MessageViewHolder> fbAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                                                                                                                                R.layout.message_row,
                                                                                                                                MessageViewHolder.class,
                                                                                                                                databaseReference) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {

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
        }

        public void setMessageText(String messageText) {

            TextView messageTextView = UiUtils.findView(view, R.id.messageTextView);
            messageTextView.setText(messageText);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String messageText = messageEditText.getText().toString();

            if(!TextUtils.isEmpty(messageText.trim())) {

                DatabaseReference newMessage = databaseReference.push();

                newMessage.child("messageId").setValue("" +1);
                newMessage.child("messageAuthorId").setValue("" +FirebaseAuth.getInstance().getCurrentUser().getUid());
                newMessage.child("messageRecipientId").setValue("" +2);
                newMessage.child("messageDateAndTime").setValue("" +new Date().getTime());
                newMessage.child("messageText").setValue(messageText);
                newMessage.child("isMessageUnread").setValue("" +true);

                messageEditText.setText("");

//                newMessage.child("id").setValue(1);
//                newMessage.child("text").setValue(messageText);
//                newMessage.child("authorId").setValue(1);
//                newMessage.child("recipientId").setValue(2);
//                newMessage.child("dateAndTimInMillis").setValue(new Date().getTime());
//                newMessage.child("isUnread").setValue(true);

            }
        }
    };

}
