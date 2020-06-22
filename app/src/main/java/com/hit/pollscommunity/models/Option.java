package com.hit.pollscommunity;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Option implements Serializable {

    private String optionName;
    private ArrayList<Vote> votes;


    public Option(){}



    public Option(String optionName, ArrayList<Vote> votes){
        this.optionName = optionName;
        this.votes = votes;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getNumberOfVotes() {
        if(votes == null){
            createVotesArray();
        }
        return votes.size();
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }


    public void addVote(Vote vote){
        votes.add(vote);
    }

    public ArrayList<Vote> getVotes() {
        if(votes == null){
            createVotesArray();
        }
        return votes;
    }

    private void createVotesArray(){
        votes = new ArrayList<Vote>();
    }

    @Override
    public String toString() {
        return "Option{" +
                "optionName='" + optionName + '\'' +
                ", votes=" + votes +
                '}';
    }


}
