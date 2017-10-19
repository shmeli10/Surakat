package com.shmeli.surakat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.holders.UserViewHolder;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 10/19/17.
 */

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar             allUsersPageToolbar;

    private RecyclerView        allUsersRecyclerVIew;

    private DatabaseReference   usersFBDatabaseRef;

    //private String              selectedUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        allUsersPageToolbar     = UiUtils.findView(this, R.id.allUsersPageToolbar);
        setSupportActionBar(allUsersPageToolbar);
        getSupportActionBar().setTitle(R.string.text_all_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersRecyclerVIew    = UiUtils.findView(this, R.id.allUsersRecyclerVIew);
        allUsersRecyclerVIew.setHasFixedSize(true);
        allUsersRecyclerVIew.setLayoutManager(new LinearLayoutManager(this));

        usersFBDatabaseRef  = FirebaseDatabase.getInstance().getReference().child(CONST.FIREBASE_USERS_CHILD);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UserViewHolder> fbAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                                                                                                                    R.layout.user_row,
                                                                                                                    UserViewHolder.class,
                                                                                                                    usersFBDatabaseRef) {
            @Override
            protected void populateViewHolder(UserViewHolder    viewHolder,
                                              User              model,
                                              int               position) {

                viewHolder.setName(model.getUserName());
                viewHolder.setStatus(model.getUserStatus());
                viewHolder.setAvatar(model.getUserThumbImageUrl());

                final String selectedUserId = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Log.e("LOG", "AllUsersActivity: userClickListener: selectedUserId= " +selectedUserId);

                        Intent profileIntent = new Intent(  AllUsersActivity.this,
                                                            ProfileActivity.class);
                        profileIntent.putExtra(CONST.USER_ID, selectedUserId);
                        startActivity(profileIntent);
                    }
                });
                //viewHolder.itemView.setOnClickListener(userClickListener);
            }
        };

        allUsersRecyclerVIew.setAdapter(fbAdapter);
    }
}
