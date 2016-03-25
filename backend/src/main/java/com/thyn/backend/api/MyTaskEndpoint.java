package com.thyn.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.api.services.datastore.client.DatastoreHelper;
import com.googlecode.objectify.cmd.Query;
import com.thyn.backend.entities.MyTask;
import com.thyn.backend.entities.users.User;
import com.thyn.backend.utilities.security.SecurityUtilities;
import com.thyn.backend.utilities.security.ThyNSession;

import java.util.logging.Logger;
import java.util.Date;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import static com.thyn.backend.datastore.OfyService.ofy;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.users.Profile;

import java.util.List;
import java.util.ArrayList;



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
        )
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
                                                HttpServletRequest req) throws APIErrorException{
        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        Long profileID = sessionUser.getProfileId();
        Query<MyTask> query = null;
        if(helper){
            if(!isSolved) query = ofy().load().type(MyTask.class).filter("helperUserProfileKey",profileID);
            else query = ofy().load().type(MyTask.class).filter("helperUserProfileKey",profileID).filter("isSolved",true);
        }
        else {
            if(!isSolved) query = ofy().load().type(MyTask.class);
            else query = ofy().load().type(MyTask.class).filter("isSolved",true);
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
    public void insertMyTask(MyTask myTask, HttpServletRequest req) throws ConflictException, APIErrorException{
        // TODO: Implement this function
        logger.info("Calling insertMyTask method");
        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        if(myTask.getId() != null){
            if(DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class,myTask.getId()) != null){
                throw new ConflictException("Object already exists");
            }
        }
        /* Setting the user name in myTask is Redundant.
        But thats the easy way to get all the task information when TaskListFragment calls the server */
        Long profileID = sessionUser.getProfileId();
        Profile prof = DatastoreHelpers.tryLoadEntityFromDatastore(Profile.class,profileID);
        myTask.setUserProfileName(prof.getFirstName()+ " " + prof.getLastName());
        myTask.setCreateDate(new Date().toString());
        ofy().save().entity(myTask).now();
        //myTask.setTaskDescription("Mutating the task description: " + myTask.getTaskDescription());

    }

    @ApiMethod(name = "updateTaskHelper", httpMethod = HttpMethod.POST)
    public void updateTaskHelper(@Named("taskId") Long taskId, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling updateTaskHelper method. task id is: " + taskId);

        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        Long profileID = sessionUser.getProfileId();
        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        if(mTask.getHelperUserProfileKey() == null) {
            mTask.setHelperUserProfileKey(profileID);
            logger.info("Saving Task with new helper user profile key");
            ofy().save().entity(mTask).now();

        }
        else
        {
            com.thyn.backend.utilities.Logger.logWarning("Exception while assigning Task to HELPER (" + sessionUser.getId() + ").");
            throw new APIErrorException(500, "UADU02 - Internal Server Error.");
        }
    }

    @ApiMethod(name = "markComplete", httpMethod = HttpMethod.POST)
    public void markComplete(@Named("taskId") Long taskId, HttpServletRequest req) throws APIErrorException
    {
        logger.info("Calling markComplete method. task id is: " + taskId);

        ThyNSession currentSession = SecurityUtilities.enforceAuthenticationForCurrentAPICall(req);
        User sessionUser = currentSession.getSessionUser();
        if(sessionUser == null)
            throw new APIErrorException(401, "UADU01 - Unauthorized.");

        //Long profileID = sessionUser.getProfileId();
        MyTask mTask = DatastoreHelpers.tryLoadEntityFromDatastore(MyTask.class, taskId);

        mTask.setIsSolved(true);
        logger.info("Saving Task after the task is complete");
        ofy().save().entity(mTask).now();

    }

   /* private MyTask findRecord(Class<T> classType, Long key) {
        return ofy().load().type(classType).id(key).get();
//or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }*/

}