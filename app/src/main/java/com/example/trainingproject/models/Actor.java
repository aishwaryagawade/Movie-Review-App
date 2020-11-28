package com.example.trainingproject.models;

public class Actor {
    private int actorid;
    private String firstname;
    private String lastname;
    private String imageurl;

    public Actor(int actorid, String firstname, String lastname, String imageurl) {
        this.actorid = actorid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.imageurl = imageurl;
    }

    public int getActorid() {
        return actorid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getImageurl() {
        return imageurl;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "actorid=" + actorid +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}
