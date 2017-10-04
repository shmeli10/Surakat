package com.shmeli.surakat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.shmeli.surakat.model.Message;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText    messageEditText;
    private Button      sendButton;
    private ListView    messageListView;

    private Firebase    mRef;

    // private ArrayList<Message> messageList = new ArrayList<>();
    private ArrayList<String> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRef = new Firebase("https://surakat-80b2e.firebaseio.com/Messages");

        messageEditText = (EditText) findViewById(R.id.messageEditText);

        sendButton      = (Button) findViewById(R.id.sendButton);

        messageListView = (ListView) findViewById(R.id.messageListView);

        //final ArrayAdapter<Message> adapter = new ArrayAdapter<>(  this,
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(  this,
                                                                    android.R.layout.simple_list_item_1,
                                                                    messageList);

        messageListView.setAdapter(adapter);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String messageText = dataSnapshot.getValue(String.class);

//                Message newMessage = new Message(messageText, "author", false);
//                messageList.add(newMessage);

                messageList.add(messageText);

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
}
