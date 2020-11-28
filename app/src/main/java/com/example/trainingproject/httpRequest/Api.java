package com.example.trainingproject.httpRequest;

import com.example.trainingproject.models.FavouriteMovie;
import com.example.trainingproject.models.LoginInfo;
import com.example.trainingproject.models.LoginUser;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.models.ProfilePicture;
import com.example.trainingproject.models.RegUser;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {




    @POST("register")
    Call<ResponseBody> register(@Body RegUser regUser);

    @POST("login")
    Call<LoginInfo> login(@Body LoginUser loginUser);

    @GET("movies")
    Call<List<Movie>> getMovies(@Query("offset") int offset, @Query("limit") int limit);

    @GET("movie_id")
    Call<Movie> getMovieDetails(@Query("id") int id);

    @GET("movies")
    Call<List<Movie>> searchMovies(@Query("offset") int offset, @Query("limit") int limit, @Query("title") String title);

    @GET("movies_genre")
    Call<List<Movie>> searchMovieByGenre(@Query("genre") String genre);

    @POST("favorite_movie")
    Call<ResponseBody> addToFav(@Body FavouriteMovie favouriteMovie);

    @POST("movie_rating")
    Call<ResponseBody> rateMovie(@Body MovieRating movieRating);

    @GET("favourite_movies")
    Call<List<FavouriteMovie>> getFavourites(@Query("id") int id);

    @HTTP(method = "DELETE", path = "favourite_movie", hasBody = true)
    Call<ResponseBody> removeFromFav(@Query("id") int id, @Body FavouriteMovie favouriteMovie);

    @GET("rating_movieId")
    Call<List<MovieRating>> getMoviewRating(@Query("movieid") int movieid);

    @PUT("movie_rating")
    Call<ResponseBody> updateRating(@Query("id") int id, @Body MovieRating movieRating);

    @GET("rating_userId")
    Call<List<MovieRating>> getUserMovieRatings(@Query("userid") int id);

    @Multipart
    @POST("upload")
    Call<ProfilePicture> updateProfile(@Part MultipartBody.Part image);
}
