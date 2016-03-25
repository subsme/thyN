package com.thyn.backend.utilities.security;

/**
 * Created by shalu on 3/4/16.
 */
public class ThyNRegisterPackage {
        private String email;

        public ThyNRegisterPackage(){}

        public ThyNRegisterPackage(String email)
        {
            this.email = email;
        }

        public String getEmail(){
            return this.email;
        }

}
