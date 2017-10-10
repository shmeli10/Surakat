package com.shmeli.surakat.ui;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.Message;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.utils.UiUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView                    userRecyclerView;
    private RelativeLayout                  mainContainer;

    private Firebase                        fbRef;
    private FirebaseAuth                    fbAuth;
    private FirebaseAuth.AuthStateListener  fbAuthListener;
    private DatabaseReference               usersFBDatabaseRef;

    private FirebaseRecyclerAdapter<User, MainActivity.UserViewHolder> fbAdapter;

    private ArrayList<String> userList = new ArrayList<>();

    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbRef               = new Firebase(CONST.FIREBASE_USERS_LINK);
        fbAuth              = FirebaseAuth.getInstance();

        usersFBDatabaseRef = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);
        usersFBDatabaseRef.keepSynced(true);

        fbAuthListener      = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.e("LOG", "MainActivity: onAuthStateChanged(): currentUser is null: " +(firebaseAuth.getCurrentUser() == null));

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this,
                                                    LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                else {

                    String currentUser = firebaseAuth.getCurrentUser().getDisplayName();

                    Log.e("LOG", "MainActivity: onAuthStateChanged(): currentUser: " +currentUser);
                }
            }
        };

        mainContainer       = UiUtils.findView(this, R.id.mainContainer);

        userRecyclerView    = UiUtils.findView(this, R.id.userRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setOnClickListener(onClickListener);

        fbRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExists();

        fbAuth.addAuthStateListener(fbAuthListener);

        fbAdapter = new FirebaseRecyclerAdapter<User, MainActivity.UserViewHolder>( User.class,
                                                                                    R.layout.user_row,
                                                                                    MainActivity.UserViewHolder.class,
                                                                                    usersFBDatabaseRef) {
            @Override
            protected void populateViewHolder(MainActivity.UserViewHolder   viewHolder,
                                              User                          model,
                                              final int                     position) {

                if(!getRef(position).getKey().equals(fbAuth.getCurrentUser().getUid())) {
                    Log.e("LOG", "MainActivity: onStart(): populateViewHolder: show user: " +model.getUserName());

                    viewHolder.setUserName(model.getUserName());

                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String userKey = getRef(position).getKey();

                            User clickedUser = fbAdapter.getItem(position);

                            String clickedUserName  = clickedUser.getUserName();

                            Log.e("LOG", "Clicked on " +userKey);
                            Log.e("LOG", "Clicked on " +clickedUserName);

                            Intent chatIntent = new Intent( MainActivity.this,
                                    ChatActivity.class);
                            chatIntent.putExtra("userKey", userKey);

                            startActivity(chatIntent);
                        }
                    });
                }
                else {
                    Log.e("LOG", "MainActivity: onStart(): populateViewHolder: hide user: " +model.getUserName());

                    viewHolder.hideItem();
                }
            }
        };

        userRecyclerView.setAdapter(fbAdapter);
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

    private void checkUserExists() {
        Log.e("LOG", "MainActivity: checkUserExists()");

        usersFBDatabaseRef.addValueEventListener(valueEventListener);
    }

    private void logout() {
        fbAuth.signOut();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View            view;
        TextView        userNameTextView;
        LinearLayout    userContainer;

        public UserViewHolder(View itemView) {
            super(itemView);

            view                = itemView;
            userContainer       = UiUtils.findView(view, R.id.userContainer);
            userNameTextView    = UiUtils.findView(view, R.id.userNameTextView);
        }

        public void setUserName(String userName) {
            userNameTextView.setText(userName);
        }

        public void hideItem() {
            view.setVisibility(View.GONE);
            userContainer.setVisibility(View.GONE);
            userNameTextView.setVisibility(View.GONE);
        }
    }

    // ------------------------------ LISTENERS ----------------------------------------- //

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(   MainActivity.this,
                                        ChatActivity.class));
        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            User user = dataSnapshot.getValue(User.class);

            userList.add(user.getUserName());
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

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            Log.e("LOG", "LoginActivity: valueEventListener: (dataSnapshot.hasChild(" +userId+ ")): " +(dataSnapshot.hasChild(userId)));

            if(!dataSnapshot.hasChild(userId)) {
                startActivity(new Intent(MainActivity.this, SetAccountActivity.class));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
