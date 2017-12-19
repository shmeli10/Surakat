package com.shmeli.surakat.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


import com.shmeli.surakat.R;
import com.shmeli.surakat.adapters.MainPageViewPagerAdapter;
import com.shmeli.surakat.data.CONST;

import com.shmeli.surakat.ui.settings.SettingsActivity;

import com.shmeli.surakat.utils.UiUtils;


public class MainActivity extends AppCompatActivity {

//    private RecyclerView                    userRecyclerView;
    private RelativeLayout                  mainContainer;

    private TabLayout                       mainPageTabLayout;
    private Toolbar                         mainPageToolbar;
    private ViewPager                       mainPageViewPager;

//    private Firebase                        fbRef;
//    private FirebaseUser                    fbUser;

    private DatabaseReference               rootFBDatabaseRef;
    private DatabaseReference               currentUserFBDatabaseRef;
    private DatabaseReference               usersFBDatabaseRef;

    private FirebaseAuth                    fbAuth;

    private MainPageViewPagerAdapter        mainPageViewPagerAdapter;

    private String currentUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        mainContainer       = UiUtils.findView(this, R.id.mainContainer);
        mainPageToolbar     = UiUtils.findView(this, R.id.mainPageToolbar);
        setSupportActionBar(mainPageToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        mainPageViewPagerAdapter = new MainPageViewPagerAdapter( getApplicationContext(),
                                                                 getSupportFragmentManager());

        mainPageViewPager   = UiUtils.findView(this, R.id.mainPageViewPager);
        mainPageViewPager.setAdapter(mainPageViewPagerAdapter);

        mainPageTabLayout   = UiUtils.findView(this, R.id.mainPageTabLayout);
        mainPageTabLayout.setupWithViewPager(mainPageViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUser();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("LOG", "MainActivity: onStop()");

        FirebaseUser currentUser = fbAuth.getCurrentUser();

        if(currentUser != null) {
            currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(false);
            currentUserFBDatabaseRef.child(CONST.USER_LAST_SEEN).setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(  R.menu.menu,
                                    menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("LOG", "MainActivity: onOptionsItemSelected()");

        switch(item.getItemId()) {

            case R.id.menu_signout:
                showAlertDialog();
                break;
            case R.id.menu_settings:
                moveToSettings();
                break;
        }

        return true;
    }

    private void init() {
        Log.e("LOG", "MainActivity: init()");

        rootFBDatabaseRef   = FirebaseDatabase.getInstance().getReference();
        usersFBDatabaseRef  = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);

        fbAuth              = FirebaseAuth.getInstance();

        //initUser();

        //checkUser();

        //fbAuth.addAuthStateListener(fbAuthListener);

        /*Log.e("LOG", "MainActivity: init(): (fbAuth.getCurrentUser() is null): " +(fbAuth.getCurrentUser() == null));

        if(fbAuth.getCurrentUser() != null) {

            currentUserId = fbAuth.getCurrentUser().getUid();
            Log.e("LOG", "MainActivity: init(): senderId= " +currentUserId);

            if(!TextUtils.isEmpty(currentUserId)) {

                rootFBDatabaseRef           = FirebaseDatabase.getInstance().getReference();
                usersFBDatabaseRef          = rootFBDatabaseRef.child(CONST.FIREBASE_USERS_CHILD);
                currentUserFBDatabaseRef    = usersFBDatabaseRef.child(currentUserId);
                //currentUserFBDatabaseRef.addListenerForSingleValueEvent(currentUserDataListener);

                //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef is null: " + (currentUserFBDatabaseRef == null));
                Log.e("LOG", "MainActivity: init(): fbAuth.getCurrentUser() is null: " + (fbAuth.getCurrentUser() == null));

                if(currentUserFBDatabaseRef != null) {
                //if(fbAuth.getCurrentUser() != null) {
                    //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef: " + currentUserFBDatabaseRef);
                    currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
                }
                else {

                    Log.e("LOG", "MainActivity: init(): go to SetAccountActivity");

                    Intent setAccountIntent = new Intent(   MainActivity.this,
                                                            SetAccountActivity.class);
                    setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(setAccountIntent);
                }
            }
            else
                Log.e("LOG", "MainActivity: init(): current user id error!");
        }
        else {

            Log.e("LOG", "MainActivity: init(): go to LoginActivity");

            Intent loginIntent = new Intent(MainActivity.this,
                                            LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }*/
    }

    private void initUser() {
        Log.e("LOG", "MainActivity: initUser()");

        currentUserId = fbAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(currentUserId)) {

            currentUserFBDatabaseRef    = usersFBDatabaseRef.child(currentUserId);

            if(currentUserFBDatabaseRef != null)
                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
            else {

                goToSetAccountActivity();

//                Log.e("LOG", "MainActivity: checkUser(): go to SetAccountActivity");
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                                                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//                finish();
            }

            //currentUserFBDatabaseRef.addListenerForSingleValueEvent(currentUserDataListener);

            //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef is null: " + (currentUserFBDatabaseRef == null));
            //Log.e("LOG", "MainActivity: initUser(): fbAuth.getCurrentUser() is null: " + (fbAuth.getCurrentUser() == null));

//            if(currentUserFBDatabaseRef != null) {
//                //if(fbAuth.getCurrentUser() != null) {
//                //Log.e("LOG", "MainActivity: init(): currentUserFBDatabaseRef: " + currentUserFBDatabaseRef);
//                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
//            }
//            else {
//
//                Log.e("LOG", "MainActivity: init(): go to SetAccountActivity");
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//            }
        }
        else
            Log.e("LOG", "MainActivity: initUser(): get current user id error!");
    }

    private void checkUser() {
        Log.e("LOG", "MainActivity: checkUser()");

        FirebaseUser currentUser = fbAuth.getCurrentUser();

        if(currentUser == null) {

            goToLoginActivity();

//            Log.e("LOG", "MainActivity: checkUser(): go to LoginActivity");
//
//            Intent loginIntent = new Intent(MainActivity.this,
//                                            LoginActivity.class);
//            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(loginIntent);
//            finish();
        }
        else {
            initUser();
        }
    }

//    private void checkUserExists() {
//        Log.e("LOG", "MainActivity: checkUserExists()");
//
//        usersFBDatabaseRef.addValueEventListener(valueEventListener);
//    }

    private void logout() {
        Log.e("LOG", "MainActivity: logout()");
        fbAuth.signOut();

        goToLoginActivity();

//        Log.e("LOG", "MainActivity: logout(): go to LoginActivity");
//        startActivity(new Intent(   MainActivity.this,
//                                    LoginActivity.class));
//        finish();
    }

    private void goToLoginActivity() {
        Log.e("LOG", "MainActivity: goToLoginActivity()");

        Intent loginIntent = new Intent(MainActivity.this,
                                        LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void goToSetAccountActivity() {
        Log.e("LOG", "MainActivity: goToSetAccountActivity()");

        Intent setAccountIntent = new Intent(   MainActivity.this,
                                                SetAccountActivity.class);
        setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setAccountIntent);
        finish();
    }

    private void moveToSettings() {

        Intent settingsIntent = new Intent( MainActivity.this,
                                            SettingsActivity.class);
        //settingsIntent.addFlags();
        startActivity(settingsIntent);
        //finish();
    }

    private void showAlertDialog() {

        int alertDialogMessageResId = R.string.text_logout_confirm;
        int alertDialogHeaderResId  = R.string.text_logout_header;

        int alertDialogDividerColorResId = getResources().getColor(R.color.colorAccent);

        int noTextResId     = R.string.text_no;
        int yesTextResId    = R.string.text_yes;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(   MainActivity.this,
                                                                            R.style.Theme_Sphinx_Dialog_Alert);
        alertDialogBuilder.setMessage(alertDialogMessageResId)
                .setCancelable(false)
                .setPositiveButton(yesTextResId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();
                    }
                })
                .setNegativeButton(noTextResId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(alertDialogHeaderResId);
        alertDialog.show();

        // Set title divider color
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog.findViewById(titleDividerId);

        if (titleDivider != null)
            titleDivider.setBackgroundColor(alertDialogDividerColorResId);
    }

//    public static class UserViewHolder extends RecyclerView.ViewHolder{
//
//        View            view;
//        TextView        userNameTextView;
//        RelativeLayout  userContainer;
//
//        public UserViewHolder(View itemView) {
//            super(itemView);
//
//            view                = itemView;
//            userContainer       = UiUtils.findView(view, R.id.userRowContainer);
//            userNameTextView    = UiUtils.findView(view, R.id.userRowName);
//        }
//
//        public void setUserName(String userName) {
//            userNameTextView.setText(userName);
//        }
//
//        public void hideItem() {
//            view.setVisibility(View.GONE);
//            userContainer.setVisibility(View.GONE);
//            userNameTextView.setVisibility(View.GONE);
//        }
//    }

    // ------------------------------ LISTENERS ----------------------------------------- //

//    ValueEventListener currentUserDataListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//            //Log.e("LOG", "MainActivity: currentUserDataListener: (dataSnapshot.hasChild(" +currentUserId+ "): " +(dataSnapshot.hasChild(currentUserId)));
//            //Log.e("LOG", "MainActivity: currentUserDataListener: dataSnapshot is null: " +(dataSnapshot  == null));
//
//            if(dataSnapshot == null) {
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                                                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//            }
//
//            /*if(dataSnapshot != null) { //dataSnapshot.hasChild(currentUserId)) {
//
//                currentUserFBDatabaseRef.child(CONST.USER_IS_ONLINE).setValue(true);
//            }
//            else {
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                                                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//            }*/
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) { }
//    };



//    FirebaseAuth.AuthStateListener fbAuthListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//            Log.e("LOG", "MainActivity: fbAuthListener: currentUser is null: " +(firebaseAuth.getCurrentUser() == null));
//
//            if(firebaseAuth.getCurrentUser() == null) {
//                startActivity(new Intent(   MainActivity.this,
//                                            LoginActivity.class));
//                finish();
//            }
//            /*else {
//                currentUserId = fbAuth.getCurrentUser().getUid();
//            }*/
//                /*else {
//
//                    String currentUser = firebaseAuth.getCurrentUser().getDisplayName();
//
//                    Log.e("LOG", "MainActivity: onAuthStateChanged(): currentUser: " +currentUser);
//                }*/
//        }
//    };

//    ChildEventListener childEventListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            User user = dataSnapshot.getValue(User.class);
//
//            userList.add(user.getUserName());
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onCancelled(FirebaseError firebaseError) {
//
//        }
//    };

//    ValueEventListener valueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//            Log.e("LOG", "LoginActivity: valueEventListener: (dataSnapshot.hasChild(" +currentUserId+ "): " +(dataSnapshot.hasChild(currentUserId)));
//
//            if(!dataSnapshot.hasChild(currentUserId)) {
//
//                Intent setAccountIntent = new Intent(   MainActivity.this,
//                                                        SetAccountActivity.class);
//                setAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(setAccountIntent);
//            }
//            else {
//
//                if(fbAuth.getCurrentUser() != null) {
//
//                    //currentUserId = fbAuth.getCurrentUser().getUid();
//
//                    usersFBDatabaseRef.child(currentUserId)
//                            .child(CONST.USER_IS_ONLINE).setValue(true);
//                }
//                else {
//
//                    Intent loginIntent = new Intent(MainActivity.this,
//                                                    LoginActivity.class);
//                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(loginIntent);
//                }
//            }
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) { }
//    };
}
