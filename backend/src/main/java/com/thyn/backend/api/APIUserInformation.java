package com.thyn.backend.api;



/**
 * Created by shalu on 3/4/16.
 */
public class APIUserInformation {
    public Long profileID;
    public String name;
    public int numNeighbrsHelped;
    public int thyNPoints;

    public Long getProfileID() {
        return profileID;
    }

    public String getName() {
        return name;
    }

    public int getNumNeighbrsHelped() {
        return numNeighbrsHelped;
    }

    public int getThyNPoints() {
        return thyNPoints;
    }

    public APIGeneralResult result;

    public APIGeneralResult getResult() {
        return result;
    }

    public APIUserInformation(APIGeneralResult rslt){

        this.result = rslt;
        this.profileID = null;
        this.numNeighbrsHelped = -1;
        this.thyNPoints = -1;

        this.name = null;
    }

    public APIUserInformation(APIGeneralResult rslt,
                              Long profileID,
                              String name,
                              int numNeighbrsHelped,
                              int thyNPoints
                             ){
        this.result = rslt;
        this.profileID = profileID;
        this.numNeighbrsHelped = numNeighbrsHelped;
        this.thyNPoints = thyNPoints;

        this.name = name;
    }
}
