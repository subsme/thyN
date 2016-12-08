package com.thyn.backend.api;



/**
 * Created by shalu on 3/4/16.
 */
public class APIUserInformation {
    public Long profileID;
    public String name;
    public int numNeighbrsHelped;
    public int thyNPoints;
    public boolean basicprofileInfo;
    public String address;
    public String aptNo;
    public Double latitude;
    public Double longitude;
    public String city;
    public String phone;
    public APIGeneralResult result;


    public APIUserInformation(APIGeneralResult rslt){

        this.result = rslt;
        this.profileID = null;
        this.numNeighbrsHelped = -1;
        this.thyNPoints = -1;
        this.name = null;
        this.basicprofileInfo = false;
        this.aptNo = null;
        this.address = null;
        this.latitude = -1.0;
        this.longitude = -1.0;
        this.city = null;
        this.phone = null;
    }

    public APIUserInformation(APIGeneralResult rslt,
                              Long profileID,
                              String name,
                              int numNeighbrsHelped,
                              int thyNPoints,
                              boolean basicprofileInfo,
                              String address,
                              String aptNo,
                              Double latitude,
                              Double longitude,
                              String city,
                              String phone
    ){
        this.result = rslt;
        this.profileID = profileID;
        this.numNeighbrsHelped = numNeighbrsHelped;
        this.thyNPoints = thyNPoints;
        this.basicprofileInfo = basicprofileInfo;
        this.name = name;
        this.address = address;
        this.aptNo = aptNo;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
    }


    public Long getProfileID() {
        return profileID;
    }

    public String getName() {
        return name;
    }

    public int getNumNeighbrsHelped() {
        return numNeighbrsHelped;
    }

    public int getThyNPoints() {
        return thyNPoints;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAptNo() {

        return aptNo;
    }

    public void setAptNo(String aptNo) {
        this.aptNo = aptNo;
    }

    public APIGeneralResult getResult() {
        return result;
    }


    public boolean isBasicprofileInfo() {
        return basicprofileInfo;
    }

    public void setBasicprofileInfo(boolean basicprofileInfo) {
        this.basicprofileInfo = basicprofileInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
