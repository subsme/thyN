package com.thyn.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.api.services.datastore.client.DatastoreHelper;
import com.googlecode.objectify.cmd.Query;
import com.thyn.backend.entities.MyTask;
import com.thyn.backend.entities.log.Log_Action;
import com.thyn.backend.entities.users.Device;
import com.thyn.backend.entities.users.User;
import com.thyn.backend.gcm.GcmSender;
import com.thyn.backend.utilities.security.SecurityUtilities;
import com.thyn.backend.utilities.security.ThyNSession;

import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.Date;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
//import javax.validation.constraints.Null;

import static com.thyn.backend.datastore.OfyService.ofy;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.users.Profile;

import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;


/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myTaskApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.android.thyn.com",
                ownerName = "backend.android.thyn.com",
                packagePath = ""
        ),
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE)
)
public class MyTaskEndpoint {

    private static final Logger logger = Logger.getLogger(MyTaskEndpoint.class.getName());

    /**
     * This method gets the <code>MyTask</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>MyTask</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getMyTask")
    public MyTask getMyTask(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getMyTask method");
        return null;
    }

    /**
     * Return a collection of tasks
     *
     * @param count The number of tasks
     * @return a list of Tasks
     */

    @ApiMethod(name = "listTasks", httpMethod = HttpMethod.GET, path="mytask/list")
    public CollectionResponse<MyTask> listTasks(@Nullable @Named("count") Integer count,
                                                @Named("helper") Boolean helper,
                                                @Named("solved") Boolean isSolved,
                                                @Named("profileId") Long profileID,
                                                HttpServletRequest req) throws APIErrorException{
  /*      ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        profileID = sessionUser.getProfileId();
**/
        Cookie cookies[] = req.getCookies();
        if(cookies!= null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie c = cookies[i];
                logger.info("cookies: " + c.getName() + "-" + c.getValue());
            }
        }

                logger.info("In listTasks. The profile id is : " + profileID);
        Query<MyTask> query = null;
        if(helper){
            logger.info("HELPER is SET");
            if(!isSolved) query = ofy().load().type(MyTask.class).filter("helperUserProfileKey", profileID).order("-mCreateDate");
            else query = ofy().load().type(MyTask.class).filter("helperUserProfileKey",profileID).filter("isSolved", true).order("-mCreateDate");
        }
        else {
            logger.info("HELPER is not SET");
            if(!isSolved) //query = ofy().load().type(MyTask.class).filter("helperUserProfileKey ==",null);
                query = ofy().load().type(MyTask.class).filter("isSolved", false).order("-mCreateDate");
            else query = ofy().load().type(MyTask.class).filter("isSolved", true).order("-mCreateDate");
            //.order("-mCreateDate");
        }
        logger.info("The query is : " + query);

        if (count != null) query.limit(count);


