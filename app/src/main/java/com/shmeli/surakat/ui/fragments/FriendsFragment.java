package com.shmeli.surakat.ui.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.shmeli.surakat.ui.ChatActivity;
import com.shmeli.surakat.ui.ProfileActivity;
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

    private String              currentUserId           = "";
    private String              selectedUserId          = "";
    private String              selectedUserName        = "";
    private String              selectedUserThumbImage  = "";

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

//                friendsViewHolder.setDate(model.getFriendshipStartDate());
                friendsViewHolder.setStatus("");

                //String listUserId = getRef(position).getKey();
                selectedUserId = getRef(position).getKey();

                usersFBDatabaseRef.child(selectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //String userName             = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                        selectedUserName        = dataSnapshot.child(CONST.USER_NAME).getValue().toString();
                        //String userThumbImageUrl    = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();
                        selectedUserThumbImage  = dataSnapshot.child(CONST.USER_THUMB_IMAGE).getValue().toString();

                        if(dataSnapshot.hasChild(CONST.USER_IS_ONLINE)) {
                            Boolean userIsOnline = (boolean) dataSnapshot.child(CONST.USER_IS_ONLINE).getValue();
                            friendsViewHolder.setOnlineStatus(userIsOnline);

                            //Log.e();
                        }

                        friendsViewHolder.setName(selectedUserName);
                        friendsViewHolder.setAvatar(selectedUserThumbImage);

                        friendsViewHolder.itemView.setOnClickListener(friendClickListener);

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

    View.OnClickListener friendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String openProfileText      = getResources().getString(R.string.text_open_profile);
            String sendMessageText      = getResources().getString(R.string.text_send_message);
            String selectOptionsText    = getResources().getString(R.string.text_select_options);

            int alertDialogDividerColorResId = getResources().getColor(R.color.colorAccent);

            CharSequence[] optionsArr = new CharSequence[] {openProfileText, sendMessageText};

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(   getContext(),
                                                                                R.style.Theme_Sphinx_Dialog_Alert);
            alertDialogBuilder.setTitle(selectOptionsText);
            alertDialogBuilder.setItems(optionsArr,
                                        optionClickListener);
//            alertDialogBuilder.show();

            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setTitle(alertDialogHeaderResId);
            alertDialog.show();

            // Set title divider color
            int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = alertDialog.findViewById(titleDividerId);

            if (titleDivider != null)
                titleDivider.setBackgroundColor(alertDialogDividerColorResId);
        }
    };

    DialogInterface.OnClickListener optionClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog,
                            int             which) {

            switch(which) {

                case CONST.OPEN_PROFILE_TYPE:
                    Intent profileIntent = new Intent(  getContext(),
                                                        ProfileActivity.class);
                    profileIntent.putExtra( CONST.USER_ID,
                                            selectedUserId);
                    startActivity(profileIntent);
                    break;
                case CONST.SEND_MESSAGE_TYPE:
                    Intent chatIntent = new Intent( getContext(),
                                                    ChatActivity.class);
                    chatIntent.putExtra(CONST.USER_ID,
                                        selectedUserId);
                    chatIntent.putExtra(CONST.USER_NAME,
                                        selectedUserName);
                    chatIntent.putExtra(CONST.USER_THUMB_IMAGE,
                                        selectedUserThumbImage);
                    startActivity(chatIntent);
                    break;
            }
        }
    };
}
