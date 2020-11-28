package com.example.trainingproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.squareup.picasso.Picasso;

public class ReviewMovieActivity extends AppCompatActivity {

    public static final String TAG = "ReviewMovieActivity";

    RatingBar ratingBar;
    EditText reviewComment;
    ImageView reviewPoster;
    TextView reviewTitle;
    TextView reviewYear;
    int movieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_movie);

        ratingBar = findViewById(R.id.ratingBar2);
        reviewComment = findViewById(R.id.reviewComment);
        reviewPoster = findViewById(R.id.reviewPoster);
        reviewTitle = findViewById(R.id.reviewTitle);
        reviewYear = findViewById(R.id.reviewYear);

        reviewTitle.setText(MovieDetailsActivity.movie.getTitle());
        reviewYear.setText(MovieDetailsActivity.movie.getReleasedate());
        Picasso.with(this).load(MovieDetailsActivity.movie.getImageurl()).placeholder(R.drawable.placeholder).fit().into(reviewPoster);


            if(MovieDetailsActivity.movie.isUserRated()){
                ratingBar.setRating(MovieDetailsActivity.userRatingClass.getRating());
                reviewComment.setText(MovieDetailsActivity.userRatingClass.getComment());
            }


    }

    public void updateReview(View view){

        String comment = reviewComment.getText().toString();
        int rating = (int) ratingBar.getRating();
        if(comment.length()>=5){
            if(MovieDetailsActivity.movie.isUserRated()){
                //update review

                MovieRating movieRating = new MovieRating(MovieDetailsActivity.userRatingClass.getId(),MovieDetailsActivity.movie,MainActivity.userProfile,rating ,comment, RateMovieActivity.getTimestamp());
                Call<ResponseBody> updateRating = RetrofitClient.getInstance().getApi().updateRating(MovieDetailsActivity.userRatingClass.getId(), movieRating);
                updateRating.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(TAG, String.valueOf(response.code()));
                        if(response.isSuccessful()){
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

            }else {
                //new review

                MovieRating movieRating = new MovieRating(-1,MovieDetailsActivity.movie, MainActivity.userProfile,rating,comment, RateMovieActivity.getTimestamp());
                Call<ResponseBody> rateMovieCall = RetrofitClient.getInstance().getApi().rateMovie(movieRating);
                rateMovieCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(TAG, String.valueOf(response.code()));
                        if(response.isSuccessful()){
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i(TAG, "onFailure: ");
                    }
                });
            }
        }else {
            Toast.makeText(this, "Review should be minimum 5 characters long.", Toast.LENGTH_SHORT).show();
        }



    }
}