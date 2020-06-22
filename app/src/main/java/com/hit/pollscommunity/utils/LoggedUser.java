package com.hit.pollscommunity;

public class LoggedUser {

    private static User user;

    private LoggedUser(){};

    public static User getInstance(){
        if(user==null){
            user = new User();
        }
        return user;
    }

    public static void setUser(User newuser){

        user.setAdmin(newuser.getAdmin());
        user.setFirstName(newuser.getFirstName());
        user.setLastName(newuser.getLastName());
        user.setUid(newuser.getUid());
    }
}
