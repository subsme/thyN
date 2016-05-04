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
    @Index private String email;
    private Date creationTime;
    private String displayName;
    private Long profileKey;
    private String fbUserId;
    private UserRole userRole;
    private UserStatus userStatus;

    public static User createNewUserOnDatastore(UserRole userRole, String email, String name, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        return createNewUserOnDatastore(userRole, email, name, null, LoginType.thyN, password);
    }
    public static User createNewUserOnDatastore(UserRole userRole, String email, String name, String externalUserId, LoginType lType, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        User user = new User(userRole, email, name, password);
        switch(lType)
        {
            case Facebook:
                user.setFbUserId(externalUserId);
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

    private User(UserRole userRole, String email, String name, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        this.userRole = userRole;
        this.email = email;
        this.userStatus = UserStatus.ACTIVE;
        //this.displayName = name;
        this.creationTime = new Date();
        String firstName = name;
        String lastName = null;
        if(name.trim().contains(" ")){
            firstName = name.substring(0,name.indexOf(" "));
            lastName =  name.substring(name.indexOf(" "));
        }

        Profile prof = Profile.createNewProfileOnDatastore(firstName, lastName);
        this.profileKey = prof.getId();

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
    public Long getProfileId() {
        return profileKey;
    }
    public boolean setProfileId(Long profileId) {
        if(this.profileKey == profileId)
            return true;
        this.profileKey = profileId;
        return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
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
