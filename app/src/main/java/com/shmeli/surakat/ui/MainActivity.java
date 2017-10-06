package com.shmeli.surakat.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView                        userListView;
    private RelativeLayout                  mainContainer;

    private Firebase                        fbRef;
    private FirebaseAuth                    fbAuth;
    private FirebaseAuth.AuthStateListener  fbAuthListener;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbRef           = new Firebase(CONST.FIREBASE_USERS_LINK);
        fbAuth          = FirebaseAuth.getInstance();

        fbAuthListener  = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this,
                                                    LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        adapter = new ArrayAdapter<>(   this,
                                        android.R.layout.simple_list_item_1,
                                        userList);

        mainContainer = UiUtils.findView(this, R.id.mainContainer);

        userListView = UiUtils.findView(this, R.id.userListView);
        userListView.setOnItemClickListener(onItemClickListener);
        userListView.setAdapter(adapter);


        fbRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fbAuth.addAuthStateListener(fbAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("LOG", "MainActivity: onOptionsItemSelected()");

        if(item.getItemId() == R.id.menu_signout) {

            logout();
        }

        return true;
    }

    private void logout() {

        fbAuth.signOut();
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
