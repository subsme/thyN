package com.thyn.backend.datastore;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import com.thyn.backend.entities.MyTask;

import com.thyn.backend.utilities.Logger;
import com.thyn.backend.entities.users.User;
import com.thyn.backend.entities.users.Profile;
/**
 * Created by subu sundaram on 3/3/16.
 */
public class OfyService {

    static {
        factory().register(MyTask.class);
        factory().register(User.class);
        factory().register(Profile.class);
        Logger.logWarning("Performed Classes registrations");
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}