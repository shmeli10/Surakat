package com.shmeli.surakat.utils;

import android.app.Activity;
import android.view.View;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class UiUtils {
    public static <T extends View> T findView(View root, int id)      {
        return (T) root.findViewById(id); }

    public static <T extends View> T findView(Activity activity, int id)      {
        return (T) activity.getWindow().getDecorView().getRootView().findViewById(id); }
}
