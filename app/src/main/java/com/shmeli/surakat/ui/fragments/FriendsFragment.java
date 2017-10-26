package com.shmeli.surakat.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.shmeli.surakat.holders.FriendsViewHolder;
import com.shmeli.surakat.model.Friends;
import com.shmeli.surakat.utils.UiUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private View                view;
    private RecyclerView        friendsRecyclerVIew;

    private DatabaseReference   rootFBDatabaseRef;
    private DatabaseReference   friendsFBDatabaseRef;
    private DatabaseReference   usersFBDatabaseRef;

    private String              currentUserId = "";

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends,
                                container,
                                false);

        friendsRecyclerVIew    = UiUtils.findView(view, R.id.friendsRecyclerVIew);
        friendsRecyclerVIew.setHasFixedSize(true);
        friendsRecyclerVIew.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUserId           = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootFBDatabaseRef       = FirebaseDatabase.getInstance().getReference();
        friendsFBDatabaseRef    = rootFBDatabaseRef.child(CONST.FIREBASE_FRIENDS_CHILD).child(currentUserId);
        friendsFBDatabaseRef.keepSynced(true);
        usersFBDatabaseRef      = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
        usersFBDatabaseRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.user_row,
                FriendsViewHolder.class,
                friendsFBDatabaseRef) {

            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder,
                                              Friends           model,
                                              int               position) {

                friendsViewHolder.setDate(model.getDate());

                String listUserId = getRef(position).getKey();

                usersFBDatabaseRef.child(listUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                        String userThumbImageUrl    = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setAvatar(userThumbImageUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendsRecyclerVIew.setAdapter(friendsRecyclerViewAdapter);
    }

    // ------------------------ VALUE EVENT LISTENERS ------------------------------------ //

}
