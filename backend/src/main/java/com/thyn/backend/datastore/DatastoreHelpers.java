package com.thyn.backend.datastore;

import java.util.List;

import com.googlecode.objectify.Key;

import static com.thyn.backend.datastore.OfyService.ofy;
import com.thyn.backend.entities.EntityObject;
import com.thyn.backend.entities.users.User;
import com.thyn.backend.utilities.Logger;

/**
 * Helpers used to manage entities on the datastore
 *
 * @author Angelo Agatino Nicolosi
 */
public class DatastoreHelpers {

    public final static <T extends EntityObject> T tryLoadEntityFromDatastore(Class<T> classType, Long id)
    {
        try
        {
            T entity = ofy().load().key(getKey(classType, id)).now();
            return entity;
        }catch(Exception e)
        {
            Logger.logError("Exception in tryLoadEntity.", e);
            return null;
        }
    }

    public final static User tryGetUserByEmailAddress(String email)
    {
        return tryLoadEntityFromDatastore(User.class, "email ==", email);
    }

    public final static <T extends EntityObject> Key<T> trySaveEntityOnDatastore(T entity)
    {
        try
        {
            return ofy().save().entity(entity).now();
        }catch(Exception e)
        {
            Logger.logError("Exception in trySaveEntityOnDatastore.", e);
            return null;
        }
    }

    public final static <T extends EntityObject> boolean removeEntityFromDatastore(T entity)
    {
        try
        {
            ofy().delete().entity(entity).now();
            return true;
        }catch(Exception e)
        {
            Logger.logError("Exception in removeEntityFromDatastore.", e);
            return false;
        }
    }

    public final static <T extends EntityObject> List<T> tryGetAllEntitiesOfAClass(Class<T> classObj)
    {
        try
        {
            return ofy().load().type(classObj).list();
        }catch(Exception e)
        {
            Logger.logError("Exception in tryGetAllEntitiesOfAClass.", e);
            return null;
        }
    }

    private final static <T extends EntityObject> Key<T> getKey(Class<T> classType, Long id) {
        return Key.create(classType, id);
    }

    public final static <T extends EntityObject> T tryLoadEntityFromDatastore(Class<T> classType, String operator, String value)
    {
        try
        {
            T entity = ofy().load().type(classType).filter(operator, value).first().now();
            return entity;
        }catch(Exception e)
        {
            Logger.logError("Exception in tryLoadEntity.", e);
            return null;
        }
    }
    public final static <T extends EntityObject> T tryLoadEntityFromDatastore(Class<T> classType, String operator, Long value)
    {
        try
        {
            T entity = ofy().load().type(classType).filter(operator, value).first().now();
            return entity;
        }catch(Exception e)
        {
            Logger.logError("Exception in tryLoadEntity.", e);
            return null;
        }
    }


}
