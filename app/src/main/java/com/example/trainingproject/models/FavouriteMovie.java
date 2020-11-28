package com.example.trainingproject.models;

public class FavouriteMovie {

    private int id;
    private Movie movie;
    private UserProfile user;



    public FavouriteMovie(int id, Movie movie, UserProfile user) {
        this.id = id;
        this.movie = movie;
        this.user = user;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FavMovie{" +
                "id=" + id +
                ", movie=" + movie +
                ", user=" + user +
                '}';
    }
}
