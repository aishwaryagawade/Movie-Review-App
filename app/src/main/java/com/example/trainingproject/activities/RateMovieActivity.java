package com.example.trainingproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateMovieActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RateMovie";
    ImageView posterImg;
    ImageView bgPoster;
    TextView rateTitle;
    RatingBar ratingBar;
    TextView rateNo;
    ImageView close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);
        posterImg = findViewById(R.id.ratePoster);
        rateTitle = findViewById(R.id.rateMovieTitle);
        bgPoster = findViewById(R.id.bgPoster);
        ratingBar = findViewById(R.id.ratingBar);
        rateNo = findViewById(R.id.rateNo);
        close = findViewById(R.id.close);

        close.setOnClickListener(this);



        Intent intent = getIntent();



        Picasso.with(this)
                .load(MovieDetailsActivity.movie.getImageurl())
                .fit()
                .transform(new BlurTransformation(this, 25, 4))
                .into(bgPoster);

        Picasso.with(this).load(MovieDetailsActivity.movie.getImageurl()).into(posterImg);
        rateTitle.setText("How would you rate "+ MovieDetailsActivity.movie.getTitle()+ "?");

        if(MovieDetailsActivity.movie.isUserRated()){
            ratingBar.setRating(MovieDetailsActivity.userRatingClass.getRating());
            posterImg.setVisibility(View.INVISIBLE);
            rateNo.setVisibility(View.VISIBLE);
            rateNo.setText(String.valueOf(MovieDetailsActivity.userRatingClass.getRating()));
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                posterImg.setVisibility(View.INVISIBLE);
                rateNo.setVisibility(View.VISIBLE);
                rateNo.setText(String.valueOf((int)rating));
            }
        });
    }


    public void rateMovie(View view){
        final int rating = (int) ratingBar.getRating();


        if (MovieDetailsActivity.movie.isUserRated()) {
            MovieRating movieRating = new MovieRating(MovieDetailsActivity.userRatingClass.getId(),MovieDetailsActivity.movie, MainActivity.userProfile,rating,MovieDetailsActivity.userRatingClass.getComment(),getTimestamp());
            Call<ResponseBody> updateRating = RetrofitClient.getInstance().getApi().updateRating(MovieDetailsActivity.userRatingClass.getId(), movieRating);
            updateRating.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
            MovieRating movieRating = new MovieRating(-1,MovieDetailsActivity.movie, MainActivity.userProfile,rating,"", getTimestamp());
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


    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.close){
            finish();
        }
    }

    public static String getTimestamp(){
        SimpleDateFormat sdf;
        Date date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        return  sdf.format(date);
    }
}