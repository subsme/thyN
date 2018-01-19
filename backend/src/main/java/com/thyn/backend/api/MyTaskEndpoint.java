package com.thyn.backend.api;
/*subu - just converted dates from sting to date type (the dates are read as datetime at the client end).
        need to see how this changes the backend database structure.
        i need this because i need to retrieve tasks lin listtask only if the current date is less than the servive date.
        need to index the service date.
        */
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
import com.thyn.backend.entities.users.communication.Message;
import com.thyn.backend.gcm.GcmSender;
import com.thyn.backend.utilities.security.SecurityUtilities;
import com.thyn.backend.utilities.security.ThyNSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
                                                @Named("socialType") int socialType,
                                                @Named("socialID") String socialID,
                                                @Named("filterRadius") Integer radius,
                                                HttpServletRequest req) throws APIErrorException{
        User user = null;
        if(socialType == 0) {//Facebook login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "fbUserId ==", socialID);
        }
        else{//Google Login
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "googUserId ==", socialID);
        }
        if(user == null){
            logger.severe("The user " + socialID + " doesnt exist in the database. Returning null. BYE BYE");
            return null;
        }

        Long profileID = user.getProfileId();
        logger.info("In listTasks. The profile id is : " + profileID);

        logger.info("User information is: "
                + " LAT " + user.getLAT()
                + " LONG " + user.getLONG()
                + " city " + user.getCity()
                + " address " + user.getAddress()
                + " phone " + user.getPhone());

        //Getting the current date and time

        Date currentDate = new Date();

        Query<MyTask> query = null;
        if(helper){
            logger.info("HELPER is SET");
            if(!isSolved) query = ofy().load().type(MyTask.class).filter("helperUserProfileKey", profileID);//order("-mCreateDate");
            else query = ofy().load().type(MyTask.class).filter("helperUserProfileKey",profileID).filter("isSolved", true);//.order("-mCreateDate");
        }
        else {
            logger.info("HELPER is not SET");
            if(!isSolved)
                query = ofy().load().type(MyTask.class).filter("isSolved", false);//.filter("mServiceDate >", df.format(dateobj));//.order("-mCreateDate");
            else query = ofy().load().type(MyTask.class).filter("isSolved", true);//.order("-mCreateDate");
            //.order("-mCreateDate");
        }
    //Dec 08,2016 Subu setting distance = equator miles.
        double distance = 24901; //20;
        // if(radius > 0) distance = radius; Subu Dec 08commenting for now for testing
        logger.info("The search radius is set to: " + distance);
        double originLAT = user.getLAT();
        double originLONG = user.getLONG();
        if(originLAT == 0 || originLONG == 0){
            logger.severe("The user " + profileID + " never set their location while signing up with thyNeighbr. Returning null. BYE BYE");
            return null;
        }

        if (count != null) query.limit(count);

        List<MyTask> records = new ArrayList<MyTask>();
        QueryResultIterator<MyTask> iterator = query.iterator();
        int num = 0;
        logger.info("adding task lists");
        while (iterator.hasNext()) {
            MyTask task = iterator.next();
            if(!helper) {
                double val = checkIfDistanceCriteriaIsMet(originLAT, originLONG, task.getLAT(), task.getLONG());
                logger.info("Task is: " + task.getTaskTitle()
                        + "\nDistance is: " + val
                        + "\nAddress is: " + task.getBeginLocation()
                        + "\nTask create date is: " + task.getCreateDate()
                        + "\nTask update date is: " + task.getUpdateDate()
                        + "\nTask service date is: " + task.getServiceDate()
                        + "\ncurrent date: " + currentDate
                        + "\ntask.getServiceDate().compareTo(currentDate): " + task.getServiceDate().compareTo(currentDate));
                if (true/*val <= distance &&
                        ((task.getServiceDate().compareTo(currentDate)>0) ||
                                (task.getServiceDate().compareTo(currentDate)==0))*/){ // If the current date is less or equal to task service date. Other dates just expire without being helped.
                    logger.info("Less than " + distance + ". Adding " + task.getTaskTitle());
                    task.setDistanceFromOrigin(val);
                    records.add(task);
                } else {
                    logger.info("Greater than " + distance + ". Not Adding " + task.getTaskTitle());
                }
            }
            else{//  helper is set
                logger.info("Adding Task: " + task.getTaskTitle());
                records.add(task);
            }
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
        myTask.setUserProfileName(prof.getFirstName() + " " + prof.getLastName());
        myTask.setCreateDate(new Date());
        myTask.setUpdateDate(new Date());
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
                +   ", Time Range: " + myTask.getServiceTimeRange()
                +   ", LAT: " + myTask.getLAT()
                +   ", LONG: " + myTask.getLONG()
                +   ", CITY: " + myTask.getCity());

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
        myTask.setCreateDate(new Date());
        myTask.setUpdateDate(new Date());



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
    public APIGeneralResult updateMyTask(MyTask myTask, @Named("socialType") int socialType, @Named("socialID") String socialID, HttpServletRequest req) throws ParseException, NullPointerException, APIErrorException{
        // TODO: Subu - This requires me to check if the social
        logger.info("Calling updateMyTask method");
        logger.info("Client inserting a task. Calling updateMyTask method");
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


        MyTask task = null;
        if(myTask.getId() != null){
            task = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class,myTask.getId());
        }
        if(task == null) throw new NullPointerException("Task object is null");

        if(!task.getUserProfileKey().equals(user.getProfileId())){
            logger.severe("Unknown user is trying to update a task not owned by him/her.");
            return new APIGeneralResult(0, "Fail", "Unknown user is trying to update a task not owned by him/her.");
        }
        task.setTaskTitle(myTask.getTaskTitle());
        task.setTaskDescription(myTask.getTaskDescription());
        task.setBeginLocation(myTask.getBeginLocation());
        task.setServiceDate(myTask.getServiceDate());
        task.setServiceToDate(myTask.getServiceToDate());
        task.setLONG(myTask.getLONG());
        task.setLAT(myTask.getLAT());
        task.setCity(myTask.getCity());
        task.setServiceTimeRange(myTask.getServiceTimeRange());
        task.setUpdateDate(new Date());
        try {
            DatastoreHelpers.trySaveEntityOnDatastore(task);
        }
        catch(Exception e){
            return new APIGeneralResult(0, "Fail", "Task Update failed");
        }
        return new APIGeneralResult(1, "OK", "Task Update was successful");
        /*use the enum created in APIGeneralResult.javain this reutrn. Use this function in TaskFragment to check if it returned ok result. if ok, then return true and handle it onpostexecute in the thread.
                in the thread, delete the task from database and update with the new task. then refresh the content in the layout.*/
    }
    @ApiMethod(name = "deleteMyTask", httpMethod = HttpMethod.POST)
    public APIGeneralResult deleteMyTask(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws ParseException, NullPointerException, APIErrorException{
        logger.info("Calling deleteMyTask method");
        MyTask myTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);
        if(!profileID.equals(myTask.getUserProfileKey())){
            logger.info("the profile id is: " + profileID);
            logger.info("The task's profile id is: " + myTask.getUserProfileKey());
            return new APIGeneralResult("Fail", "User is not authenticated to delete this task");
        }
        boolean b = myTask.dispose();
        if(!b) return new APIGeneralResult("Fail", "Task Delete failed");
        return new APIGeneralResult("OK", "Task delete successfully");
    }

    @ApiMethod(name = "updateTaskHelper", httpMethod = HttpMethod.POST, path="mytask/updatetaskhelper")
    public void updateTaskHelper(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling updateTaskHelper method. task id is: " + taskId);
        logger.info("The profile id is : " + profileID);

        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        if(mTask != null && mTask.getHelperUserProfileKey() == null) {
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

            User user1 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", profileID);
            User user2 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", mTask.getUserProfileKey());
            String user1Name = user1.getName().trim();
            if(user1Name.indexOf(" ") != -1) user1Name = user1Name.substring(0,user1Name.indexOf(" ")); //just the first name of user 1
            String user2Name = user2.getName().trim();
            if(user2Name.indexOf(" ") != -1) user2Name = user2Name.substring(0, user2Name.indexOf(" ")); //just the first name of user 2

            GcmSender.sendMessageToDeviceGroup(device.getNotification_key(),"Yay! " + prof.getFirstName() + " " + prof.getLastName() + " is interested in helping you.", user1.getName(), user1.getProfileId(), taskId);

            /* Subu Update Message 12/14/16 */
            /* Saving a notification for the user who is being helped. */
            String content = mTask.getTaskTitle() + ": " + prof.getFirstName() + " " + prof.getLastName() + " is interested in helping you.";
            Message message = Message.createNewMessage(taskId, mTask.getTaskTitle(), mTask.getUserProfileKey(), user2Name, user2.getImageURL(), profileID, user1Name, user1.getImageURL(), true, content, new Date());
            DatastoreHelpers.trySaveEntityOnDatastore(message);
            /* Saving a notification for the user who is helping */
            content = "You have indicated to help " + mTask.getUserProfileName();
            message = Message.createNewMessage(taskId, mTask.getTaskTitle(), profileID, user1Name, user1.getImageURL(), mTask.getUserProfileKey(), user2Name, user2.getImageURL(), true, content, new Date());
            DatastoreHelpers.trySaveEntityOnDatastore(message);
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

        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class,profileID);
        Device device = ofy().load().type(Device.class).filter("profile_key ==", mTask.getUserProfileKey()).first().now();
        if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);

        User user1 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", profileID);
        User user2 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", mTask.getUserProfileKey());
        String user1Name = user1.getName().trim();
        if(user1Name.indexOf(" ") != -1) user1Name = user1Name.substring(0,user1Name.indexOf(" ")); //just the first name of user 1
        String user2Name = user2.getName().trim();
        if(user2Name.indexOf(" ") != -1) user2Name = user2Name.substring(0, user2Name.indexOf(" ")); //just the first name of user 2

        GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), "Unfortunately " + prof.getFirstName() + " " + prof.getLastName() + " has decided to not help because of something that has come up on their side.", user1.getName(), user1.getProfileId(), taskId);

         /* Subu Update Message 12/14/16 */
            /* Saving a notification for the user who is being helped. */
        String content = mTask.getTaskTitle() + ": " + mTask.getHelperProfileName() + " has decided to not help because of something that has come up on their side.";
        Message message = Message.createNewMessage(taskId, mTask.getTaskTitle(), mTask.getUserProfileKey(), user1Name, user1.getImageURL(), profileID, user2Name, user2.getImageURL(), true, content, new Date());
        DatastoreHelpers.trySaveEntityOnDatastore(message);
            /* Saving a notification for the user who is helping */
        content = "You have decided to not help " + mTask.getUserProfileName();
        message = Message.createNewMessage(taskId, mTask.getTaskTitle(), profileID, user2Name, user2.getImageURL(), mTask.getUserProfileKey(), user1Name, user1.getImageURL(), true, content, new Date());
        DatastoreHelpers.trySaveEntityOnDatastore(message);


    }
    @ApiMethod(name = "sendChatMessageToTaskCreator", httpMethod = HttpMethod.POST, path="sendChatMessageToTaskCreator")
    public APIGeneralResult sendChatMessageToTaskCreator(@Named("taskId") Long taskId, @Named("profileID") Long profileID, @Named("message") String message, HttpServletRequest req) throws APIErrorException{

        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        Device device = ofy().load().type(Device.class).filter("profile_key ==", mTask.getUserProfileKey()).first().now();
        if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);


        logger.info("The profile Key for the user who is sending the message: " + profileID);
        logger.info("The profile Key for task creator is " + mTask.getUserProfileKey());
        User user1 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileId", profileID);
        User user2 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileId", mTask.getUserProfileKey());

        if(user1 == null){
            logger.severe("Cant find user: " + profileID);
        }
        if(user2 == null){
            logger.severe("Cant find user: " + mTask.getUserProfileKey());
        }
        if(user1 == null) return new APIGeneralResult("400", "No user exists for the profileID: " + profileID);
        if(user2 == null) return new APIGeneralResult("400", "No user exists for the taskID: " + mTask.getUserProfileKey());
        logger.info("Calling sendChatMessageToTaskCreator method. task id is: " + taskId +
                ", title: " + mTask.getTaskTitle() +
                ", user profile key: " + mTask.getUserProfileKey() +
                ", Sender: " + user1.getName() +
                ", Sender ID: " +profileID +
                ", user image url: " + user1.getImageURL() +
                ", Recipient: " + user2.getName() +
                ", Recipeint ID: " + user2.getProfileId() +
                ", image URL: " + user2.getImageURL() +
                ", message:  " + message);


        String user1Name = user1.getName().trim();
        if(user1Name.indexOf(" ") != -1) user1Name = user1Name.substring(0,user1Name.indexOf(" ")); //just the first name of user 1
        String user2Name = user2.getName().trim();
        if(user2Name.indexOf(" ") != -1) user2Name = user2Name.substring(0, user2Name.indexOf(" ")); //just the first name of user 2
        Message msg = Message.createNewMessage(taskId, mTask.getTaskTitle(), profileID, user1Name ,user1.getImageURL(), user2.getProfileId(), user2Name, user2.getImageURL(), false, message, new Date());
        logger.info("calling GcmSender : Sender: " + user1.getName() +
                "Profile Id: " + user1.getProfileId() +
                " message: " + message);
        GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), message, user1.getName(), profileID, taskId);
        return new APIGeneralResult("200", "Successfully sent message");
    }
    @ApiMethod(name = "sendChatMessageToTaskHelper", httpMethod = HttpMethod.POST, path="sendChatMessageToTaskHelper")
    public APIGeneralResult sendChatMessageToTaskHelper(@Named("taskId") Long taskId, @Named("profileID") Long profileID, @Named("message") String message, HttpServletRequest req) throws APIErrorException{

        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        Device device = ofy().load().type(Device.class).filter("profile_key ==", mTask.getHelperUserProfileKey()).first().now();
        if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);


        User user1 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileId", profileID);
        User user2 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileId", mTask.getHelperUserProfileKey());

        if(user1 == null){
            logger.severe("Cant find user: " + profileID);
        }
        if(user2 == null){
            logger.severe("Cant find user: " + mTask.getHelperUserProfileKey());
        }
        if(user1 == null) return new APIGeneralResult("400", "No user exists for the profileID: " + profileID);
        if(user2 == null) return new APIGeneralResult("400", "No user exists for the taskID: " + mTask.getHelperUserProfileKey());

        logger.info("Calling sendChatMessageToTaskHelper method. Task id: " + taskId +
                ", title: " + mTask.getTaskTitle() +
                ", profile ID: " + profileID +
                ", Sender: " + user1.getName() +
                ", Sender ID: " + user1.getProfileId() +
                ", user profile key: " + mTask.getHelperUserProfileKey() +
                ", Recipient: " + user2.getName() +
                ", Recipient ID: " + user2.getProfileId() +
                ", user image url: " + user1.getImageURL() +
                ", image URL: " + user2.getImageURL() +
                ", message:  " + message);


        String user1Name = user1.getName().trim();
        if(user1Name.indexOf(" ") != -1) user1Name = user1Name.substring(0,user1Name.indexOf(" ")); //just the first name of user 1
        String user2Name = user2.getName().trim();
        if(user2Name.indexOf(" ") != -1) user2Name = user2Name.substring(0, user2Name.indexOf(" ")); //just the first name of user 2
        Message msg = Message.createNewMessage(taskId, mTask.getTaskTitle(), mTask.getUserProfileKey(), user1Name ,user1.getImageURL(), mTask.getHelperUserProfileKey(), user2Name, user2.getImageURL(), false, message, new Date());

        logger.info("calling GcmSender : Sender: " + user1.getName() +
                    "Profile Id: " + user1.getProfileId() +
                    " message: " + message);
        //GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), message);
        GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), message, user1.getName(), user1.getProfileId(), taskId);

        return new APIGeneralResult("200", "Successfully sent message");
    }
    @ApiMethod(name = "getChatRoomMessages", httpMethod = HttpMethod.GET, path="getChatRoomMessages")
    public CollectionResponse<Message> getChatRoomMessages(
                                            @Named("taskId") Long taskId,
                                            @Named("profileID") Long profileID,
                                            HttpServletRequest req) throws APIErrorException {

        Query<Message> query = ofy().load().type(Message.class).filter("taskID", taskId);
        List<Message> records = new ArrayList<>();
        QueryResultIterator<Message> iterator = query.iterator();
        logger.info("Iterating through Chat room messages");
        while (iterator.hasNext()) {
            Message message = iterator.next();
            String str = message.getUserFromName() + ": " + message.getContent();
            logger.info(str);
            records.add(message);
        }
        return CollectionResponse.<Message>builder().setItems(records).build();
    }

    @ApiMethod(name = "getChatRooms", httpMethod = HttpMethod.GET, path="getChatRooms")
    public CollectionResponse<MyTask> getChatRooms(
            @Named("profileID") Long profileID,
            HttpServletRequest req) throws APIErrorException {
        List<MyTask> records = new ArrayList<>();

        Query<Message> query = ofy().load().type(Message.class).project("taskID").distinct(true);

        QueryResultIterator<Message> iterator = query.iterator();
        logger.info("Iterating through ChatRooms");
        while (iterator.hasNext()) {
            Message message = iterator.next();
            MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, message.getTaskID());
            String str = "Task id is: " + message.getTaskID() + ": " + mTask.getTaskTitle();
            logger.info(str);
            records.add(mTask);
        }
        logger.info("done iterating");
        return CollectionResponse.<MyTask>builder().setItems(records).build();
    }

    @ApiMethod(name = "markComplete", httpMethod = HttpMethod.POST)
    public void markComplete(@Named("taskId") Long taskId, @Named("profileID") Long profileID, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling markComplete method. task id is: " + taskId);

        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        mTask.setIsSolved(true);
        logger.info("Saving Task after the task is complete");
        ofy().save().entity(mTask).now();

        log("COMPLETED", profileID, mTask.getUserProfileKey(), taskId, 10);
        /* Retrieve the profile */
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class, profileID);

        //Device device = DatastoreHelpers.tryLoadEntityFromDatastore(Device.class, "profile_key ==", profileID.toString());
        // the above line didnt work because profile_key is Long but the profileID that I was passing is String. So made sure profileID is passed as Long.
        Device device = ofy().load().type(Device.class).filter("profile_key ==", mTask.getUserProfileKey()).first().now();
        if(device == null) throw new APIErrorException(500, "UADU03 - Internal Server Error. Can't find the device based on profile id " +  profileID);

        User user1 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", profileID);
        User user2 = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "profileKey ==", mTask.getUserProfileKey());

        String user1Name = user1.getName().trim();
        if(user1Name.indexOf(" ") != -1) user1Name = user1Name.substring(0,user1Name.indexOf(" ")); //just the first name of user 1
        String user2Name = user2.getName().trim();
        if(user2Name.indexOf(" ") != -1) user2Name = user2Name.substring(0, user2Name.indexOf(" ")); //just the first name of user 2

        GcmSender.sendMessageToDeviceGroup(device.getNotification_key(), "Yay! " + prof.getFirstName() + " " + prof.getLastName() + " has marked the task Complete.", user1.getName(), user1.getProfileId(), taskId);

        /* Subu Update Message 12/14/16 */
            /* Saving a notification for the user who is being helped. */
        String content = mTask.getHelperProfileName() + " is saying that they helped you.";
        Message message = Message.createNewMessage(taskId, mTask.getTaskTitle(), mTask.getUserProfileKey(), user1Name, user1.getImageURL(), profileID, user2Name, user2.getImageURL(), true, content, new Date());
        //DatastoreHelpers.trySaveEntityOnDatastore(message);
            /* Saving a notification for the user who is helping */
        content = "Thank you! You have indicated that you helped " + mTask.getUserProfileName();
        message = Message.createNewMessage(taskId, mTask.getTaskTitle(), profileID, user2Name, user2.getImageURL(), mTask.getUserProfileKey(), user1Name, user1.getImageURL(), true, content, new Date());
        //DatastoreHelpers.trySaveEntityOnDatastore(message);
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

    private double checkIfDistanceCriteriaIsMet(    double originLAT
                                                    , double originLONG
                                                    , double destLAT
                                                    , double destLONG){
        double distance  = ((
        Math.acos(
                Math.sin(originLAT * Math.PI / 180) *
                        Math.sin(destLAT * Math.PI / 180) +
                        Math.cos(originLAT * Math.PI / 180) *
                                Math.cos(destLAT * Math.PI / 180) *
                                Math.cos((originLONG - destLONG) * Math.PI / 180)
        ) * 180 / Math.PI ) * 60 * 1.1515);
       return distance;
    }



}