package com.thyn.backend.entities;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.UUID;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.Ref;
import com.thyn.backend.entities.users.Profile;
/**
 * Created by subu sundaram on 3/2/16.
 */
@Entity
public class MyTask extends EntityObject{

    private String mBeginLocation;
    private String mEndLocation;
    private String mServiceDate;
    private String mCreateDate;
    private String mTaskDescription;
    private int mWaitResponseTime;
    @Index
    private boolean isSolved;
    @Index
    private Long userProfileKey;
    private String userProfileName;
    @Index
    private Long helperUserProfileKey;

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public Long getHelperUserProfileKey() {
        return helperUserProfileKey;
    }

    public void setHelperUserProfileKey(Long helperUserProfileKey) {
        this.helperUserProfileKey = helperUserProfileKey;
    }

    public Long getUserProfileKey() {
        return userProfileKey;
    }

    public void setUserProfileKey(Long userKey) {
        this.userProfileKey = userKey;
    }


    public boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public String getBeginLocation() {
        return mBeginLocation;
    }


    public void setBeginLocation(String mBeginLocation) {
        this.mBeginLocation = mBeginLocation;
    }

    public String getEndLocation() {
        return mEndLocation;
    }

    public void setEndLocation(String mEndLocation) {
        this.mEndLocation = mEndLocation;
    }

    public String getServiceDate() {
        return mServiceDate;
    }


    public void setServiceDate(String sDate) throws ParseException{
        this.mServiceDate = sDate;
    }

    public String getTaskDescription() {
        return mTaskDescription;
    }

    public void setTaskDescription(String mTaskDescription) {
        this.mTaskDescription = mTaskDescription;
    }

    public int getWaitResponseTime() {
        return mWaitResponseTime;
    }

    public void setWaitResponseTime(int mWaitResponseTime) {
        this.mWaitResponseTime = mWaitResponseTime;
    }
    @Override
    public boolean dispose() { return true;}
    @Override
    public String getName(){ return null;}

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String mCreateDate) {
        this.mCreateDate = mCreateDate;
    }
}
