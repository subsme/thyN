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

    //private Long mTaskId;
    private String mBeginLocation;
    private double mLAT;
    private double mLONG;
    private String mCity;
    private String mEndLocation;
    private Date mServiceDate;
    private Date mCreateDate;
    private Date mUpdateDate;
    private Calendar mTaskDate; // we need to get rid of this member variable because we have mTaskFromDate and mTaskToDate. Dont know why i have a calendar variable instead of a date variable.
    private Date mTaskFromDate;
    private Date mTaskToDate;
    private String mTaskTimeRange;

    private String mTaskTitle;
    private String mTaskDescription;
    private int mWaitResponseTime;
    private boolean isSolved;
    private boolean isAccepted;
    private String userProfileName;
    private Long userProfileKey;
    private Long mHelperProfileKey;
    private String helperProfileName;
    private String imageURL;
    private double distance;

    public static int DISPLAY_DATE=0;



    public static int DISPLAY_TIME=1;
    public static int DISPLAY_DATE_TIME=2;

    public Task(String title, String description, Date from, Date to, String time, String beginLocation, double latitude, double longitude, String city){
        this.mTaskTitle = title;
        this.mTaskDescription = description;
        this.mTaskFromDate = from;
        this.mTaskToDate = to;
        this.mTaskTimeRange = time;
        this.mBeginLocation = beginLocation;
        this.mLAT = latitude;
        this.mLONG = longitude;
        this.mCity = city;
    }

    public void setTaskToDate(Date mTaskToDate) {
        this.mTaskToDate = mTaskToDate;
    }
    public Date getTaskToDate() {
        return mTaskToDate;
    }

    public String getHelperProfileName() {
        return helperProfileName;
    }

    public void setHelperProfileName(String helperProfileName) {
        this.helperProfileName = helperProfileName;
    }

    public void setHelperProfileKey(Long helperProfileKey) {
        this.mHelperProfileKey = helperProfileKey;
    }

    public Long getHelperProfileKey() {
        return mHelperProfileKey;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public Task(){
        //Generate unique identifier
        //mTaskId = UUID.randomUUID().getMostSignificantBits();
    }

   // public void setTaskId(Long id){this.mTaskId = id;}

    public Date getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(Date mUpdateDate) {
        this.mUpdateDate = mUpdateDate;
    }

    public Date getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(Date mCreateDate) {
        this.mCreateDate = mCreateDate;
    }

  /*  public Long getTaskId() {
        return mTaskId;

    }*/

    public Long getId() {
        return mId;
    }

    public void setId(Long mId) {
        this.mId = mId;
    }

    public String getTaskTitle() {
        return mTaskTitle;
    }

    public void setTaskTitle(String mTaskTitle) {
        this.mTaskTitle = mTaskTitle;
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
    public Date getTaskFromDate() {
        return mTaskFromDate;
    }

    public void setTaskFromDate(Date mTaskDate) {
        this.mTaskFromDate = mTaskDate;
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

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getDateReadableFormat(){
        Date currentDate = new Date();
        long duration  = currentDate.getTime() - mCreateDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if(diffInMinutes < 60) return diffInMinutes +  "Min AGO";
        else if(diffInMinutes/60 < 2) return diffInMinutes/60 + "H AGO";
        else if(diffInMinutes/60 < 24) return diffInMinutes/60 + "H AGO";
        else if(diffInMinutes/(60*24) < 2) return diffInMinutes/(60*24) + "D AGO";
        return diffInMinutes/(60*24) + "D AGO";
        //return (String)android.text.format.DateFormat.format("EEE, MMM d", mCreateDate);

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

    public String getTaskTimeRange() {
        return mTaskTimeRange;
    }

    public void setTaskTimeRange(String mTaskTimeRange) {
        this.mTaskTimeRange = mTaskTimeRange;
    }

    public double getLAT() {
        return mLAT;
    }

    public void setLAT(double mLAT) {
        this.mLAT = mLAT;
    }

    public double getLONG() {
        return mLONG;
    }

    public void setLONG(double mLONG) {
        this.mLONG = mLONG;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
