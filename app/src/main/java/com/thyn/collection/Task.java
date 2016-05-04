package com.thyn.collection;

import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 * Created by shalu on 2/22/16.
 */

public class Task {
    private Long mId;
    private String mBeginLocation;
    private String mEndLocation;
    private Date mServiceDate;
    private Date mCreateDate;
    private Calendar mTaskDate;
    private String mTaskDescription;
    private int mWaitResponseTime;
    private boolean isSolved;
    private boolean isAccepted;



    private String userProfileName;
    private Long userProfileKey;


    public static int DISPLAY_DATE=0;
    public static int DISPLAY_TIME=1;
    public static int DISPLAY_DATE_TIME=2;


    public Task(){
        //Generate unique identifier
        mId = UUID.randomUUID().getMostSignificantBits();
    }
    public void setId(Long id){this.mId = id;}

    public Date getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(Date mCreateDate) {
        this.mCreateDate = mCreateDate;
    }

    public Long getId() {
        return mId;

    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public String getBeginLocation() {
        return mBeginLocation;
    }

    public String getBeginCityandState() {
        String[] splitString =  mBeginLocation.split("\\s");
        int count = splitString.length;
        return splitString[count-1] + splitString[count];

    }
    public Calendar getTaskDate() {
        return mTaskDate;
    }

    public void setTaskDate(Calendar mTaskDate) {
        this.mTaskDate = mTaskDate;
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


    public String getDateReadableFormat(){
        Date currentDate = new Date();
        long duration  = currentDate.getTime() - mCreateDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if(diffInMinutes < 60) return diffInMinutes +  " mins ago";
        else if(diffInMinutes/60 < 2) return diffInMinutes/60 + " hour ago";
        else if(diffInMinutes/60 < 24) return diffInMinutes/60 + " hours ago";
        else if(diffInMinutes/(60*24) < 2) return diffInMinutes/(60*24) + " day ago";
        else if(diffInMinutes/(60*24) < 30) return diffInMinutes/(60*24) + " days ago";
        return (String)android.text.format.DateFormat.format("EEE, MMM d", mCreateDate);

    }

    public String getDateReadableFormat(int type, Date d){
        if(type == Task.DISPLAY_DATE) return (String)android.text.format.DateFormat.format("EEE, MMM d", d);
        else if(type == Task.DISPLAY_TIME) return (String)android.text.format.DateFormat.format("h:mm a", d);
        else return (String)android.text.format.DateFormat.format("EEE, MMM d h:mm a", d);
    }

    public void setServiceDate(Date mDate) {
        this.mServiceDate = mDate;


    }

    public String getTaskDescription() {
        return mTaskDescription;
    }

    public void setTaskDescription(String mTaskDescription) {
        this.mTaskDescription = mTaskDescription;
    }

    public int getmWaitResponseTime() {
        return mWaitResponseTime;
    }

    public void setWaitResponseTime(int mWaitResponseTime) {
        this.mWaitResponseTime = mWaitResponseTime;
    }

    public String toString(){
        int len = mTaskDescription.length();
        return len > 40?mTaskDescription.substring(0,len-3)+"...":mTaskDescription;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }


    public Long getUserProfileKey() {
        return userProfileKey;
    }

    public void setUserProfileKey(Long userProfileKey) {
        this.userProfileKey = userProfileKey;
    }

}
