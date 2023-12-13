package com.example.coolmangoesmonitoringapp;

public class MsgModelClass {
    String message;
    String senderid;
    long timeStamp;
    String voiceUrl;
    String imageUrl;  // Add this field for storing the image URL

    public MsgModelClass() {
    }

    public MsgModelClass(String message, String senderid, long timeStamp, String imageUrl, String voiceUrl) {
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
        this.imageUrl = imageUrl;
        this.voiceUrl = voiceUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageUri() {
        return imageUrl;
    }

    public void setImageUri(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }
}
