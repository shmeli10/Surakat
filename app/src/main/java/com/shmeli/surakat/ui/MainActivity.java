package com.shmeli.surakat.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView    userListView;

    private Firebase    mRef;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRef = new Firebase(CONST.FIREBASE_USERS_LINK);

        adapter = new ArrayAdapter<>(   this,
                                        android.R.layout.simple_list_item_1,
                                        userList);

        userListView = UiUtils.findView(this, R.id.userListView);
        userListView.setOnItemClickListener(onItemClickListener);
        userListView.setAdapter(adapter);


        mRef.addChildEventListener(childEventListener);
    }

    // ------------------------------ LISTENERS ----------------------------------------- //



    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            startActivity(new Intent(   MainActivity.this,
                                        ChatActivity.class));
        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            String userName = dataSnapshot.getValue(String.class);

            userList.add(userName);

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
    };
}
