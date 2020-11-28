package com.example.trainingproject.models;


public class MovieRating {
    private int id;
    private Movie movie;
    private UserProfile user;
    private int rating;
    private String comment;
    private String timestamp;


    public MovieRating(int id, Movie movie, UserProfile user, int rating, String comment,String timestamp) {
        this.id =id;
        this.movie = movie;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MovieRating{" +
                "id=" + id +
                ", movie=" + movie +
                ", user=" + user +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
