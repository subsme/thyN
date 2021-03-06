package com.thyn.backend.entities.users;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.WPT.UserRole;
import com.thyn.backend.entities.EntityObject;
import com.thyn.backend.utilities.Logger;
import com.thyn.backend.utilities.security.LoginType;
import com.thyn.backend.WPT.UserStatus;
import com.thyn.backend.datastore.DatastoreHelpers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

/**
 * Created by shalu on 3/4/16.
 */
@Entity
public class User extends EntityObject{
    @Index
    private String email;
    private Date creationTime;
    private String displayName;
    @Index
    private Long profileId;

    @Index
    private String fbUserId;
    @Index
    private String googUserId;
    private String imageURL;
    private String gender;

    private UserRole userRole;

    private UserStatus userStatus;

    private String phone;

    private String Address;
    private String aptNo;
    private double mLAT;
    private double mLONG;
    private String mCity;

    public String getImageURL() {
        return imageURL;
    }

    public static User createNewUserOnDatastore(UserRole userRole, String email, String name, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        return createNewUserOnDatastore(userRole, email, name, LoginType.thyN, null, null, null, password);
    }
    public static User createNewUserOnDatastore(UserRole userRole, String email, String name, LoginType lType, String externalUserId, String imageURL, String gender, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        User user = new User(userRole, email, name, password,externalUserId==null?false:true);

        user.setGender(gender);
        user.setImageURL(imageURL);

        switch(lType)
        {
            case Facebook:
                user.setFbUserId(externalUserId);
                break;
            case Google:
                user.setGoogUserId(externalUserId);
                break;
            default:
                break;
        }
        if(DatastoreHelpers.trySaveEntityOnDatastore(user) != null)
            return user;
        return null;
    }
  /*  public static boolean deleteUserFromDatastore(UserRole userRole, String email, String name){
        User user = new User(userRole, email, name);
        return DatastoreHelpers.removeEntityFromDatastore(user);

    }*/

    private User(){}

    private User(UserRole userRole, String email, String name, String password, boolean isExternal) throws NoSuchAlgorithmException, InvalidKeySpecException{
        this.userRole = userRole;
        this.email = email;
        this.userStatus = UserStatus.ACTIVE;
        this.displayName = name;
        this.creationTime = new Date();
        String firstName = name;
        String lastName = null;
        if(name.trim().contains(" ")){
            firstName = name.substring(0,name.indexOf(" "));
            lastName =  name.substring(name.indexOf(" "));
        }

        Profile prof = Profile.createNewProfileOnDatastore(firstName, lastName);
        this.profileId = prof.getId();

        if(!isExternal)
            prof.setPassword(password);



    }
    @Override
    public String getName() {
        if(displayName != null && displayName.trim().length() > 0)
            return displayName;
        else return "Anonymous";
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }
    public boolean setUserStatus(UserStatus userStatus){
        if(this.userStatus.equals(userStatus)) return true;
        this.userStatus = userStatus;
        return DatastoreHelpers.trySaveEntityOnDatastore(this)!=null;
    }

    public boolean setProfileId(Long profileId) {
        if(this.profileId == profileId)
            return true;
        this.profileId = profileId;
        return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
    }

    public Long getProfileId() {
        return profileId;
    }
    public String getFbUserId() {
        return fbUserId;
    }

    public boolean setFbUserId(String fbUserId) {
        if(this.fbUserId != null && this.fbUserId.equals(fbUserId))
            return true;
        this.fbUserId = fbUserId;
        return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
    }
    public boolean setGoogUserId(String googUserId) {
        if(this.googUserId != null && this.googUserId.equals(googUserId))
            return true;
        this.googUserId = googUserId;
        return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
    }
    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    @Override
    public boolean dispose() {
        Long userId = this.getId();
        boolean status = true;

        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, this.getProfileId());
        if (prof != null)
            status &= prof.dispose();

        return status & DatastoreHelpers.removeEntityFromDatastore(this);
    }

}
