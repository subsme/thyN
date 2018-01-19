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
    private String taskName;
    @Index
    private Date messageTime;
    @Index
    private Long userKey;

    private String userImageURL;
    private Long userToKey;
    private String userToImageURL;
    @Index
    private Boolean notification;

    private String content;

    private String userFromName;

    private String userToName;

    public static Message createNewMessage(Long taskID, String taskName, Long userKey, String userFromName, String userImageURL, Long userToKey, String userToName, String userToImageURL, Boolean notification, String content, Date messageTime) {
        Message message = new Message(taskID, taskName, userKey, userFromName, userImageURL, userToKey, userToName, userToImageURL, notification, content, messageTime);
        if(DatastoreHelpers.trySaveEntityOnDatastore(message) != null)
            return message;
        return null;
    }

    private Message(){}


    private Message(Long taskID, String taskName, Long userKey, String userFromName, String userImageURL, Long userToKey, String userToName, String userToImageURL, Boolean notification, String content, Date messageTime) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.userKey = userKey;
        this.userFromName = userFromName;
        this.userImageURL = userImageURL;
        this.userToKey = userToKey;
        this.userToName = userToName;
        this.userToImageURL = userToImageURL;
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

    public Long getUserToKey() {
        return userToKey;
    }

    public void setUserToKey(Long userToKey) {
        this.userToKey = userToKey;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserToImageURL() {
        return userToImageURL;
    }

    public void setUserToImageURL(String userToImageURL) {
        this.userToImageURL = userToImageURL;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setUserFromName(String userFromName) {
        this.userFromName = userFromName;
    }

    public String getUserToName() {
        return userToName;
    }

    public void setUserToName(String userToName) {
        this.userToName = userToName;
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

