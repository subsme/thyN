package com.thyn.backend.api;

import com.google.api.server.spi.ServiceException;
/**
 * Created by shalu on 3/4/16.
 */
public class APIErrorException extends ServiceException{
    public APIErrorException(int code, String message){
        super(code,message);
    }
}
