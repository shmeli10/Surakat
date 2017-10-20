package com.shmeli.surakat.data;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class CONST {

    public static final String FIREBASE_BASE_LINK               = "https://surakat-80b2e.firebaseio.com";

    public static final String FIREBASE_FRIENDS_CHILD           = "Friends";

    public static final String FIREBASE_MESSAGES_CHILD          = "Messages";
    public static final String FIREBASE_MESSAGES_LINK           = FIREBASE_BASE_LINK + "/" + FIREBASE_MESSAGES_CHILD;

    public static final String FIREBASE_FRIEND_REQUEST_CHILD    = "FriendRequests";

    public static final String FIREBASE_USERS_CHILD             = "Users";
    public static final String FIREBASE_USERS_LINK              = FIREBASE_BASE_LINK + "/" + FIREBASE_USERS_CHILD;

    public static final String REQUESTS_TAB_NAME                = "REQUESTS";
    public static final String CHATS_TAB_NAME                   = "CHATS";
    public static final String FRIENDS_TAB_NAME                 = "FRIENDS";

    public static final String USER_ONLINE_STATUS               = "online";
    public static final String USER_OFFLINE_STATUS              = "offline";

    public static final String DEFAULT_VALUE                    = "default";

    public static final String USER_ID                          = "userId";
    public static final String USER_IMAGE                       = "userImageUrl";
    public static final String USER_NAME                        = "userName";
    public static final String USER_STATUS                      = "userStatus";
    public static final String USER_THUMB_IMAGE                 = "userThumbImageUrl";

    public static final String REQUEST_TYPE_TEXT                = "requestTypeText";
    public static final String REQUEST_TYPE_ID                  = "requestTypeId";

    public static final String SENT_REQUEST                     = "sent";
    public static final String RECEIVED_REQUEST                 = "received";

    public static final int PUBLICATION_MAX_LENGTH              = 400;

    public static final int IS_A_FRIEND_STATE                   = 3;
    public static final int IS_NOT_A_FRIEND_STATE               = 0;
    public static final int RECEIVED_REQUEST_STATE              = 2;
    public static final int SENT_REQUEST_STATE                  = 1;

}
