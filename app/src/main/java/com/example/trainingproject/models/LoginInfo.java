package com.example.trainingproject.models;

public class LoginInfo {
    private  int id;
    private ProfilePicture uploadedFile;
    private UserProfile user;

    public LoginInfo(int id, ProfilePicture uploadedFile, UserProfile user) {
        this.id = id;
        this.uploadedFile = uploadedFile;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public ProfilePicture getUploadedFile() {
        return uploadedFile;
    }

    public UserProfile getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", uploadedFile=" + uploadedFile +
                ", user=" + user +
                '}';
    }
}
