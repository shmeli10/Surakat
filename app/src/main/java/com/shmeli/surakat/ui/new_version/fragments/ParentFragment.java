package com.shmeli.surakat.ui.new_version.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.support.annotation.Nullable;

/**
 * Created by Serghei Ostrovschi on 11/15/17.
 */

public abstract class ParentFragment extends Fragment {

    public int fragmentCode;

    public int fragmentTitleResId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retain this fragment
        setRetainInstance(true);
    }

    // ---------------------------------- GETTERS --------------------------------------------//

    public int getFragmentCode() {
        return fragmentCode;
    }

    public int getFragmentTitleResId() {
        return fragmentTitleResId;
    }

    // ---------------------------------- SETTERS --------------------------------------------//

    public void setFragmentCode(int fragmentCode) {
        this.fragmentCode = fragmentCode;
    }

    public void setFragmentTitleResId(int fragmentTitleResId) {
        this.fragmentTitleResId = fragmentTitleResId;
    }
}
