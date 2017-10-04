package com.shmeli.surakat;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Serghei Ostrovschi on 10/4/17.
 */

public class SurakatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
