package com.thyn.backend.entities.log;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.EntityObject;

import java.util.Date;

/**
 * Created by shalu on 10/11/16.
 */
@Entity
public class Log_Action extends EntityObject {
    @Index
    private String action;
    @Index
    private Date actionTime;
    @Index
    private Long userKey;
    @Index
    private Long taskKey;
    private int points;
    @Index
    private Long neighbrWhoIsHelpedKey;

    public static Log_Action createNewUserOnDatastore(String action, Date actionTime, Long userKey, Long neighbrWhoIsHelpedKey, Long taskKey, int points) {
        Log_Action log = new Log_Action(action, actionTime, userKey, neighbrWhoIsHelpedKey, taskKey, points);
        if(DatastoreHelpers.trySaveEntityOnDatastore(log) != null)
            return log;
        return null;
    }
    private Log_Action(){}


    private Log_Action(String action, Date actionTime, Long userKey, Long neighbrWhoIsHelpedKey, Long taskKey, int points){
        this.action = action;
        this.actionTime = actionTime;
        this.userKey = userKey;
        this.taskKey = taskKey;
        this.neighbrWhoIsHelpedKey = neighbrWhoIsHelpedKey;
        this.points = points;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }

    public Long getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(Long taskKey) {
        this.taskKey = taskKey;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Long getNeighbrWhoIsHelpedKey() {
        return neighbrWhoIsHelpedKey;
    }

    public void setNeighbrWhoIsHelpedKey(Long neighbrWhoIsHelpedKey) {
        this.neighbrWhoIsHelpedKey = neighbrWhoIsHelpedKey;
    }


    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean dispose() {
        return DatastoreHelpers.removeEntityFromDatastore(this);
    }
}
