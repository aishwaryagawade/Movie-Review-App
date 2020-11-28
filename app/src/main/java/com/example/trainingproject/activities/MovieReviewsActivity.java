package com.example.trainingproject.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.adapters.ReviewsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewsActivity extends AppCompatActivity {
    RecyclerView movieReviewsView;

    TextView noReviewsYet;

    List<MovieRating> ratingList;
    List<MovieRating> ratingAndReview = new ArrayList<>();

    ReviewsAdapter reviewsAdapter;
    int movieId;
    public static final String TAG = "MovieReviewsActivity";
    ShimmerFrameLayout reviewShimmer;
    public static final int REVIEW_REQUEST = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);
        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId",-1);
        movieReviewsView = findViewById(R.id.reviewsView);
        noReviewsYet = findViewById(R.id.noReviewsYet);
        reviewShimmer = findViewById(R.id.reviewShimmer);
        reviewShimmer.startShimmer();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        reviewsAdapter = new ReviewsAdapter(getApplicationContext(), false);
        movieReviewsView.setAdapter(reviewsAdapter);
        movieReviewsView.setLayoutManager(linearLayoutManager);
        ratingAndReview.clear();
        refresh();

    }


    public void refresh(){

        Call<List<MovieRating>> ratingCall = RetrofitClient.getInstance().getApi().getMoviewRating(movieId);
        ratingCall.enqueue(new Callback<List<MovieRating>>() {
            @Override
            public void onResponse(Call<List<MovieRating>> call, Response<List<MovieRating>> response) {
                if(response.isSuccessful()){
                    ratingList = response.body();

                    for (MovieRating movieRating: ratingList){

                        if (!movieRating.getComment().equals("")){
                            ratingAndReview.add(movieRating);
                            Log.i(TAG, movieRating.toString());
                        }


                    }
                    if (ratingAndReview.isEmpty()){
                        noReviewsYet.setVisibility(View.VISIBLE);
                    }
                    reviewShimmer.setVisibility(View.GONE);
                    reviewShimmer.stopShimmer();
                    reviewsAdapter.setData(ratingAndReview);

                }
            }

            @Override
            public void onFailure(Call<List<MovieRating>> call, Throwable t) {

            }
        });
    }




    public void addReview(View view){
        Intent intent = new Intent(this, ReviewMovieActivity.class);
        startActivityForResult(intent, REVIEW_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REVIEW_REQUEST && resultCode==RESULT_OK){
            ratingAndReview.clear();
            refresh();
            noReviewsYet.setVisibility(View.INVISIBLE);
        }
    }
}