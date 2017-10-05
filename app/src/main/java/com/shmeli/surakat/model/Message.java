package com.shmeli.surakat.model;

import java.util.Date;

/**
 * Created by Serghei Ostrovschi on 10/4/17.
 */

public class Message {

    private int messageId;
    private int messageAuthorId;
    private int messageRecipientId;

    private long messageDateAndTime;

    private String messageText;

    private boolean isMessageUnread;

    public Message(int      messageId,
                   int      messageAuthorId,
                   int      messageRecipientId,
                   String   messageText,
                   boolean  isMessageUnread) {

        this.messageId          = messageId;
        this.messageAuthorId    = messageAuthorId;
        this.messageRecipientId = messageRecipientId;

        this.messageText        = messageText;
        this.isMessageUnread    = isMessageUnread;

        this.messageDateAndTime = new Date().getTime();
    }

    public int getMessageId() {
        return messageId;
    }

    public int getMessageAuthorId() {
        return messageAuthorId;
    }

    public int getMessageRecipientId() {
        return messageRecipientId;
    }

    public long getMessageDateAndTime() {
        return messageDateAndTime;
    }

    public void setMessageDateAndTime(long messageDateAndTime) {
        this.messageDateAndTime = messageDateAndTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isMessageUnread() {
        return isMessageUnread;
    }

    public void setMessageUnread(boolean messageUnread) {
        isMessageUnread = messageUnread;
    }
}
