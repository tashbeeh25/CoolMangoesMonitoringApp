package com.example.coolmangoesmonitoringapp;

public class Groups {

    public Groups(){}
    String group_id;
    String name;
    //String posts;


    public Groups(String group_id, String name, String posts) {
        this.group_id = group_id;
        this.name = name;
        //this.posts = posts;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //public String getPosts() {
    //    return posts;
    //}

    //public void setPosts(String posts) {
    //    this.posts = posts;
    //}


}

