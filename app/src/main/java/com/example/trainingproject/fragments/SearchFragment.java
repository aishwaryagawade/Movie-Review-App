package com.example.trainingproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trainingproject.activities.HomeScreenActivity;
import com.example.trainingproject.activities.MovieDetailsActivity;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.R;
import com.example.trainingproject.utils.RecyclerItemClickListener;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.adapters.SearchAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    RecyclerView searchRecyclerView;
    static SearchAdapter searchAdapter;
    public static List<Movie> searchedMovies = new ArrayList<>();
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public static int offset;
    public static ShimmerFrameLayout searchShimmer;
    public static View somethingWentWrong;
    static TextView errorMsg;
    public static TextView noSuchMovie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searches, container,false);

        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        searchShimmer = view.findViewById(R.id.searchShimmerView);
        searchAdapter = new SearchAdapter(getActivity());
        somethingWentWrong = view.findViewById(R.id.somethingWentWrong);
        errorMsg = view.findViewById(R.id.errorMsg);
        noSuchMovie = view.findViewById(R.id.noSuchMovie);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setAdapter(searchAdapter);

        offset = 1;



        searchRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), searchRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movieId", searchedMovies.get(position).getMovieid());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (searchAdapter.isLoading()) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            searchAdapter.setLoading(false);
                            Log.v("...", "Last Item Wow !");

                            search(offset, HomeScreenActivity.searchText);
                            offset++;
                        }
                    }
                }
            }
        });




        return view;
    }


    public static void search(int page, String text){

        Call<List<Movie>> searchCall = RetrofitClient.getInstance().getApi().searchMovies(page,5,text);
        searchCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.i(TAG, String.valueOf(response.code()));
                if(response.isSuccessful() && response.body()!= null){

                    searchedMovies.addAll(response.body());
                    Log.i(TAG,searchedMovies.toString());
                    if(searchedMovies.isEmpty()){
                        noSuchMovie.setVisibility(View.VISIBLE);
                    }
                    searchShimmer.setVisibility(View.GONE);
                    searchShimmer.stopShimmer();
                    searchAdapter.setData(searchedMovies);
                    searchAdapter.setLoading(true);

                }else {
                    searchShimmer.setVisibility(View.GONE);
                    searchShimmer.stopShimmer();
                    somethingWentWrong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                searchShimmer.setVisibility(View.GONE);
                searchShimmer.stopShimmer();
                errorMsg.setText("Seems like you are offline");
                somethingWentWrong.setVisibility(View.VISIBLE);
            }
        });
    }
}
