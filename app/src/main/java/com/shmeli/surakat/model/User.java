package com.shmeli.surakat.model;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class User {

    // private int userId;
    //private String userId;

    private String userImageUrl;
    private String userName;
    private String userDeviceToken;
    private String userStatus;
    private String userThumbImageUrl;

    private Boolean userIsOnline;

    //private String userEmail;

    // private boolean isUserOnline;
//    private String isUserOnline;

    public User() { }

    public User(String userImageUrl,
                String userName,
                String userDeviceToken,
                String userStatus,
                String userThumbImageUrl,
                Boolean userIsOnline) {

        this.userImageUrl       = userImageUrl;
        this.userName           = userName;
        this.userDeviceToken    = userDeviceToken;

        this.userStatus         = userStatus;
        this.userThumbImageUrl  = userThumbImageUrl;

        this.userIsOnline       = userIsOnline;

    }

    // ------------------------ GETTERS ----------------------------- //

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDeviceToken() {
        return userDeviceToken;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getUserThumbImageUrl() {
        return userThumbImageUrl;
    }

    public Boolean getUserIsOnline() {
        return userIsOnline;
    }

    // ------------------------ SETTERS ----------------------------- //

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserDeviceToken(String userDeviceToken) {
        this.userDeviceToken = userDeviceToken;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserThumbImageUrl(String userThumbImageUrl) {
        this.userThumbImageUrl = userThumbImageUrl;
    }

    public void setUserIsOnline(Boolean userIsOnline) {
        this.userIsOnline = userIsOnline;
    }

    //    public User(int     userId,
//                String  userName,
//                String  userEmail,
//                boolean isUserOnline) {

//    public User(String  userId,
//                String  userName,
//                String  userEmail,
//                String  isUserOnline) {

//        this.userId         = userId;
//        this.userName       = userName;
//        //this.userEmail      = userEmail;
//        this.isUserOnline   = isUserOnline;
//    }

//    public int getUserId() {
//        return userId;
//    }

//   public String getUserId() {
//        return userId;
//    }



//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

//    public String getUserEmail() {
//        return userEmail;
//    }
//
//    public void setUserEmail(String userEmail) {
//        this.userEmail = userEmail;
//    }

//    public String isUserOnline() {
//        return isUserOnline;
//    }
//
//    public void setUserOnline(String userOnline) {
//        isUserOnline = userOnline;
//    }

    //    public boolean isUserOnline() {
//        return isUserOnline;
//    }

//    public void setUserOnline(boolean userOnline) {
//        isUserOnline = userOnline;
//    }
}
