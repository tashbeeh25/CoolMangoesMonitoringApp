package com.example.coolmangoesmonitoringapp;
public class Users {

    //public Users(String id, String usern, String emaill, String fn){}

     public Users(){}
     String user_id;
     String profilePic;
     String username;
     String email;
     String first_name;
     String last_name;
     String status;
     String password;
     String lastMessage;

     //(id, usern, fn, ln, emaill, passwd, imageUri,status);
    public Users(String user_id, String username, String first_name, String last_name,  String email, String password, String profilePic, String status) {
        this.user_id = user_id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.status = status;
    }

    public String getProfilepic() {
        return profilePic;
    }

    public void setProfilepic(String profilepic) {
        this.profilePic = profilepic;
    }

    public String getfirstName() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMail() {
        return email;
    }

    public void setMail(String mail) {
        this.email = mail;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
