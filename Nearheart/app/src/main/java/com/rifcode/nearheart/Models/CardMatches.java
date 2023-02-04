package com.rifcode.nearheart.Models;


import java.util.ArrayList;

public class CardMatches {
    private String userId;
    private String username;
    private String photoProfile;
    private String age;
    private String gender;
    private long countImages;

    public CardMatches() {
    }

    public CardMatches(String userId, String username, String photoProfile, String age, String gender, long images) {
        this.userId = userId;
        this.username = username;
        this.photoProfile = photoProfile;
        this.age = age;
        this.gender = gender;
        this.countImages = images;
    }

    public CardMatches(String userId, String username, String photoProfile, String age, String gender) {
        this.userId = userId;
        this.username = username;
        this.photoProfile = photoProfile;
        this.age = age;
        this.gender = gender;
    }

    public long getImages() {
        return countImages;
    }

    public void setImages(long images) {
        this.countImages = images;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
