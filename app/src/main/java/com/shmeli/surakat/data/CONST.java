package com.shmeli.surakat.data;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class CONST {

    public static String FIREBASE_BASE_LINK         = "https://surakat-80b2e.firebaseio.com";

    public static String FIREBASE_MESSAGES_CHILD    = "Messages";
    public static String FIREBASE_MESSAGES_LINK     = FIREBASE_BASE_LINK + "/" + FIREBASE_MESSAGES_CHILD;

    public static String FIREBASE_USERS_CHILD       = "Users";
    public static String FIREBASE_USERS_LINK        = FIREBASE_BASE_LINK + "/" + FIREBASE_USERS_CHILD;

    public static final int PUBLICATION_MAX_LENGTH  = 400;
}
