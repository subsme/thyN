package com.thyn.backend.entities.users.communication;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.EntityObject;

import java.util.Date;

/**
 * Created by Subu Sundaram on 12/14/16.
 */
@Entity
public class Message extends EntityObject {
    @Index
    private Long taskID;
    @Index
    private Date messageTime;
    @Index
    private Long userKey;
    @Index
    private Boolean notification;

    private String content;

    public static Message createNewMessage(Long taskID, Long userKey, Boolean notification, String content, Date messageTime) {
        Message message = new Message(taskID, userKey, notification, content, messageTime);
        if(DatastoreHelpers.trySaveEntityOnDatastore(message) != null)
            return message;
        return null;
    }

    private Message(){}


    private Message(Long taskID, Long userKey, Boolean notification, String content, Date messageTime) {
        this.taskID = taskID;
        this.userKey = userKey;
        this.notification = notification;
        this.content = content;
        this.messageTime = messageTime;
    }

    public Long getTaskID() {
        return taskID;
    }

    public void setTaskID(Long taskID) {
        this.taskID = taskID;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean dispose() {
        return DatastoreHelpers.removeEntityFromDatastore(this);
    }
}

