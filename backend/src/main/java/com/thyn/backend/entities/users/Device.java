package com.thyn.backend.entities.users;

import com.googlecode.objectify.annotation.Cache;
import com.thyn.backend.WPT.UserRole;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.EntityObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.thyn.backend.utilities.security.LoginType;

/**
 * Created by subu sundaram on 4/6/16.
 */
@Entity
public class Device extends EntityObject {
    @Index
    private String registration_token;
    private String notification_key_name;

    private String notification_key;
    @Index
    private Long profile_key;

    private Device(){}
    public Device(String userFirstName, String registration_token, Long profile_key){
        this.registration_token = registration_token;
        this.profile_key = profile_key;
        this.notification_key_name = userFirstName + "-" + profile_key;
        this.notification_key = null;
    }
    public Device(String userFirstName, String registration_token, Long profile_key, String notification_key){
        this.registration_token = registration_token;
        this.profile_key = profile_key;
        this.notification_key_name = userFirstName + "-" + profile_key;
        this.notification_key = notification_key;
    }

    public static Device createNewDeviceOnDatastore(String userFirstName, String registration_token, Long profile_key, String notification_key){
        Device device = new Device(userFirstName, registration_token, profile_key, notification_key);
        if(DatastoreHelpers.trySaveEntityOnDatastore(device) != null)
            return device;
        return null;
    }

    public static Device createNewDeviceOnDatastore(Device device){
        if(DatastoreHelpers.trySaveEntityOnDatastore(device) != null)
            return device;
        return null;
    }


    public String getRegistration_token() {
        return registration_token;
    }

    public void setRegistration_token(String registration_token) {
        this.registration_token = registration_token;
    }

    public String getNotification_key_name() {
        return notification_key_name;
    }

    public void setNotification_key_name(String notification_key_name) {
        this.notification_key_name = notification_key_name;
    }

    public String getNotification_key() {
        return notification_key;
    }

    public void setNotification_key(String notification_key) {
        this.notification_key = notification_key;
    }

    public Long getProfile_key() {
        return profile_key;
    }

    public void setProfile_key(Long profile_key) {
        this.profile_key = profile_key;
    }

    @Override
    public boolean dispose() { return true;}
    @Override
    public String getName(){ return null;}

}
