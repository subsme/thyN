package com.thyn.backend.session;

import com.google.api.server.spi.config.Authenticator;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.api.server.spi.auth.common.User;

/**
 * Created by shalu on 5/3/16.
 */
public class OAuth2EndpointsAuthenticator implements Authenticator {
    private static String TAG = "OAuth2EndpointsAuthenticator";
    private static final Logger log = Logger.getLogger(OAuth2EndpointsAuthenticator.class.getName());
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public User authenticate(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        if(session == null) {
            if (getSessionId() == null) log.info("session id is  null");
        }
        else{
            log.info("session is not null");
        }
        return null;
    }

}
