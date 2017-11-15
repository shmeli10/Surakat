package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.shmeli.surakat.R;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/14/17.
 */

public class InternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

    private RelativeLayout  rootContainer;

    private ActionBar       actionBar;
    private Toolbar         toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        Log.e("LOG", "InternalActivity: onCreate()");

        rootContainer   = UiUtils.findView( this,
                                            R.id.internalActivityContainer);

        toolbar         = UiUtils.findView( this,
                                            R.id.internalActivityToolbar);
        setSupportActionBar(toolbar);

        actionBar       = getSupportActionBar();

        init();
    }

    @Override
    public void onBackStackChanged() {
        Log.e("LOG", "InternalActivity: onBackStackChanged()");
    }

/*    @Override
    public void setFragment(Fragment fragment,
                            boolean     animate,
                            boolean     addToBackStack) {

        Log.e("LOG", "InternalActivity: setFragment()");

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
    public void setFragment(int fragmentCode, boolean animate, boolean addToBackStack) {

    }

    @Override
    public void setToolbarTitle(int titleResId) {
        Log.e("LOG", "InternalActivity: setToolbarTitle()");

        if((actionBar != null) && (titleResId > 0)) { //(!TextUtils.isEmpty(title))) {
            actionBar.setTitle(titleResId);
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {

    }

    private void init() {
        Log.e("LOG", "InternalActivity: init()");

        getFragmentManager().addOnBackStackChangedListener(this);

//        setFragment(SignInFragment.newInstance(),
//                false,
//                false);
    }

    public void moveToExternalActivity() {
        Log.e("LOG", "InternalActivity: moveToExternalActivity()");

        Intent internalActivityIntent = new Intent( InternalActivity.this,
                                                    ExternalActivity.class);
        internalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(internalActivityIntent);

        finish();

    }
}
