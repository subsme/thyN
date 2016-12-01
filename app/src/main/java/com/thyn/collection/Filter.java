package com.thyn.collection;

/**
 * Created by shalu on 11/14/16.
 */
public class Filter {
    public boolean mostRecent;
    public boolean expiringSoon;
    public boolean closestToMyHome;
    public int distanceRadius = 0;

    public Filter(boolean mostRecent, boolean expiringSoon, boolean closestToMyHome, int distanceRadius) {
        this.mostRecent = mostRecent;
        this.expiringSoon = expiringSoon;
        this.closestToMyHome = closestToMyHome;
        this.distanceRadius = distanceRadius;
    }
}
