package com.thyn.user;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.android.backend.userAPI.model.ThyNLogonPackage;
import com.thyn.android.backend.userAPI.model.ThyNRegisterPackage2;
import com.thyn.connection.GoogleAPIConnector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SignupLooperThread<Token> extends HandlerThread {
    private static final String TAG = "SignupLooperThread";
    private static final int MESSAGE_DOWNLOAD = 0;
    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

    Handler mResponseHandler;
    Listener<Token> mListener;

    public interface Listener<Token>{
        void onResponseFromServer(Token token, APIGeneralResult b);
    }
    public void setListener(Listener<Token> listener){
        mListener = listener;
    }

    public SignupLooperThread(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for : " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }
    public void queueRequest(Token token, String email,String password, String name){
        Log.i(TAG, "Got a request: " + email + ", " + password + ", " + name);
        requestMap.put(token, email+":"+password+":"+name);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }
    private void handleRequest(final Token token){
        try{
            final String emailAndPasswordAndName = requestMap.get(token);
            final String email = emailAndPasswordAndName.substring(0, emailAndPasswordAndName.indexOf(":"));
            final String password = emailAndPasswordAndName.substring(emailAndPasswordAndName.indexOf(":")+1, emailAndPasswordAndName.lastIndexOf(":"));
            final String name = emailAndPasswordAndName.substring(emailAndPasswordAndName.lastIndexOf(":")+1);
            Log.i(TAG, "email is: " + email + ", password is: " + password + ", name is: " + name);
            ThyNRegisterPackage2 tnrp = new ThyNRegisterPackage2();
            tnrp.setEmail(email);
            tnrp.setName(name);
            tnrp.setPassword(password);
            final APIGeneralResult rslt = GoogleAPIConnector.connect_UserAPI().registerNewThyNUser(tnrp).execute();
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token) != emailAndPasswordAndName)
                        return;

                    requestMap.remove(token);
                    mListener.onResponseFromServer(token,rslt);
                }
            });
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
