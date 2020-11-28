package com.example.trainingproject.models;

public class UserProfile {

    private String email;
    private String fullname;
    private String password;
    private int userid;

    public UserProfile(String email, String fullname, String password, int userid) {
        this.email = email;
        this.fullname = fullname;
        this.password = password;
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userid=" + userid +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
