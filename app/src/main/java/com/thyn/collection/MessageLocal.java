package com.thyn.collection;

import java.io.Serializable;
/**
 * Created by shalu on 12/16/16.
 */
public class MessageLocal implements Serializable {
    Long id;
    String message, createdAt;
    String user;
    Long userKey;
    Long createdAtInMillis;

    public MessageLocal() {
    }

    public MessageLocal(Long id, String message, String createdAt, String user, Long userkey) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.userKey = userKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getCreatedAtInMillis() {
        return createdAtInMillis;
    }

    public void setCreatedAtInMillis(Long createdAtInMillis) {
        this.createdAtInMillis = createdAtInMillis;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }
}
