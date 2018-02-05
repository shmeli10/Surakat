package com.shmeli.surakat.ui.new_version.fragments;

import android.text.TextUtils;

/**
 * Created by Serghei Ostrovschi on 2/5/18.
 */

public abstract class FragmentWithInfoInToolbar extends ParentFragment {

    private int fragmentInfoHeadResId;
    private int fragmentInfoBodyResId;

    private String fragmentInfoBodyText = "";

    // ---------------------------------- GETTERS --------------------------------------------//

    public int getFragmentInfoHeadResId() {
        return fragmentInfoHeadResId;
    }

    public int getFragmentInfoBodyResId() {
        return fragmentInfoBodyResId;
    }

    public String getFragmentInfoBodyText() {
        return fragmentInfoBodyText;
    }

    // ---------------------------------- SETTERS --------------------------------------------//

    public void setFragmentInfoHeadResId(int fragmentInfoHeadResId) {
        if(fragmentInfoHeadResId >= 0)
            this.fragmentInfoHeadResId = fragmentInfoHeadResId;
    }

    public void setFragmentInfoBodyResId(int fragmentInfoBodyResId) {
        if(fragmentInfoBodyResId >= 0)
            this.fragmentInfoBodyResId = fragmentInfoBodyResId;
    }

    public void setFragmentInfoBodyText(String fragmentInfoBodyText) {
        if(!TextUtils.isEmpty(fragmentInfoBodyText))
            this.fragmentInfoBodyText = fragmentInfoBodyText;
    }
}
