package com.thyn.backend.api;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.repackaged.com.google.api.services.datastore.client.Datastore;


import com.thyn.backend.utilities.security.ThyNLogonPackage;
import com.thyn.backend.utilities.InputValidation;
import com.thyn.backend.utilities.Logger;
import com.thyn.backend.entities.users.User;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.WPT.UserStatus;
import com.thyn.backend.entities.users.Profile;
import com.thyn.backend.utilities.security.SecurityUtilities;
import com.thyn.backend.utilities.security.ThyNSession;
import com.thyn.backend.utilities.security.ThyNRegisterPackage2;
import com.thyn.backend.utilities.security.ThyNValidationPackage;
import com.thyn.backend.utilities.EmailManager;
import com.google.appengine.api.utils.SystemProperty;
import com.thyn.backend.WPT.UserRole;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.config.Nullable;

import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;
import javax.inject.Named;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.ArrayList;
import static com.thyn.backend.datastore.OfyService.ofy;
/**
 * Created by subu sundaram on 3/4/16.
 */
@Api(name = "userAPI",
        version = "v1",
        description = "User API",
        namespace = @ApiNamespace(ownerDomain = "backend.android.thyn.com",
                ownerName = "backend.android.thyn.com",
                packagePath=""))

public class UserAPI {
    static enum RegistrationStatusCode
    {
        OK,
        MailAddressAlreadyInUse,
        InvalidMailAddress
    }

    static class UserRegistrationStatus
    {
        private RegistrationStatusCode status;
        private User user;

        public UserRegistrationStatus(RegistrationStatusCode status, User user)
        {
            this.status = status;
            this.user = user;
        }

        public RegistrationStatusCode getStatus()
        {
            return this.status;
        }

        public User getUser()
        {
            return this.user;
        }

    }
    /*TODO: change API in order to accept hashed pwd */
    @ApiMethod(name = "LogonWithThyN", httpMethod = HttpMethod.POST)
    public APIGeneralResult LogonWithThyNUser(ThyNLogonPackage logonPackage, HttpServletRequest req) throws APIErrorException {
        if (!InputValidation.validateEmailAddressInput(logonPackage.getEmail()))
            throw new APIErrorException(400, "UALM01 - Invalid email.");

        if (!InputValidation.validatePassword(logonPackage.getPassword()))
            throw new APIErrorException(400, "UALM02 - Invalid password.");

        User user = null;
        try
        {
            user = validateThyNLogin(logonPackage.getEmail(), logonPackage.getPassword());
            if (user != null)
                ThyNSession.SetNewSession(req, user);
        }catch(Exception e)
        {
            Logger.logError("UALM03 - Exception while logging user in through thyN logon. Email: " + logonPackage.getEmail() + ". ", e);
            throw new APIErrorException(500, "UALM03 - Internal Server Error.");
        }

        if (user == null)
            throw new APIErrorException(401, "UALM04 - Invalid credentials.");

        return new APIGeneralResult("OK", "User authenticated successfully.");
    }

