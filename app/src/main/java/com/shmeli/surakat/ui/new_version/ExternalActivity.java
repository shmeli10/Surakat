package com.shmeli.surakat.ui.new_version;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.shmeli.surakat.R;
import com.shmeli.surakat.ui.new_version.fragments.SignInFragment;
import com.shmeli.surakat.utils.UiUtils;

public class ExternalActivity   extends     ParentActivity
                                implements  FragmentManager.OnBackStackChangedListener {

    private RelativeLayout  rootContainer;

    private int             fragmentsContainerResId;

    private ActionBar       actionBar;
    private Toolbar         toolbar;

    private ProgressDialog  progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external);

        Log.e("LOG", "ExternalActivity: onCreate()");

        init();

        getFragmentManager().addOnBackStackChangedListener(this);

    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
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

        transaction.replace(fragmentsContainerResId,
                            fragment,
                            fragment.getClass().getName());

        if(addToBackStack)
            transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();
    }

    @Override
    public void changeFragment(Fragment fragment) {

    }

    private void init() {
        Log.e("LOG", "ExternalActivity: init()");

        rootContainer   = UiUtils.findView( this,
                                            R.id.externalActivityContainer);

        fragmentsContainerResId = R.id.externalActivityFragmentsContainer;

        toolbar         = UiUtils.findView( this,
                                            R.id.externalActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("External Activity");

        progressDialog  = new ProgressDialog(   this,
                                                R.style.Theme_Sphinx_Dialog_Alert);

        setFragment(SignInFragment.newInstance(),
                    false,
                    false);
    }


}