        List<MyTask> records = new ArrayList<MyTask>();
        QueryResultIterator<MyTask> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            logger.info("adding iterator ");
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

//Find the next cursor
       /* if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }*/
        return CollectionResponse.<MyTask>builder().setItems(records).build();
    }
    /**
     * This inserts a new <code>MyTask</code> object.
     *
     * @param myTask The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertMyTask", httpMethod = HttpMethod.POST)
    public void insertMyTask(MyTask myTask, @Named("profileID") Long profileID, HttpServletRequest req) throws ConflictException, APIErrorException{
        // TODO: Implement this function
        logger.info("Calling insertMyTask method");
        logger.info("The profile id is : " + profileID);
  /*      ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");
*/
        if(myTask.getId() != null){
            if(DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class,myTask.getId()) != null){
                throw new ConflictException("Object already exists");
            }
        }
        /* Setting the user name in myTask is Redundant.
        But thats the easy way to get all the task information when TaskListFragment calls the server */
        //Long profileID = sessionUser.getProfileId();
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, profileID);
        myTask.setUserProfileName(prof.getFirstName()+ " " + prof.getLastName());
        myTask.setCreateDate(new Date().toString());
        ofy().save().entity(myTask).now();
        //myTask.setTaskDescription("Mutating the task description: " + myTask.getTaskDescription());

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

    /**
     * This inserts a new <code>MyTask</code> object.
     *
     * @param myTask The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "updateMyTask", httpMethod = HttpMethod.POST)
    public void updateMyTask(MyTask myTask, HttpServletRequest req) throws ParseException, NullPointerException, APIErrorException{
        // TODO: Implement this function
        logger.info("Calling updateMyTask method");
  /*      ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");
*/
        MyTask task = null;
        if(myTask.getId() != null){
            task = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class,myTask.getId());
        }
        if(task == null) throw new NullPointerException("Task object is null");

        task.setTaskDescription(myTask.getTaskDescription());
        task.setBeginLocation(myTask.getBeginLocation());
        task.setServiceDate(myTask.getServiceDate());
        task.setUpdateDate(new Date().toString());
        DatastoreHelpers.trySaveEntityOnDatastore(task);
    }

    @ApiMethod(name = "updateTaskHelper", httpMethod = HttpMethod.POST, path="mytask/updatetaskhelper")
    public void updateTaskHelper(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling updateTaskHelper method. task id is: " + taskId);
        logger.info("The profile id is : " + profileID);
       /* ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");
*/
      //  Long profileID = sessionUser.getProfileId();
        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        if(mTask.getHelperUserProfileKey() == null) {
            mTask.setHelperUserProfileKey(profileID);
            Profile helperProf = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, profileID);
            mTask.setHelperProfileName(helperProf.getFirstName() + " " + helperProf.getLastName());
            logger.info("Saving Task with new helper user profile key");
            //ofy().save().entity(mTask).now();
            DatastoreHelpers.trySaveEntityOnDatastore(mTask);

            log("HELPER-ASSIGNED", profileID, mTask.getUserProfileKey(), taskId, 0);
            /* Retrieve the profile */
            Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class,profileID);

            //Device device = DatastoreHelpers.tryLoadEntityFromDatastore(Device.class, "profile_key ==", profileID.toString());
            // the above line didnt work because profile_key is Long but the profileID that I was passing is String. So made sure profileID is passed as Long.
            Device device = ofy().load().type(Device.class).filter("profile_key ==", mTask.getUserProfileKey()).first().now();
            if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);
            GcmSender.sendMessageToDeviceGroup(device.getNotification_key(),"Yay! " + prof.getFirstName() + " " + prof.getLastName() + " is interested in helping you.");
        }
        else
        {
          //  com.thyn.backend.utilities.Logger.logWarning("Exception while assigning Task to HELPER (" + sessionUser.getId() + ").");
            throw new APIErrorException(500, "UADU02 - Internal Server Error.");
        }
    }

    @ApiMethod(name = "cancelTaskHelper", httpMethod = HttpMethod.POST, path="mytask/canceltaskhelper")
    public void cancelTaskHelper(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling cancelTaskHelper method. task id is: " + taskId);
        logger.info("The profile id is : " + profileID);

        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);
        mTask.setHelperUserProfileKey(null);
        mTask.setHelperProfileName(null);
        DatastoreHelpers.trySaveEntityOnDatastore(mTask);

        log("HELPER-CANCELLED", profileID, mTask.getUserProfileKey(), taskId, 0);



    }

    @ApiMethod(name = "markComplete", httpMethod = HttpMethod.POST)
    public void markComplete(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling markComplete method. task id is: " + taskId);

       /* ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");
*/
        //Long profileID = sessionUser.getProfileId();
        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        mTask.setIsSolved(true);
        logger.info("Saving Task after the task is complete");
        ofy().save().entity(mTask).now();

        log("COMPLETED", profileID, mTask.getUserProfileKey(), taskId, 10);
        /* Retrieve the profile */
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class,profileID);

        //Device device = DatastoreHelpers.tryLoadEntityFromDatastore(Device.class, "profile_key ==", profileID.toString());
        // the above line didnt work because profile_key is Long but the profileID that I was passing is String. So made sure profileID is passed as Long.
        Device device = ofy().load().type(Device.class).filter("profile_key ==", profileID).first().now();
        if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);
        GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), "Yay! " + prof.getFirstName() + " " + prof.getLastName() + " has marked the task Complete.");

    }

   /* private MyTask findRecord(Class<T> classType, Long key) {
        return ofy().load().type(classType).id(key).get();
//or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }*/
    private String getCity(String address){
        String[] parts = address.split(",");
        String city = null;
        if(parts != null && parts.length>0){
            int len = parts.length;
            if(len == 2){// Great. here 707 Henrietta Avenue Sunnyvale is in part[0] and CA is in part[1]
                String streetaddresswithcity = parts[0];
            }

        }
        return null;
    }

    private void log(String action, Long profileID, Long neighbrWhoIsHelpedID, Long taskId, int points){
        Log_Action log = Log_Action.createNewUserOnDatastore(action, new Date(),profileID, neighbrWhoIsHelpedID, taskId, points);
        DatastoreHelpers.trySaveEntityOnDatastore(log);
    }



}