package com.thyn.backend.api;

import java.io.Serializable;

/**
 * Created by shalu on 3/4/16.
 */
public class APIGeneralResult{
    public String statusCode;
    public String message;

    public APIGeneralResult(String statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }
}
