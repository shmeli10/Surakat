package com.shmeli.surakat.model;

import java.util.Date;

/**
 * Created by Serghei Ostrovschi on 10/4/17.
 */

public class Message {

//    private int messageId;
//    private int messageAuthorId;
//    private int messageRecipientId;
//
//    private long messageDateAndTime;
//
//    private String messageText;
//
//    private boolean isMessageUnread;


    private String messageId;
    private String messageAuthorId;
    private String messageAuthorName;
    private String messageRecipientId;

    private String messageDateAndTime;

    private String messageText;

    private String isMessageUnread;

//    public Message(int      messageId,
//                   int      messageAuthorId,
//                   int      messageRecipientId,
//                   String   messageText,
//                   boolean  isMessageUnread) {


    public Message() {}

//    public Message(String   messageId,
//                   String   messageAuthorId,
//                   String   messageRecipientId,
//                   String   messageText,
//                   String   isMessageUnread) {
//
//        this.messageId          = messageId;
//        this.messageAuthorId    = messageAuthorId;
//        this.messageRecipientId = messageRecipientId;
//
//        this.messageText        = messageText;
//        this.isMessageUnread    = isMessageUnread;
//
//        this.messageDateAndTime = "" +new Date().getTime();
//    }

//    public int getMessageId() {
//        return messageId;
//    }
//
//    public int getMessageAuthorId() {
//        return messageAuthorId;
//    }
//
//    public int getMessageRecipientId() {
//        return messageRecipientId;
//    }
//
//    public long getMessageDateAndTime() {
//        return messageDateAndTime;
//    }
//
//    public void setMessageDateAndTime(long messageDateAndTime) {
//        this.messageDateAndTime = messageDateAndTime;
//    }
//
//    public String getMessageText() {
//        return messageText;
//    }
//
//    public void setMessageText(String messageText) {
//        this.messageText = messageText;
//    }
//
//    public boolean isMessageUnread() {
//        return isMessageUnread;
//    }
//
//    public void setMessageUnread(boolean messageUnread) {
//        isMessageUnread = messageUnread;
//    }

    public String getMessageId() {
        return messageId;
    }

//    public void setMessageId(String messageId) {
//        this.messageId = messageId;
//    }

    public String getMessageAuthorId() {
        return messageAuthorId;
    }

//    public void setMessageAuthorId(String messageAuthorId) {
//        this.messageAuthorId = messageAuthorId;
//    }


    public String getMessageAuthorName() {
        return messageAuthorName;
    }

    public String getMessageRecipientId() {
        return messageRecipientId;
    }

//    public void setMessageRecipientId(String messageRecipientId) {
//        this.messageRecipientId = messageRecipientId;
//    }

    public String getMessageDateAndTime() {
        return messageDateAndTime;
    }

    public void setMessageDateAndTime(String messageDateAndTime) {
        this.messageDateAndTime = messageDateAndTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getIsMessageUnread() {
        return isMessageUnread;
    }

    public void setIsMessageUnread(String isMessageUnread) {
        this.isMessageUnread = isMessageUnread;
    }
}
