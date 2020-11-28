package com.example.trainingproject.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingproject.BuildConfig;
import com.example.trainingproject.adapters.ActorsAdapter;
import com.example.trainingproject.fragments.FavouritesFragment;
import com.example.trainingproject.utils.DBHelper;
import com.example.trainingproject.models.FavouriteMovie;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.adapters.ReviewsAdapter;
import com.example.trainingproject.utils.SaveImageHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MovieDetails";

    int movieId;
    boolean isFavourite;

    ImageView posterMovie;
    ImageView shareMovie;

    TextView movieTitle;
    TextView movieYear;
    TextView movieRate;
    TextView movieLength;
    TextView movieGenre;
    TextView movieDesc;
    TextView avgRating;
    TextView userRating;
    TextView noReviews;

    ShimmerFrameLayout shimmerFrameLayout;

    ConstraintLayout movieDetail;
    ConstraintLayout cast;
    ConstraintLayout constraintLayoutReviews;
    View somethingWentWrong;


    RecyclerView actorsView;
    ActorsAdapter actorsAdapter;
    ReviewsAdapter reviewsAdapter;

    static Movie movie;
    static MovieRating userRatingClass;

    Button addToFav;
    ImageView rateNow;
    List<MovieRating> ratingAndReview = new ArrayList<>();
    RecyclerView commentsView;
    ShimmerFrameLayout movieReviewShimmer;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", 0);
        dbHelper = new DBHelper(this);
        isFavourite = dbHelper.isPresent(movieId);
        Log.i("columnid", String.valueOf(dbHelper.getId(movieId)));

        Log.i("isPresent", String.valueOf(dbHelper.isPresent(movieId)));

        somethingWentWrong = findViewById(R.id.somethingWentWrong);
        posterMovie = findViewById(R.id.moviePoster);
        movieTitle = findViewById(R.id.titleMovie);
        movieYear = findViewById(R.id.yearMovie);
        movieRate = findViewById(R.id.rateMovie);
        movieLength = findViewById(R.id.lengthMovie);
        movieGenre = findViewById(R.id.movieGenre);
        movieDesc = findViewById(R.id.movieDescription);
        avgRating = findViewById(R.id.movieRating);
        posterMovie.setImageResource(R.drawable.placeholder);
        shimmerFrameLayout = findViewById(R.id.shimmerView_container);
        cast = findViewById(R.id.cast);
        constraintLayoutReviews = findViewById(R.id.constraintLayoutReview);
        noReviews = findViewById(R.id.noReviews);
        shimmerFrameLayout.startShimmer();
        movieDetail = findViewById(R.id.movieDetail);
        actorsView = findViewById(R.id.actorsRecyclerView);
        addToFav = findViewById(R.id.addToFav);
        rateNow = findViewById(R.id.rateNow);
        userRating = findViewById(R.id.userRating);
        shareMovie = findViewById(R.id.shareMovie);
        commentsView = findViewById(R.id.reviewsRecyclerView);
        movieReviewShimmer = findViewById(R.id.movieReviewShimmer);
        movieReviewShimmer.startShimmer();
        reviewsAdapter = new ReviewsAdapter(this, true);
        LinearLayoutManager commentsMnager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        commentsView.setLayoutManager(commentsMnager);
        commentsView.setAdapter(reviewsAdapter);

        String path = "";
        if (path.isEmpty()) {
            File file = new File(this.getFilesDir(), "favourite_movies");
            path = file.getAbsolutePath();
            Log.d("PATH", path);
        }

        rateNow.setOnClickListener(this);
        shareMovie.setOnClickListener(this);

        refresh();




    }


    public void refresh(){
        final Call<Movie> movieDetails = RetrofitClient.getInstance().getApi().getMovieDetails(movieId);
        movieDetails.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                if(response.isSuccessful()){
                    movie = response.body();


                    Picasso.with(getApplication()).load(movie.getImageurl()).placeholder(R.drawable.placeholder).fit().into(posterMovie, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {
                            Log.i(TAG, "onError: ");
                        }
                    });

                    movieTitle.setText(movie.getTitle());
                    movieYear.setText(movie.getReleasedate());
                    movieRate.setText(movie.getRating());
                    movieLength.setText(HomeScreenActivity.convertTime(movie.getLength()));
                    movieGenre.setText(movie.getGenre());
                    movieDesc.setText(movie.getDescription());
                    if (movie.getAvgrating()==null){
                        avgRating.setText("-");
                    }else {

                        avgRating.setText(movie.getAvgrating()+"/5");
                    }
                    setFavouriteBtn();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    movieDetail.setVisibility(View.VISIBLE);
                    cast.setVisibility(View.VISIBLE);
                    constraintLayoutReviews.setVisibility(View.VISIBLE);
                    actorsAdapter = new ActorsAdapter(getApplicationContext(), movie.getActor());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    actorsView.setAdapter(actorsAdapter);
                    actorsView.setLayoutManager(linearLayoutManager);
                    getReviews();
                }else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    somethingWentWrong.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                somethingWentWrong.setVisibility(View.VISIBLE);
            }
        });

    }

    public void getReviews(){
        Call<List<MovieRating>> ratingCall = RetrofitClient.getInstance().getApi().getMoviewRating(movieId);
        ratingCall.enqueue(new Callback<List<MovieRating>>() {
            @Override
            public void onResponse(Call<List<MovieRating>> call, Response<List<MovieRating>> response) {
                if(response.isSuccessful()){

                    List<MovieRating> ratingList = response.body();
                    ratingAndReview.clear();
                    for (MovieRating movieRating: ratingList){

                        if(movieRating.getUser().getUserid()== MainActivity.userProfile.getUserid()){
                            setRating(movieRating.getRating());
                            userRatingClass = movieRating;
                            movie.setUserRated(true);
                        }
                        if (!movieRating.getComment().equals("")){
                            ratingAndReview.add(movieRating);
                            Log.i(TAG, movieRating.toString());
                        }
                    }
                    Log.i(TAG, String.valueOf(movie.isUserRated()));
                    if (ratingAndReview.isEmpty()){
                        noReviews.setVisibility(View.VISIBLE);
                    }else{
                        noReviews.setVisibility(View.INVISIBLE);
                    }
                    movieReviewShimmer.setVisibility(View.GONE);
                    movieReviewShimmer.stopShimmer();
                    reviewsAdapter.setData(ratingAndReview);

                }
            }

            @Override
            public void onFailure(Call<List<MovieRating>> call, Throwable t) {

            }
        });

    }

    public void addToFav(View view){

        if(!isFavourite){
            FavouriteMovie favouriteMovie = new FavouriteMovie(-1,movie, MainActivity.userProfile);

            Call<ResponseBody> addToFavCall = RetrofitClient.getInstance().getApi().addToFav(favouriteMovie);
            addToFavCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    isFavourite = true;
                    setFavouriteBtn();
                    Picasso.with(getApplicationContext()).load(movie.getImageurl()).into(new SaveImageHelper(getApplicationContext(),movie.getTitle(),"favourite_movies"));

                    addToDb();


                    Log.i(TAG, String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAG, "onFailure: ");
                }
            });


        }
        else {

            Call<ResponseBody> removeFromFav = RetrofitClient.getInstance().getApi().removeFromFav(dbHelper.getId(movieId), new FavouriteMovie(dbHelper.getId(movieId),movie,MainActivity.userProfile));
            removeFromFav.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.i("delete", String.valueOf(response.code()));
                    if(response.isSuccessful()){
                        isFavourite = false;
                        setFavouriteBtn();
                        FavouritesFragment.deleteImage(getApplicationContext(), movie.getTitle());
                        dbHelper.deleteMovie(movieId);
                        setResult(RESULT_OK);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


        }

    }

    private void addToDb() {

        Call<List<FavouriteMovie>> getFavMovieCall = RetrofitClient.getInstance().getApi().getFavourites(MainActivity.userProfile.getUserid());
        getFavMovieCall.enqueue(new Callback<List<FavouriteMovie>>() {
            @Override
            public void onResponse(Call<List<FavouriteMovie>> call, Response<List<FavouriteMovie>> response) {
                if(response.isSuccessful()){
                    for(FavouriteMovie favouriteMovie: response.body()){
                        if(favouriteMovie.getMovie().getMovieid()==movieId){
                            dbHelper.insertMovie(favouriteMovie.getId(),favouriteMovie.getMovie());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FavouriteMovie>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.rateNow){
            Log.i(TAG, "onClick: ");

            Intent intent = new Intent(this, RateMovieActivity.class);
            intent.putExtra("movieId",movie.getMovieid());
            intent.putExtra("Title",movie.getTitle());
            intent.putExtra("imgUrl",movie.getImageurl());
            startActivityForResult(intent,1);
        }else if(v.getId() == R.id.shareMovie){
            Log.i(TAG, "share movie");
            String msg;
            Intent intent = new Intent(Intent.ACTION_SEND);
            if(movie.getAvgrating()==null){
                msg = "Be the first one to rate it.";
            }else {
                msg = "It has "+movie.getAvgrating()+" average rating out of 5.";
            }
            Drawable drawable=posterMovie.getDrawable();
            Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();

            try {

                File file = new File(getApplicationContext().getExternalCacheDir(), File.separator +movie.getTitle()+".jpg");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);

                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);
                intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            }catch (Exception e){

            }

            intent.putExtra(Intent.EXTRA_TEXT,"Hey, Checkout "+movie.getTitle()+", "+movie.getRating()+" rated movie released in "+movie.getReleasedate()+ ".\n"+msg);
            intent.setType("image/JPG");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Send via"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1 && resultCode == RESULT_OK && data!=null){
            refresh();
        }
        if(requestCode == 2 && resultCode == RESULT_OK){
            refresh();
        }
    }

    public void setRating(int rating){
        userRating.setText(rating+"/5");
        rateNow.setImageResource(R.drawable.ic_baseline_blue_star);
    }

    public void seeComments(View view){
        Intent intent = new Intent(this, MovieReviewsActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    public void addReview(View view){
        Intent intent = new Intent(this, ReviewMovieActivity.class);
        startActivityForResult(intent,2);
    }

    public void setFavouriteBtn(){
        if(isFavourite){

            Drawable drawable = addToFav.getContext().getDrawable(R.drawable.ic_baseline_favorite_24);
            addToFav.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            addToFav.setText(R.string.addedToFav);

        }else {
            isFavourite=false;
            addToFav.setText(R.string.addToFav);
            Drawable drawable = addToFav.getContext().getDrawable(R.drawable.ic_outline_favorite_border_24);
            addToFav.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReviews();
    }

}