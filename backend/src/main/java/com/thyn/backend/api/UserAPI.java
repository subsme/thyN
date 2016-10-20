package com.thyn.backend.api;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.api.services.datastore.client.Datastore;


import com.googlecode.objectify.cmd.Query;
import com.thyn.backend.entities.MyTask;
import com.thyn.backend.entities.log.Log_Action;
import com.thyn.backend.gcm.GcmSender;
import com.thyn.backend.utilities.security.ExternalAuthentication;
import com.thyn.backend.utilities.security.ExternalLogonPackage;
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


import com.google.api.server.spi.response.ConflictException;

import javax.inject.Named;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.thyn.backend.entities.users.Device;

import org.json.JSONObject;

import static com.thyn.backend.datastore.OfyService.ofy;

/**
 * Created by subu sundaram on 3/4/16.
 */
@Api(name = "userAPI",
        version = "v1",
        description = "User API",
        namespace = @ApiNamespace(ownerDomain = "backend.android.thyn.com",
                ownerName = "backend.android.thyn.com",
                packagePath=""),
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE))

public class UserAPI {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserAPI.class.getName());
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
        Logger.logInfo("Calling LogonWithThyN");
        if (!InputValidation.validateEmailAddressInput(logonPackage.getEmail()))
            throw new APIErrorException(400, "UALM01 - Invalid email.");

        if (!InputValidation.validatePassword(logonPackage.getPassword()))
            throw new APIErrorException(400, "UALM02 - Invalid password.");

        User user = null;
        Profile prof = null;
        try
        {
            user = validateThyNLogin(logonPackage.getEmail(), logonPackage.getPassword());
            if (user != null) {
                ThyNSession.SetNewSession(req, user);
                prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, user.getProfileId());
            }


        }catch(Exception e)
        {
            Logger.logError("UALM03 - Exception while logging user in through thyN logon. Email: " + logonPackage.getEmail() + ". ", e);
            throw new APIErrorException(500, "UALM03 - Internal Server Error.");
        }

        if (user == null)
            return new APIGeneralResult(null, "UALM04 - Invalid credentials.");


        return new APIGeneralResult(prof.getId().toString(), prof.getFirstName() + " " + prof.getLastName());
    }

    @ApiMethod(name = "LogonWithGoogle", httpMethod = HttpMethod.POST)
    public APIUserInformation LogonWithGoogleUser(ExternalLogonPackage googLogonPackage, HttpServletRequest req) throws APIErrorException {
        Logger.logInfo("Calling LogonWithGoogle");

        String googUserToken = googLogonPackage.getAccessToken();
        if (!InputValidation.validateTextBoxInput(googUserToken))
            throw new APIErrorException(400, "UALG01 - Invalid input for Google Login.");

        User user = null;
        Profile prof = null;
        try
        {
            user = ExternalAuthentication.getInfoFromGoogleForLogin(googUserToken);

        }catch(Exception e)
        {
            Logger.logError("UALM03 - Exception while logging user in through thyN logon. Email: " + googLogonPackage.getUserId() + ". ", e);
            throw new APIErrorException(500, "UALM03 - Internal Server Error.");
        }

        if (user == null)
            //return new APIGeneralResult(null, "UALM04 - Invalid credentials.");
            return new APIUserInformation(new APIGeneralResult("NOt OK", "UALM04 - Invalid credentials."));
        Logger.logInfo("user profile id is:" + user.getProfileId().toString());
        APIUserInformation userStats = getUserStatisticsForHomePage(user, 20);

        return userStats;
    }


    @ApiMethod(name = "LogonWithFacebook", httpMethod = HttpMethod.POST)
    public APIUserInformation LogonWithFacebookUser(ExternalLogonPackage fbLogonPackage, HttpServletRequest req) throws APIErrorException {
        Logger.logInfo("Calling LogonWithFacebok");

        String fbUserToken = fbLogonPackage.getAccessToken();
        if (!InputValidation.validateTextBoxInput(fbUserToken))
            throw new APIErrorException(400, "UALG01 - Invalid input for Google Login.");

        User user = null;
        Profile prof = null;
        try
        {
            user = ExternalAuthentication.getInfoFromFacebookForLogin(fbUserToken);

        }catch(Exception e)
        {
            Logger.logError("UALM03 - Exception while logging user in through thyN logon. Email: " + fbLogonPackage.getUserId() + ". ", e);
            throw new APIErrorException(500, "UALM03 - Internal Server Error.");
        }

        if (user == null)
            return new APIUserInformation(new APIGeneralResult("Not OK", "UALM02 Error "));

        Logger.logInfo("user profile id is:" + user.getProfileId().toString());

        APIUserInformation userStats = getUserStatisticsForHomePage(user, 20);


        return userStats;
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

    private User doesExternalUserExistInDB(String email) throws Exception{
        User user = DatastoreHelpers.tryGetUserByEmailAddress(email);
        return user;
    }

    @ApiMethod(name = "registerNewThyNUser", httpMethod = HttpMethod.POST)
    public APIGeneralResult registerNewThyNUser(ThyNRegisterPackage2 registerPackage2) throws APIErrorException
    {
        String email = registerPackage2.getEmail();
        String password = registerPackage2.getPassword();
        String name = registerPackage2.getName();
        UserRegistrationStatus statusRegistration = tryRegisterNewUser(email, name, password);
        Logger.logInfo("In registerNewThynUser");
        switch(statusRegistration.getStatus())
        {
            case OK:
                User user = DatastoreHelpers.tryGetUserByEmailAddress(email);
                Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, user.getProfileId());
                return new APIGeneralResult(prof.getId().toString(), prof.getFirstName() + " " + prof.getLastName());
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
    public Profile completeRegistrationAndLogonThyNUser(ThyNValidationPackage validationPackage, HttpServletRequest req) throws APIErrorException
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
                    //return new APIGeneralResult("OK", user.getProfileId().toString());
                    return profile;
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
    @ApiMethod(name = "getUser", httpMethod = HttpMethod.GET, path = "UserInfo")
    public User getUser(@Named("userId") long userId) throws APIErrorException{
        User user =  DatastoreHelpers.tryLoadEntityFromDatastore(User.class, userId);

        if(user == null)
        {
            throw new APIErrorException(400, "UAGP01 - Invalid user id.");
        }

        return user;
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
    @ApiMethod(name = "updateMyProfile", httpMethod = HttpMethod.POST, path="updateMyProfile")
    public APIGeneralResult updateMyProfile(@Named("phone") String phone,
                                            @Named("address") String address,
                                            @Named("lat") String latitude,
                                            @Named("long") String longitude,
                                            @Named("city") String city,
                                            @Named("socialID") String socialID,
                                            @Named("socialType") int socialType,
                                            HttpServletRequest req) throws ConflictException, APIErrorException{
        logger.info("Phone received from the client is: " + phone);
        logger.info("Address received from the client is: " + address
                + ", lat: " + latitude
                + ", long: " + longitude
                + ", city: " + city);
        User user = null;
        if(socialType == 0) {//Facebook login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "fbUserId ==", socialID);
        }
        else{//Google Login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "googUserId ==", socialID);
        }
        user.setAddress(address);
        user.setCity(city);
        user.setLAT(Double.parseDouble(latitude));
        user.setLONG(Double.parseDouble(longitude));
        user.setPhone(phone);
        ofy().save().entity(user).now();
        return new APIGeneralResult("OK", "Extra profile information added.");
    }

        @ApiMethod(name = "insertMyTaskWithSocialID", httpMethod = HttpMethod.POST, path="mytask/insertmytaskwithsocialid")
    public APIGeneralResult insertMyTaskWithSocialID(MyTask myTask, @Named("socialType") int socialType, @Named("socialID") String socialID, HttpServletRequest req) throws ConflictException, APIErrorException{
        //Long profileId = findProfileId(socialID);
        logger.info("Client inserting a task. Calling insertMyTaskWithSocialID method");
        logger.info("The social id is : " + socialID + ", The social type is: " + socialType);
        logger.info("----------------Task Information sent from client-----------------");
        logger.info("Title: " + myTask.getTaskTitle()
                +   ", Description: " + myTask.getTaskDescription()
                +   ", Location: " + myTask.getBeginLocation()
                +   ", From Date: " + myTask.getServiceDate()
                +   ", To Date: " + myTask.getServiceToDate()
                +   ", Time Range: " + myTask.getServiceTimeRange());

        User user = null;
        if(socialType == 0) {//Facebook login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "fbUserId ==", socialID);
        }
        else{//Google Login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "googUserId ==", socialID);
        }
        logger.info("User profile id retrieved from the database is: " + user.getProfileId());
        myTask.setUserProfileKey(user.getProfileId());
        myTask.setUserProfileName(user.getName());
        myTask.setImageURL(user.getImageURL());
        myTask.setCreateDate(new Date().toString());

        logger.info("Saving task information in database...");
        ofy().save().entity(myTask).now();
        return new APIGeneralResult("OK", "New Task Created Successfully");
    }


    private APIUserInformation getUserStatisticsForHomePage(User user,Integer range){
        Long profileID = user.getProfileId();
        String name = user.getName();
        String city = user.getCity();
        String phone = user.getPhone();
        String address = user.getAddress();
        double latitude = user.getLAT();
        double longitude = user.getLONG();


        Vector<Integer> u = this.findDistinctNeigbrsHelped(profileID);
/*
        Query<Log_Action> query = null;

        query = ofy().load().type(Log_Action.class)
                .filter("userKey ==", profileID)
                .filter("action =", "COMPLETED")
                .order("-actionTime");
        QueryResultIterator<Log_Action> iterator = query.iterator();
        int num = 0;
        int count = 0;
        int totPoints = 0;
        while (iterator.hasNext()) {
            count ++;
            Log_Action log_action = iterator.next();
            totPoints += log_action.getPoints();
            logger.info("adding Log_Action: User" + log_action.getUserKey()
                    + ", Neighbr helped " + log_action.getNeighbrWhoIsHelpedKey()
                    + ", Task helped " + log_action.getTaskKey());
        }
*/
        //int requests = findNumberOfRequestsInRange(profileID, range);
        //int myhelps = findNumberofPeopleIWillBeHelping(profileID);

        APIGeneralResult rslt = new APIGeneralResult("OK", "Success");

        logger.info("profileID: " + profileID + ", count: " + u.get(0)
                + " ,totPoints: " + u.get(1)
                );
        boolean b = false;
        if(city != null && phone != null) b = true;
        APIUserInformation stats = new APIUserInformation(rslt,
                profileID,
                name,
                u.get(0),
                u.get(1),
                b,
                address,
                latitude,
                longitude,
                city
                );

        return stats;
    }
    private Vector<Integer> findDistinctNeigbrsHelped(Long profileKey){
        Query<Log_Action> query = null;

        query = ofy().load().type(Log_Action.class)
                .filter("userKey", profileKey)
                .filter("action =", "COMPLETED")
                .order("-actionTime");
        //List<Log_Action> records = new ArrayList<Log_Action>();
        QueryResultIterator<Log_Action> iterator = query.iterator();
        int num = 0;
        if(iterator == null) return null;
        int count = 0;
        int points = 0;
        while (iterator.hasNext()) {
            count ++;

            Log_Action log_action = iterator.next();
            points +=log_action.getPoints();

            //records.add(log_action);
            logger.info("adding Log_Action: User" + log_action.getUserKey()
                    + ", Neighbr helped " + log_action.getNeighbrWhoIsHelpedKey()
                    + ", Task helped " + log_action.getTaskKey());
        }
        Vector<Integer> v = new Vector<Integer>();
        v.add(count);
        v.add(points);
        return v;
    }

    private int findNumberOfRequestsInRange(Long profileKey, int range){
        Query<MyTask> query = ofy().load().type(MyTask.class).filter("isSolved", false).order("-mCreateDate");
        QueryResultIterator<MyTask> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            num ++;
        }
        return num;
    }
    private int findNumberofPeopleIWillBeHelping(Long profileKey){
        Query<MyTask> query = ofy().load().type(MyTask.class)
                .filter("helperUserProfileKey", profileKey)
                .order("-mCreateDate");
        QueryResultIterator<MyTask> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            num ++;
        }
        return num;
    }

    @ApiMethod(name = "setGCMRegistrationToken", httpMethod = HttpMethod.POST)
    public APIGeneralResult setGCMRegistrationToken(HttpServletRequest req, @Named("profileID") Long profileID, @Named("token") String token) throws APIErrorException{

        try{
            token = URLDecoder.decode(token, "UTF-8");
        }
        catch(UnsupportedEncodingException uee){
            uee.printStackTrace();
        }
        Logger.logInfo("token value is: " + token);
        Logger.logInfo("profileId value is: " + profileID);

       /* ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");
*/

        Device device = DatastoreHelpers.tryLoadEntityFromDatastore(Device.class, "registration_token ==", token);
        if(device != null) {
            Device device1 = DatastoreHelpers.tryLoadEntityFromDatastore(Device.class, "profile_key ==", profileID);
            if(device1 != null) return new APIGeneralResult("OK", "Not adding device. This device already added previously");
            else{
                device.setProfile_key(profileID);
                DatastoreHelpers.trySaveEntityOnDatastore(device);
            }
        }

        //Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, sessionUser.getProfileId());
        /* Retrieve the profile */
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class,profileID);
        device = new Device(prof.getFirstName(), token, prof.getId());

       // if device creation was successful. Send the device information to GCM
        if(device.getNotification_key() == null) {
            String notification_key = GcmSender.createDeviceGroup(device.getNotification_key_name(), token);
            if(notification_key != null){
                device.setNotification_key(notification_key);
                Device.createNewDeviceOnDatastore(device);
            }
        }
        return new APIGeneralResult("OK", "Device sent to GSM API and saved in our database");
    }

    /* Helper class */
    protected static UserRegistrationStatus tryRegisterNewUser(String email, String name, String password) throws APIErrorException
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
            user = User.createNewUserOnDatastore(UserRole.USER, email, name, password);
            if (user != null)
                return new UserAPI.UserRegistrationStatus(UserAPI.RegistrationStatusCode.OK, user);
            else
            {
                Logger.logWarning("UANU02 - Exception while storing new user. Email: " + email + ".");
                throw new APIErrorException(500, "UANU02 - Internal Server Error.");
            }
        }catch(InvalidKeySpecException ikse){
            Logger.logError("UACM02 - InvalidKeySpecException while setting the password when registering the user.", ikse);
            throw new APIErrorException(500, "UACM02 - Internal Server Error.");
        }
        catch(NoSuchAlgorithmException nsae){
            Logger.logError("UACM02 - NoSuchAlgorithmException while setting the password when registering the user.", nsae);
            throw new APIErrorException(500, "UACM02 - Internal Server Error.");
        }catch(Exception e)
        {
            Logger.logError("UANU03 - Exception while creating new user. Email: " + email + ".", e);
            throw new APIErrorException(500, "UANU03 - Internal Server Error.");
        }

    }

}
