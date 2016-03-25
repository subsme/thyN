package com.thyn.backend.utilities.security;

/**
 * Created by subu sundaram on 3/8/16.
 */
public class ThyNRegisterPackage2 {
    private String email;
    private String password;
    private String name;

    public ThyNRegisterPackage2(){}

    public ThyNRegisterPackage2(String email, String password, String name)
    {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    //public void setEmail(String email){this.email = email;}

    public String getEmail(){
        return this.email;
    }
    public String getPassword(){ return this.password;}
    public String getName(){ return this.name;}
}
