package com.thyn.backend.utilities.security;

/**
 * Created by shalu on 9/21/16.
 */
public class ExternalLogonPackage {
    private String userId;
    private String accessToken;

    private ExternalLogonPackage() {}

    public ExternalLogonPackage(String userId, String accessToken)
    {
        this.userId = userId;
        this.accessToken = accessToken;
    }
    public ExternalLogonPackage(String accessToken)
    {
        this.userId = null;
        this.accessToken = accessToken;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getAccessToken(){
        return this.accessToken;
    }
}