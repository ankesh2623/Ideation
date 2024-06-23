package com.example.ideation.Model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String userName;
    private String profession;
    private String userID;
    private String imageURL;
    private String bio;
    private String Address;
    private int followCount;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserModel() { }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


    public UserModel(String userName, String profession, String userID, String address, String imageURL) {
        this.userName = userName;
        this.profession = profession;
        this.userID = userID;
        this.followCount=0;
        this.Address=address;
        this.imageURL=imageURL;
        this.bio = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
