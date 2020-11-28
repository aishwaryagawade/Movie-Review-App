package com.example.trainingproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainingproject.activities.MainActivity;
import com.example.trainingproject.activities.MovieDetailsActivity;
import com.example.trainingproject.utils.DBHelper;
import com.example.trainingproject.models.FavouriteMovie;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.adapters.MovieAdapter;
import com.example.trainingproject.R;
import com.example.trainingproject.utils.RecyclerItemClickListener;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.utils.SaveImageHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesFragment extends Fragment  {

    public static final String TAG ="FavouritesFragment";
    private static final int REMOVE_FROM_FAV = 40;
    List<FavouriteMovie> fav;
    List<Movie> favMovies;
    RecyclerView favRecyclerView;
    MovieAdapter movieAdapter;
    ShimmerFrameLayout favShimmer;
    DBHelper dbHelper;
    View mRoot;
    TextView noFavourites;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites,container,false);
        Log.i(TAG, "onCreateView: ");
        favRecyclerView = view.findViewById(R.id.favouritesRecyclerView);
        favShimmer = view.findViewById(R.id.favShimmer);
        mRoot = view.findViewById(R.id.favFrag);
        noFavourites = view.findViewById(R.id.noFavourites);



        dbHelper = new DBHelper(getContext());
        favShimmer.startShimmer();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        favRecyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(getActivity());
        favRecyclerView.setAdapter(movieAdapter);
        favMovies = new ArrayList<>();
        favMovies = dbHelper.getMovieList();
        Log.i(TAG, dbHelper.getMovieList().toString());
        refresh();





        favRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), favRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(isNetworkAvailable()){
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("movieId", favMovies.get(position).getMovieid());
                    startActivityForResult(intent,REMOVE_FROM_FAV);
                }


            }

            @Override
            public void onLongItemClick(View view, final int position) {

                if(isNetworkAvailable()){
                    new AlertDialog.Builder(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                            .setIcon(android.R.drawable.ic_menu_delete)
                            .setTitle("Delete Item")
                            .setMessage("Are you sure you want to delete "+favMovies.get(position).getTitle()+" from your favourites?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<ResponseBody> removeFromFav = RetrofitClient.getInstance().getApi().removeFromFav(fav.get(position).getId(), fav.get(position));
                                    removeFromFav.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if(response.isSuccessful()){
                                                deleteImage(getContext(), favMovies.get(position).getTitle());
                                                refresh();
                                                favMovies.clear();
                                                favShimmer.setVisibility(View.VISIBLE);
                                                favShimmer.startShimmer();
                                                Toast.makeText(getActivity(), "Successfully deleted.", Toast.LENGTH_SHORT).show();



                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }else {

                }

            }
        }));

        return view;

    }

    public static void deleteImage(Context context, String name ){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("favourite_movies", Context.MODE_PRIVATE);
        File myImageFile = new File(directory, name);
            if(myImageFile.delete()){
            Log.i(TAG, "deleted");
        }
            else {
                Log.i(TAG, "not deleteImage: ");
            }
    }

    public void refresh(){

        Call<List<FavouriteMovie>> getFavMovieCall = RetrofitClient.getInstance().getApi().getFavourites(MainActivity.userProfile.getUserid());
        getFavMovieCall.enqueue(new Callback<List<FavouriteMovie>>() {
            @Override
            public void onResponse(Call<List<FavouriteMovie>> call, Response<List<FavouriteMovie>> response) {
                Log.i(TAG, String.valueOf(response.code()));
                if(response.isSuccessful()){
                    favMovies.clear();
                    dbHelper.emptyTable();
                    fav= response.body();

                    if(fav.isEmpty()){
                        noFavourites.setVisibility(View.VISIBLE);
                    }else {
                        noFavourites.setVisibility(View.GONE);
                    }

                    for (FavouriteMovie favouriteMovie : fav){
                        dbHelper.insertMovie(favouriteMovie.getId(),favouriteMovie.getMovie());
                        Picasso.with(getContext()).load(favouriteMovie.getMovie().getImageurl()).into(new SaveImageHelper(getContext(),favouriteMovie.getMovie().getTitle(),"favourite_movies"));
                        favMovies.add(favouriteMovie.getMovie());
                        Log.i(TAG, favouriteMovie.toString());
                    }
                    favShimmer.setVisibility(View.GONE);
                    favShimmer.stopShimmer();
                    movieAdapter.setOffline(false);
                    movieAdapter.setData(favMovies);
                }else {
                    if(getActivity()!=null){
                        Toast.makeText(getContext(), "Something went wrong\nShowing offline content", Toast.LENGTH_LONG).show();
                    }

                    favShimmer.setVisibility(View.GONE);
                    favShimmer.stopShimmer();
                    movieAdapter.setOffline(true);
                    movieAdapter.setData(favMovies);
                }
            }

            @Override
            public void onFailure(Call<List<FavouriteMovie>> call, Throwable t) {
                favShimmer.setVisibility(View.GONE);
                favShimmer.stopShimmer();
                if(getActivity()!=null){
                    Toast.makeText(getActivity(), "Showing offline content", Toast.LENGTH_SHORT).show();
                }

                movieAdapter.setOffline(true);
                movieAdapter.setData(favMovies);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REMOVE_FROM_FAV && resultCode == Activity.RESULT_OK){
            refresh();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
