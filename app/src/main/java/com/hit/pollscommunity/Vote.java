package com.hit.pollscommunity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Vote implements Serializable {
    private String Uid;
    private String fullName;

    public Vote(String Uid,String fullName){
        this.Uid = Uid;
        this.fullName = fullName;
    }

    public Vote(){}

    public String getUid() {
        return Uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "Uid='" + Uid + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