    private User validateThyNLogin(String email, String password) throws Exception
    {
        User user = DatastoreHelpers.tryGetUserByEmailAddress(email);
        if (user != null)
        {
            if(user.getUserStatus().equals(UserStatus.REGISTERED))
                return null;
            Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, user.getProfileId());
            if( prof.getPassword().equals(SecurityUtilities.computesHashFromPassword(password, prof.getPasswordSalt())))
                return user;
        }
        return null;
    }

    @ApiMethod(name = "registerNewThyNUser", httpMethod = HttpMethod.POST)
    public APIGeneralResult registerNewThyNUser(ThyNRegisterPackage2 registerPackage2) throws APIErrorException
    {
        String email = registerPackage2.getEmail();
        String password = registerPackage2.getPassword();
        String name = registerPackage2.getName();
        UserRegistrationStatus statusRegistration = tryRegisterNewUser(email,name);

        switch(statusRegistration.getStatus())
        {
            case OK:
                User user = DatastoreHelpers.tryGetUserByEmailAddress(email);
                Profile profile = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, user.getProfileId());
                try {
                    profile.setPassword(password);
                }
                catch(InvalidKeySpecException ikse){
                    Logger.logError("UACM02 - InvalidKeySpecException while setting the password when registering the user.", ikse);
                    throw new APIErrorException(500, "UACM02 - Internal Server Error.");
                }
                catch(NoSuchAlgorithmException nsae){
                    Logger.logError("UACM02 - NoSuchAlgorithmException while setting the password when registering the user.", nsae);
                    throw new APIErrorException(500, "UACM02 - Internal Server Error.");
                }
                user.setUserStatus(UserStatus.ACTIVE);

                return new APIGeneralResult("OK", "User registered successfully.");
            case MailAddressAlreadyInUse:
                throw new APIErrorException(400, "UANM01 - The mail address is already used by another user");
            case InvalidMailAddress:
                throw new APIErrorException(400, "UANM02 - The mail address is not valid");
            default:
                Logger.logWarning("Registration of new user. Status not recognized. " + email + ".");
                throw new APIErrorException(500, "UANM03 - Internal Server Error.");
        }
    }
    @ApiMethod(name = "completeRegistrationAndLogonThyNUser", httpMethod = HttpMethod.POST)
    public APIGeneralResult completeRegistrationAndLogonThyNUser(ThyNValidationPackage validationPackage, HttpServletRequest req) throws APIErrorException
    {
        String email = validationPackage.getEmail();
        String validationKey = validationPackage.getValidationKey();
        String newPassword = validationPackage.getPassword();

        if (!InputValidation.validateEmailAddressInput(email) || !InputValidation.validateTextBoxInput(validationKey) || !InputValidation.validatePassword(newPassword))
            throw new APIErrorException(400, "UACM01 - Invalid input.");

        try
        {
            User user = DatastoreHelpers.tryGetUserByEmailAddress(email);
            if (user != null)
            {
                if (user.getUserStatus().equals(UserStatus.REGISTERED) && user.getId().equals(Long.parseLong(validationKey)))
                {
                    Profile profile = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, user.getProfileId());
                    profile.setPassword(newPassword);

                    user.setUserStatus(UserStatus.ACTIVE);
                    //GravatarHelper.tryUpdateProfileThroughGravatar(user, profile);
                    ThyNSession.SetNewSession(req, user);
                    return new APIGeneralResult("OK", "User registration completed successfully.");
                }
            }
        }catch(Exception e)
        {
            Logger.logError("UACM02 - Exception while validating user through emailed link. Email: " + email + ".", e);
            throw new APIErrorException(500, "UACM02 - Internal Server Error.");
        }
        throw new APIErrorException(400, "UACM03 - The email, the validation key, or both are not valid.");
    }

    @ApiMethod(name = "deleteThyNUser", httpMethod = HttpMethod.DELETE)
    public APIGeneralResult deleteThyNUser(HttpServletRequest req) throws APIErrorException
    {
        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        if(sessionUser.dispose())
            return new APIGeneralResult("OK", "User removed successfully.");
        else
        {
            Logger.logWarning("Exception while removing existing user ("+sessionUser.getId()+").");
            throw new APIErrorException(500, "UADU02 - Internal Server Error.");
        }

    }
    @ApiMethod(name = "getCurrentUserProfile", httpMethod = HttpMethod.GET, path = "UserProfile")
    public Profile getCurrentUserProfile(HttpServletRequest req) throws APIErrorException{
        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, sessionUser.getProfileId());
        if(prof == null)
        {
            Logger.logWarning("Exception while fetching profile ("+sessionUser.getProfileId()+") for existing user ("+sessionUser.getId()+").");
            throw new APIErrorException(500, "UAGP02 - Internal Server Error.");
        }

        return prof;
    }

    /* Helper class */
    protected static UserRegistrationStatus tryRegisterNewUser(String email, String name) throws APIErrorException
    {
        //Validate input
        if (!InputValidation.validateEmailAddressInput(email))
            return new UserAPI.UserRegistrationStatus(UserAPI.RegistrationStatusCode.InvalidMailAddress, null);

        //Check the email is not already in use in the system
        User user = null;

        try
        {
            user = DatastoreHelpers.tryGetUserByEmailAddress(email);
        }catch(Exception e)
        {
            Logger.logError("UANU01 - Exception while checking if email already in use. Email: " + email + ".", e);
            throw new APIErrorException(500, "UANU01 - Internal Server Error.");
        }
        if (user != null)
            return new UserAPI.UserRegistrationStatus(UserAPI.RegistrationStatusCode.MailAddressAlreadyInUse, null);

        try
        {
            user = User.createNewUserOnDatastore(UserRole.USER, email, name);
            if (user != null)
                return new UserAPI.UserRegistrationStatus(UserAPI.RegistrationStatusCode.OK, user);
            else
            {
                Logger.logWarning("UANU02 - Exception while storing new user. Email: " + email + ".");
                throw new APIErrorException(500, "UANU02 - Internal Server Error.");
            }
        }catch(Exception e)
        {
            Logger.logError("UANU03 - Exception while creating new user. Email: " + email + ".", e);
            throw new APIErrorException(500, "UANU03 - Internal Server Error.");
        }

    }


}