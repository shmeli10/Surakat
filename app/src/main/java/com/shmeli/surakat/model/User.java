package com.shmeli.surakat.model;

/**
 * Created by Serghei Ostrovschi on 10/5/17.
 */

public class User {

    private int userId;

    private String userName;

    private String userEmail;

    private boolean isUserOnline;

    public User(int     userId,
                String  userName,
                String  userEmail,
                boolean isUserOnline) {

        this.userId         = userId;
        this.userName       = userName;
        this.userEmail      = userEmail;
        this.isUserOnline   = isUserOnline;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isUserOnline() {
        return isUserOnline;
    }

    public void setUserOnline(boolean userOnline) {
        isUserOnline = userOnline;
    }
}
