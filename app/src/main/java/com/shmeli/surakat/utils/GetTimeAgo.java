package com.shmeli.surakat.utils;

import android.app.Application;
import android.content.Context;

import com.shmeli.surakat.R;

/**
 * Created by Serghei Ostrovschi on 11/6/17.
 */

public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS  = 1000;
    private static final int MINUTE_MILLIS  = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS    = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS     = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long    time,
                                    Context context) {

        //String result = "";

        StringBuilder messageSB = new StringBuilder();

        if(time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if(time > now || time <= 0) {
            return null;
        }

        final long diff = (now - time);

        if(diff < MINUTE_MILLIS) {
            messageSB.append(context.getResources().getString(R.string.text_just_now));
        }
        else if(diff < (2 * MINUTE_MILLIS)) {
            messageSB.append(context.getResources().getString(R.string.text_minute_ago));
        }
        else if(diff < (50 * MINUTE_MILLIS)) {
            messageSB.append(diff / MINUTE_MILLIS);
            messageSB.append(" ");
            messageSB.append(context.getResources().getString(R.string.text_minutes_ago));
        }
        else if(diff < (90 * MINUTE_MILLIS)) {
            messageSB.append(context.getResources().getString(R.string.text_hour_ago));
        }
        else if(diff < (24 * HOUR_MILLIS)) {
            messageSB.append(diff / HOUR_MILLIS);
            messageSB.append(" ");
            messageSB.append(context.getResources().getString(R.string.text_hours_ago));
        }
        else if(diff < (48 * HOUR_MILLIS)) {
            messageSB.append(context.getResources().getString(R.string.text_yesterday));
        }
        else {
            messageSB.append(diff / DAY_MILLIS);
            messageSB.append(" ");
            messageSB.append(context.getResources().getString(R.string.text_days_ago));
        }

        return messageSB.toString();
    }
}
