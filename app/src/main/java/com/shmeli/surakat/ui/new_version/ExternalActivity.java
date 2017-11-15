package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;
import com.shmeli.surakat.utils.UiUtils;

public class ExternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

    private RelativeLayout  rootContainer;

//    private ActionBar       actionBar;
    private Toolbar         toolbar;

    private TextView        toolbarTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external);

        Log.e("LOG", "ExternalActivity: onCreate()");

        rootContainer   = UiUtils.findView( this,
                                            R.id.externalActivityContainer);

        toolbar         = UiUtils.findView( this,
                                            R.id.externalActivityToolbar);
//        setSupportActionBar(toolbar);

        toolbarTitleTextView = UiUtils.findView(toolbar,
                                                R.id.appToolbarTitleTextView);

//        actionBar       = getSupportActionBar();

        init();
    }

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "ExternalActivity: onBackStackChanged()");
    }

/*    @Override
    public void setFragment(Fragment    fragment,
                            boolean     animate,
                            boolean     addToBackStack) {

        Log.e("LOG", "ExternalActivity: setFragment()");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (animate) {
            transaction.setCustomAnimations(R.animator.slide_in,
                                            R.animator.slide_out,
                                            R.animator.slide_in,
                                            R.animator.slide_out);
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        transaction.replace(R.id.externalActivityFragmentsContainer,
                            fragment,
                            fragment.getClass().getName());

        if(addToBackStack)
            transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();

        //setToolbarTitle("External Activity");
    }*/

    @Override
    public void setToolbarTitle(int titleResId) {
        Log.e("LOG", "ExternalActivity: setToolbarTitle()");

//        if((actionBar != null) && (titleResId > 0)) {//(!TextUtils.isEmpty(title))) {
//            actionBar.setTitle(titleResId);
//        }

        if(titleResId > 0) {
            toolbarTitleTextView.setText(titleResId);
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {

    }

    private void init() {
        Log.e("LOG", "ExternalActivity: init()");

        getFragmentManager().addOnBackStackChangedListener(this);

        setFragment(SignInFragment.newInstance(),
                    false,
                    false);
    }

    public void moveToInternalActivity() {
        Log.e("LOG", "ExternalActivity: moveToInternalActivity()");

        Intent internalActivityIntent = new Intent( ExternalActivity.this,
                                                    InternalActivity.class);
        internalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(internalActivityIntent);

        finish();

    }

}
