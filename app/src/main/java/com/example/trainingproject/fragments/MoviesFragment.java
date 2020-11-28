package com.example.trainingproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trainingproject.R;
import com.example.trainingproject.activities.MovieDetailsActivity;
import com.example.trainingproject.adapters.MovieAdapter;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.utils.RecyclerItemClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoviesFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "MoviesFragment";
    public static boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int offset;
    boolean allMovies;

    ShimmerFrameLayout shimmerFrameLayout;
    View somethingWentWrong;
    View mRoot;
    ChipGroup chipGroup;
    Chip romantic;
    Chip horror;
    Chip comedy;
    Chip scifi;
    Chip action;

    int checkId;

    TextView refreshText;
    TextView errorMsg;
    TextView checkFavourites;

    RecyclerView recyclerView;
    View view;
    MovieAdapter adapter;
    List<Movie> movies = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movies, container, false);
        Log.i(TAG, "onCreateView: ");
       allMovies = true;
        chipGroup = view.findViewById(R.id.chips);
        romantic = view.findViewById(R.id.romance);
        horror = view.findViewById(R.id.horror);
        comedy = view.findViewById(R.id.comedy);
        scifi = view.findViewById(R.id.scifi);
        action = view.findViewById(R.id.action);
        refreshText = view.findViewById(R.id.refresh);
        errorMsg = view.findViewById(R.id.errorMessage);
        checkFavourites = view.findViewById(R.id.checkFavvourites);

        romantic.setOnClickListener(this);
        horror.setOnClickListener(this);
        comedy.setOnClickListener(this);
        scifi.setOnClickListener(this);
        action.setOnClickListener(this);
        refreshText.setOnClickListener(this);
        checkFavourites.setOnClickListener(this);



        Log.i("check select id", String.valueOf(chipGroup.getCheckedChipId()));

        recyclerView = view.findViewById(R.id.recyclerview);
        mRoot = view.findViewById(R.id.mRoot);
        somethingWentWrong = view.findViewById(R.id.somethingWentWrong);

        shimmerFrameLayout = view.findViewById(R.id.moviesShimmer);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MovieAdapter(getContext());
        adapter.setLoading(true);
        recyclerView.setAdapter(adapter);
        adapter.setData(movies);
        offset =0;
        movies.clear();
        chipGroup.clearCheck();
        getMovieList(offset);




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(allMovies){
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = gridLayoutManager.getChildCount();
                        totalItemCount = gridLayoutManager.getItemCount();
                        pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                        if (adapter.isLoading()) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                                adapter.setLoading(false);
                                offset++;
                                getMovieList(offset);

                                Log.i("...", "Last Item Wow !");

                            }
                        }
                    }
                }

            }
        });



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movieId", movies.get(position).getMovieid());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

    }

    public void refresh(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        if(somethingWentWrong.getVisibility()==View.VISIBLE){
            somethingWentWrong.setVisibility(View.GONE);
        }
        movies.clear();
        offset = 0;
        getMovieList(offset);

    }

    public void getMovieList(int page){
        Call<List<Movie>> movieCall = RetrofitClient.getInstance().getApi().getMovies(page,8);
        movieCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

                Log.i(TAG, String.valueOf(response.code()));
                if(response.isSuccessful()){

                    movies.addAll(response.body());
                    Log.i("offset", String.valueOf(offset));

                    adapter.setData(movies);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    adapter.setLoading(true);
                    Log.i(TAG, String.valueOf(loading));

                }else {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    somethingWentWrong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

                if (!isNetworkAvailable()){
                    Snackbar.make(mRoot,"NO INTERNET CONNECTION", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    errorMsg.setText("Seems like you are offline.");
                }
                Log.i(TAG, t.getLocalizedMessage());

                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                somethingWentWrong.setVisibility(View.VISIBLE);
                Log.e(TAG, "onFailure: ",t );
                Log.i(TAG, t.toString());

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getMovieByGenre(String genre){

        Call<List<Movie>> getByGenreCall = RetrofitClient.getInstance().getApi().searchMovieByGenre(genre);
        getByGenreCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if(response.isSuccessful()){
                    Log.i(TAG, String.valueOf(response.code()));
                    movies = response.body();
                    Log.i(TAG, movies.toString());
                    adapter.setData(movies);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                }else {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    somethingWentWrong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                somethingWentWrong.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.refresh){
            refresh();
        }else if(v.getId()==R.id.checkFavvourites){
            Fragment favouritesFragment = new FavouritesFragment();
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,favouritesFragment).commit();
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
            bottomNav.setSelectedItemId(R.id.favourites);


        }else {

            movies.clear();
            adapter.datasetChanged();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            if(somethingWentWrong.getVisibility()==View.VISIBLE){
                somethingWentWrong.setVisibility(View.GONE);
            }


            chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    checkId =checkedId;
                    Log.i("check", String.valueOf(checkId));
                    Log.i("checkedId", String.valueOf(checkedId));
                }
            });
            Log.i("checkout", String.valueOf(checkId));
            if(checkId==-1){
                Log.i("All", "All");
                allMovies = true;
                refresh();
            }else {
                allMovies = false;
                Log.i("else", "else");
                String genre = null;
                switch (v.getId())
                {
                    case R.id.romance:
                        genre= "Romance";
                        Log.i(TAG, "Romantic");
                        break;

                    case R.id.horror:
                        genre = "Horror";
                        Log.i(TAG, "Horror");
                        break;

                    case R.id.comedy:
                        genre = "Comedy";
                        Log.i(TAG, "Comady");
                        break;


                    case R.id.action:
                        genre = "Action";
                        Log.i(TAG, "Action");
                        break;

                    case R.id.scifi:
                        genre = "Science fiction";
                        Log.i(TAG,"Science Fiction");

                    default: break;

                }
                getMovieByGenre(genre);
            }
        }


        
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        //chipGroup.clearCheck();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStart() {
        //chipGroup.clearCheck();
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        chipGroup.clearCheck();
        Log.i(TAG, "onViewStateRestored: ");
    }
}
