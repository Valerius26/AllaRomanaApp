package com.example.allaromanaapp;

public class group {
    private String groupID;
    private String title;
    private String description;

    private group(){

    }

    public group(String title, String description, String GroupID){
        this.title = title;
        this.description = description;
        this.groupID = GroupID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

}
