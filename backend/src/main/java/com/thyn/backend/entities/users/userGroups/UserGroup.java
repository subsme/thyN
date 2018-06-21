package com.thyn.backend.entities.users.userGroups;

import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.entities.EntityObject;

import java.util.Date;

/**
 * Created by shalu on 2/6/18.
 */

public class UserGroup extends EntityObject {
    @Index
    private String groupName;
    private Date groupStartDate;
    private Date groupEndDate;

    public UserGroup(String groupName){
        this.groupName = groupName;
    }
    public UserGroup(String groupName, Date groupStartDate, Date groupEndDate){
        this.groupName = groupName;
        this.groupStartDate = groupStartDate;
        this.groupEndDate = groupEndDate;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupStartDate(Date groupStartDate) {
        this.groupStartDate = groupStartDate;
    }

    public void setGroupEndDate(Date groupEndDate) {
        this.groupEndDate = groupEndDate;
    }

    public String getGroupName() {
        return groupName;
    }

    public Date getGroupStartDate() {
        return groupStartDate;
    }

    public Date getGroupEndDate() {
        return groupEndDate;
    }

    @Override
    public boolean dispose() { return true;}
    @Override
    public String getName(){ return null;}

}
