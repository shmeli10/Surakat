package com.shmeli.surakat.ui.new_version.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shmeli.surakat.R;
import com.shmeli.surakat.adapters.MainPageViewPagerAdapter;
import com.shmeli.surakat.adapters.ViewPagerAdapter;

import com.shmeli.surakat.ui.new_version.InternalActivity;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/16/17.
 */
public class TabsFragment extends ParentFragment {

    private static TabsFragment  instance;

    private View                view;

    private TabLayout           tabLayout;

    private ViewPager           viewPager;
    private ViewPagerAdapter    viewPagerAdapter;

    private InternalActivity    internalActivity;

    public TabsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabsFragment.
     */
    public static TabsFragment newInstance() {
        Bundle args = new Bundle();

        if(instance == null) {
            instance = new TabsFragment();
            instance.setArguments(args);
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {

        Log.e("LOG", "TabsFragment: onCreateView()");

        view = inflater.inflate(R.layout.fragment_tabs,
                                container,
                                false);

        internalActivity    = (InternalActivity) getActivity();

        viewPagerAdapter    = new ViewPagerAdapter(internalActivity.getSupportFragmentManager());

        viewPager           = UiUtils.findView( view,
                                                R.id.tabsFragmentViewPager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout           = UiUtils.findView( view,
                                                R.id.tabsFragmentTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
