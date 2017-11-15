package com.shmeli.surakat.ui.new_version.fragments;

import android.app.Fragment;

/**
 * Created by Serghei Ostrovschi on 11/15/17.
 */

public abstract class ParentFragment extends Fragment {

    public int fragmentCode;

    public int fragmentTitleResId;

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
