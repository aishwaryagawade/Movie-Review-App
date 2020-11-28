package com.example.trainingproject.models;

public class RegUser {
    private String email;
    private String fullname;
    private String password;

    public RegUser(String email, String fullname, String password) {
        this.email = email;
        this.fullname = fullname;
        this.password = password;
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


}
