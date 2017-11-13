package com.shmeli.surakat.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.holders.UserViewHolder;
import com.shmeli.surakat.model.User;
import com.shmeli.surakat.ui.MainActivity;
import com.shmeli.surakat.ui.ProfileActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

    private View                view;
    private RecyclerView        allUsersRecyclerVIew;

    private DatabaseReference   rootFBDatabaseRef;
    private DatabaseReference   allUsersFBDatabaseRef;
    private DatabaseReference   usersFBDatabaseRef;

    private String              currentUserId           = "";

    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        view = inflater.inflate(R.layout.all_users_fragment,
                container,
                false);

        allUsersRecyclerVIew = UiUtils.findView(view, R.id.allUsersRecyclerVIew);
        allUsersRecyclerVIew.setHasFixedSize(true);
        allUsersRecyclerVIew.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUserId           = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootFBDatabaseRef       = FirebaseDatabase.getInstance().getReference();
//        allUsersFBDatabaseRef   = rootFBDatabaseRef.child(CONST.FIREBASE_FRIENDS_CHILD).child(currentUserId);
//        allUsersFBDatabaseRef.keepSynced(true);
        usersFBDatabaseRef      = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
        usersFBDatabaseRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        populateAllUsersList();
    }

    private void populateAllUsersList() {

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

                        Log.e("LOG", "AllUsersFragment: userClickListener: selectedUserId= " +selectedUserId);

                        Intent profileIntent = new Intent(  getContext(),
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
