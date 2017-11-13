package com.shmeli.surakat.model;

/**
 * Created by Serghei Ostrovschi on 10/26/17.
 */

public class Friends {

    public String date;

    public Friends() { }

    public Friends(String date) {
        this.date = date;
    }

    // ---------------------------- GETTERS -------------------------- //

    public String getFriendshipStartDate() {
        return date;
    }

    // ---------------------------- SETTERS -------------------------- //

    public void setFriendshipStartDate(String FriendshipStartDate) {
        this.date = date;
    }
}
