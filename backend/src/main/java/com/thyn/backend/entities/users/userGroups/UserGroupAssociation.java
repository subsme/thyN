package com.thyn.backend.entities.users.userGroups;

import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.entities.EntityObject;

/**
 * Created by shalu on 2/6/18.
 */

public class UserGroupAssociation extends EntityObject {
    @Index
    private Long userId;
    private String userName;

    @Index
    private Long groupId;
    private String groupName;

    @Index
    private boolean isGroupOwner;

    public UserGroupAssociation(Long userId, String userName, Long groupId, String groupName, boolean isGroupOwner){
        this.userId = userId;
        this.userName = userName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.isGroupOwner = isGroupOwner;
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public void setGroupOwner(boolean groupOwner) {
        isGroupOwner = groupOwner;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean dispose() { return true;}
    @Override
    public String getName(){ return null;}
}
