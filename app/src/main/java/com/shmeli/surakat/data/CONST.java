package com.shmeli.surakat.data;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class CONST {

    public static final String FIREBASE_BASE_LINK               = "https://surakat-80b2e.firebaseio.com";

    public static final String FIREBASE_PROJECT_ID              = "surakat-80b2e";

    public static final String FCM_URL                          = "https://fcm.googleapis.com/v1/projects/" +FIREBASE_PROJECT_ID+ "/";
    public static final String SEND_MESSAGE_URL                 = "messages:send"; // " HTTP/1.1";

    public static final String HTTP_SCHEME                      = "http";
    public static final String HTTPS_SCHEME                     = "https";

    //public static final String FIREBASE_CHAT_CHILD              = "Chat";

    public static final String FIREBASE_FRIENDS_CHILD           = "Friends";

    public static final String FIREBASE_MESSAGES_CHILD          = "Messages";
    //public static final String FIREBASE_MESSAGES_LINK           = FIREBASE_BASE_LINK + "/" + FIREBASE_MESSAGES_CHILD;

    public static final String FIREBASE_NOTIFICATIONS_CHILD     = "Notifications";

    public static final String FIREBASE_FRIEND_REQUEST_CHILD    = "FriendRequests";

    public static final String FIREBASE_USERS_CHILD             = "Users";
    public static final String FIREBASE_USERS_LINK              = FIREBASE_BASE_LINK + "/" + FIREBASE_USERS_CHILD;

//    public static final String REQUESTS_TAB_NAME                = "REQUESTS";
//    public static final String CHATS_TAB_NAME                   = "CHATS";
    public static final String ALL_USERS_TAB_NAME               = "ALL USERS";
    public static final String FRIENDS_TAB_NAME                 = "FRIENDS";

    public static final String USER_ONLINE_STATUS               = "online";
    public static final String USER_OFFLINE_STATUS              = "offline";

    public static final String DEFAULT_VALUE                    = "default";

//    public static final String DATE_TEXT                        = "date";
    public static final String FRIENDSHIP_START_DATE            = "friendshipStartDate";

    public static final String NOTIFICATION_ID                  = "notificationId";
    public static final String NOTIFICATION_SENDER_ID           = "senderId";
    public static final String NOTIFICATION_TYPE                = "type";

    public static final String NOTIFICATION_REQUEST_TYPE        = "request";

    public static final String CHAT_SEEN                        = "seen";
    public static final String CHAT_TIMESTAMP                   = "timeStamp";
    public static final String CHAT_KEY                         = "Chat/";

    public static final String MESSAGES_KEY                     = FIREBASE_MESSAGES_CHILD + "/";
    public static final String MESSAGE_TEXT                     = "messageText";
    public static final String MESSAGE_IS_SEEN                  = "messageIsSeen";
    public static final String MESSAGE_TYPE                     = "messageType";
    public static final String MESSAGE_CREATE_TIME              = "messageCreateTime";
    public static final String MESSAGE_AUTHOR_ID                = "messageAuthorId";

    public static final String MESSAGE_TEXT_TYPE                = "text";

    public static final String USER_DEVICE_TOKEN                = "userDeviceToken";
    public static final String USER_ID                          = "userId";
    public static final String USER_IMAGE                       = "userImageUrl";
    public static final String USER_IS_ONLINE                   = "userIsOnline";
    public static final String USER_LAST_SEEN                   = "userLastSeen";
    public static final String USER_NAME                        = "userName";

    public static final String USER_STATUS                      = "userStatus";
    public static final String USER_THUMB_IMAGE                 = "userThumbImageUrl";

    public static final String NO_DATE_TEXT                     = "No date";
    public static final String NO_NAME_TEXT                     = "No name";
    public static final String NO_STATUS_TEXT                   = "No status";
    public static final String REQUEST_TYPE_TEXT                = "requestTypeText";
    public static final String REQUEST_TYPE_ID                  = "requestTypeId";

    public static final String SENT_REQUEST_TEXT                = "sent";
    public static final String RECEIVED_REQUEST_TEXT            = "received";

    public static final int USER_STATUS_MAX_LENGTH              = 50;
    public static final int PUBLICATION_MAX_LENGTH              = 400;

    public static final int IS_A_FRIEND_STATE                   = 3;
    public static final int IS_NOT_A_FRIEND_STATE               = 0;
    public static final int RECEIVED_REQUEST_STATE              = 2;
    public static final int SENT_REQUEST_STATE                  = 1;

    public static final int OPEN_PROFILE_TYPE                   = 0;
    public static final int SEND_MESSAGE_TYPE                   = 1;

    public static final int LOAD_MESSAGES_COUNT                 = 5;

    public static final int CONNECT_TIMEOUT                     = 15;
    public static final int WRITE_TIMEOUT                       = 15;
    public static final int READ_TIMEOUT                        = 15;

    //////////////////////////////////////////////////////////////////////////////

    public static final int TABS_COUNT                          = 2;

    public static final int ALL_USERS_TAB_ID                    = 1;
    public static final int FRIENDS_TAB_ID                      = 0;

    //////////////////////////////////////////////////////////////////////////////

    public static final int MESSAGE_MARGIN_PX                   = 25;

    //////////////////////////////////////////////////////////////////////////////

//    public static final String CHAT_FRAGMENT_NAME               = "CHAT_FRAGMENT";
//    public static final String REGISTER_FRAGMENT_NAME           = "REGISTER_FRAGMENT";
//    public static final String SIGN_IN_FRAGMENT_NAME            = "SIGN_IN_FRAGMENT";
//    public static final String TABS_FRAGMENT_NAME               = "TABS_FRAGMENT";
//    public static final String USER_PROFILE_FRAGMENT_NAME       = "USER_PROFILE_FRAGMENT";

    public static final int CHAT_FRAGMENT_CODE                  = 1000;
    public static final int REGISTER_FRAGMENT_CODE              = 1001;
    public static final int SETTINGS_FRAGMENT_CODE              = 1002;
    public static final int SIGN_IN_FRAGMENT_CODE               = 1003;
    public static final int TABS_FRAGMENT_CODE                  = 1004;
    public static final int USER_PROFILE_FRAGMENT_CODE          = 1005;
    public static final int USER_STATUS_FRAGMENT_CODE           = 1006;

//    public static final int FILL_ACCOUNT_FRAGMENT               = 1002;

}
