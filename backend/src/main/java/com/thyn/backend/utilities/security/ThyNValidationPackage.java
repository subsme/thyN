package com.thyn.backend.utilities.security;

/**
 * Created by shalu on 3/7/16.
 */
public class ThyNValidationPackage {
    private String email;
    private String password;
    private String validationKey;

    private ThyNValidationPackage() {}

    public ThyNValidationPackage(String email, String password, String validationKey)
    {
        this.email = email;
        this.password = password;
        this.validationKey = validationKey;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getValidationKey(){
        return this.validationKey;
    }
}
