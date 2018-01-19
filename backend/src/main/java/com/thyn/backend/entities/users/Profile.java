package com.thyn.backend.entities.users;

import com.googlecode.objectify.annotation.Entity;
import com.thyn.backend.entities.EntityObject;


import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.api.server.spi.config.AnnotationBoolean;

import com.thyn.backend.datastore.DatastoreHelpers;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;

import com.thyn.backend.utilities.security.SecurityUtilities;
/**
 * Created by Subu Sundaram on 3/4/16.
 * @original author: Angelo Agatino Nicolosi
 */
@Entity
public class Profile extends EntityObject{
    //private static final long serialVersionUID = 1L;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private String password;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private byte[] passwordSalt;

    private  String firstName;
    private  String lastName;
    private Profile(){}
    public Profile(String firstName, String lastName){
        this.firstName  = firstName;
        this.lastName   = lastName;
    }

    public  String getLastName() {
        return lastName;
    }

    public  void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public  void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public  String getFirstName() {
        return firstName;
    }



    public static Profile createNewProfileOnDatastore(String firstName, String lastName)
    {
        Profile prof = new Profile(firstName, lastName);
        if (DatastoreHelpers.trySaveEntityOnDatastore(prof) != null)
            return prof;
        return null;
    }
    public String getPassword() {
        return password;
    }

    public boolean setPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(setPasswordSalt(SecurityUtilities.getRandomSalt()))
        {
            this.password = SecurityUtilities.computesHashFromPassword(password, getPasswordSalt());
            return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
        }
        return false;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public boolean setPasswordSalt(byte[] passwordSalt) {
        if(this.passwordSalt != null && this.passwordSalt.equals(passwordSalt))
            return true;
        this.passwordSalt = passwordSalt;
        return DatastoreHelpers.trySaveEntityOnDatastore(this) != null;
    }
    @Override
    public String getName() {
        return null;
    }
    @Override
    public boolean dispose() {
        return DatastoreHelpers.removeEntityFromDatastore(this);
    }
}
