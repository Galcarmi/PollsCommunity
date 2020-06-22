package com.hit.pollscommunity;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class Poll  implements Serializable {
    private String pollName;
    private ArrayList<Option> options;
    private User creator;
    private String key;
    private String createdAt;


    public Poll(){}

    public Poll(String name , ArrayList<Option> options , User creator,String createdAt){
        this.pollName = name;
        this.options = options;
        this.creator = creator;
        this.createdAt = createdAt;

    }

    public String getPollName() {
        return pollName;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public User getCreator() {
        return creator;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Poll{" +
                "pollName='" + pollName + '\'' +
                ", options=" + options +
                ", creator=" + creator +
                '}';
    }
}
