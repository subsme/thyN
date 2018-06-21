package com.thyn.backend.entities;

import java.text.ParseException;
import java.util.Date;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.users.Profile;

/**
 * Created by subu sundaram on 3/2/16.
 */
@Entity
public class MyTask extends EntityObject{

    private Long taskId;
    private String mBeginLocation;
    private double mLAT;
    private double mLONG;
    private String mCity;
    private String mEndLocation;
    @Index
    private Date mServiceDate;
    private Date mServiceToDate;
    private String mServiceTimeRange;
    @Index
    private Date mCreateDate;
    private Date mUpdateDate;
    private String mTaskTitle;
    private String mTaskDescription;
    private int mWaitResponseTime;
    @Index
    private boolean isSolved;
    @Index
    private Long userProfileKey;
    private String userProfileName;
    @Index
    private Long helperUserProfileKey;
    private String helperProfileName;
    private String imageURL;
    @Ignore
    private double distanceFromOrigin;

    @Index
    private Long groupId;
    private String groupName;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setServiceToDate(Date mServiceToDate) {
        this.mServiceToDate = mServiceToDate;
    }

    public String getTaskTitle() {
        return mTaskTitle;
    }

    public void setTaskTitle(String mTaskTitle) {
        this.mTaskTitle = mTaskTitle;
    }
    public String getHelperProfileName() {
        return helperProfileName;
    }

    public void setHelperProfileName(String helperProfileName) {
        this.helperProfileName = helperProfileName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

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

    public Date getServiceDate() {
        return mServiceDate;
    }


    public void setServiceDate(Date sDate) throws ParseException{
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
    public boolean dispose() {

        return DatastoreHelpers.removeEntityFromDatastore(this);
    }
    @Override
    public String getName(){ return null;}


    public Date getServiceToDate() {
        return mServiceToDate;
    }

    public Date getCreateDate() {
        return mCreateDate;

    }

    public void setCreateDate(Date mCreateDate) {
        this.mCreateDate = mCreateDate;
    }
    public Date getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(Date mUpdateDate) {
        this.mUpdateDate = mUpdateDate;
    }

    public String getServiceTimeRange() {
        return mServiceTimeRange;
    }

    public void setServiceTimeRange(String mServiceTimeRange) {
        this.mServiceTimeRange = mServiceTimeRange;
    }

    public double getLONG() {
        return mLONG;
    }

    public void setLONG(double mLONG) {
        this.mLONG = mLONG;
    }

    public double getLAT() {
        return mLAT;
    }

    public void setLAT(double mLAT) {
        this.mLAT = mLAT;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public double getDistanceFromOrigin() {
        return distanceFromOrigin;
    }

    public void setDistanceFromOrigin(double distanceFromOrigin) {
        this.distanceFromOrigin = distanceFromOrigin;
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
}
