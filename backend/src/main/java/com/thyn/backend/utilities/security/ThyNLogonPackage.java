package com.thyn.backend.utilities.security;

/**
 * Created by shalu on 3/4/16.
 */
public class ThyNLogonPackage {
    private String email;
    private String password;

    public ThyNLogonPackage(){}

    public ThyNLogonPackage(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }
}
