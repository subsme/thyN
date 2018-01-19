package com.thyn.backend.api;

import java.io.Serializable;

/**
 * Created by shalu on 3/4/16.
 */
public class APIGeneralResult{
    public String statusCode;
    public int code = 1;
    public String message;
    //create an enum with OK, Fail etc.

    public APIGeneralResult(String statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }
    public APIGeneralResult(int code, String statusCode, String message){
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }
}
