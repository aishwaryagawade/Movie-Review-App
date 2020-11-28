package com.example.trainingproject.models;

import java.util.ArrayList;

public class Movie {

            private int movieid;
            private String title;
            private String description;
            private String imageurl;
            private String releasedate;
            private int length;
            private String rating;
            private String avgrating;
            private String genre;
            private ArrayList<Actor> actor;
            private boolean isUserRated = false;


    public Movie() {

    }

    public Movie(int movieid, String title, String description, String imageurl, String releasedate, int length, String rating, String avgrating, String genre, ArrayList<Actor> actor) {
        this.movieid = movieid;
        this.title = title;
        this.description = description;
        this.imageurl = imageurl;
        this.releasedate = releasedate;
        this.length = length;
        this.rating = rating;
        this.avgrating = avgrating;
        this.genre = genre;
        this.actor = actor;
    }



    public int getMovieid() {
        return movieid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public int getLength() {
        return length;
    }

    public String getRating() {
        return rating;
    }

    public String getAvgrating() {
        return avgrating;
    }

    public String getGenre() {
        return genre;
    }

    public void setMovieid(int movieid) {
        this.movieid = movieid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setAvgrating(String avgrating) {
        this.avgrating = avgrating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setActor(ArrayList<Actor> actor) {
        this.actor = actor;
    }

    public ArrayList<Actor> getActor() {
        return actor;
    }

    public boolean isUserRated() {
        return isUserRated;
    }

    public void setUserRated(boolean userRated) {
        isUserRated = userRated;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieid=" + movieid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", releasedate='" + releasedate + '\'' +
                ", length='" + length + '\'' +
                ", rating=" + rating +
                ", avgrating=" + avgrating +
                ", genre='" + genre + '\'' +
                ", actor=" + actor +
                '}';
    }
}
