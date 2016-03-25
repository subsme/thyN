package com.thyn.backend.utilities.security;

import java.io.Serializable;
import com.thyn.backend.entities.users.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.thyn.backend.ThyNConstants;


/**
 * Created by shalu on 3/4/16.
 */
public class ThyNSession implements Serializable{
    private User sessionUser;
    private String accessToken;
    private LoginType loginType;

    private ThyNSession(User sessionUser, String accessToken, LoginType loginType)
    {
        this.sessionUser = sessionUser;
        this.accessToken = accessToken;
        this.loginType = loginType;
    }

    public User getSessionUser()
    {
        return sessionUser;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public LoginType getLoginType()
    {
        return loginType;
    }

    public static void SetNewSession(HttpServletRequest req, User user)
    {
        SetNewSession(req, user, null, LoginType.thyN);
    }

    public static void SetNewSession(HttpServletRequest req, User user, String accessToken, LoginType loginType)
    {
        if (user == null || req == null)
            throw new IllegalArgumentException();

        if (req.getSession(false) != null)
            req.getSession().invalidate();
        HttpSession session = req.getSession(true);

        session.setAttribute(ThyNConstants.USERSESSION, new ThyNSession(user, accessToken, loginType));
        session.setMaxInactiveInterval(1800);
    }

    public static ThyNSession getSession(HttpServletRequest req)
    {
        HttpSession session = req.getSession(false);
        if(session == null)
            return null;

        Object sessionObj = session.getAttribute(ThyNConstants.USERSESSION);

        if(sessionObj == null || !(sessionObj instanceof ThyNSession))
            return null;

        return (ThyNSession)sessionObj;
    }
}

