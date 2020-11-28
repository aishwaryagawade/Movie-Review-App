package com.example.trainingproject.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.trainingproject.fragments.FavouritesFragment;
import com.example.trainingproject.fragments.MoviesFragment;
import com.example.trainingproject.fragments.ProfileFragment;
import com.example.trainingproject.R;
import com.example.trainingproject.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String TAG = "Home Screen" ;
    BottomNavigationView bottomNav;
    SearchView searchView;
    Fragment moviesFragment = new MoviesFragment();
    Fragment favouritesFragment = new FavouritesFragment();
    Fragment profileFragment = new ProfileFragment();
    Fragment searchFragment = new SearchFragment();
    public static String searchText;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        bottomNav = findViewById(R.id.bottom_nav);
        searchView = findViewById(R.id.searchbar);

        bottomNav.setOnNavigationItemSelectedListener(navListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.cointainer_fragment, moviesFragment).commit();
        
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,searchFragment).commit();
                Log.i(TAG, "onClick: ");
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query);
                SearchFragment.noSuchMovie.setVisibility(View.GONE);
                SearchFragment.somethingWentWrong.setVisibility(View.INVISIBLE);
                SearchFragment.searchShimmer.setVisibility(View.VISIBLE);
                SearchFragment.searchShimmer.startShimmer();
                SearchFragment.searchedMovies.clear();
                SearchFragment.search(0,query);
                SearchFragment.offset = 1;
                searchText = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals("")){
                    SearchFragment.noSuchMovie.setVisibility(View.GONE);
                    SearchFragment.somethingWentWrong.setVisibility(View.INVISIBLE);
                    SearchFragment.searchShimmer.setVisibility(View.GONE);
                    SearchFragment.searchShimmer.stopShimmer();
                    SearchFragment.searchedMovies.clear();
                }else {
                    Log.i(TAG, newText);
                    SearchFragment.somethingWentWrong.setVisibility(View.INVISIBLE);
                    SearchFragment.noSuchMovie.setVisibility(View.GONE);
                    SearchFragment.searchShimmer.setVisibility(View.VISIBLE);
                    SearchFragment.searchShimmer.startShimmer();
                    SearchFragment.searchedMovies.clear();
                    SearchFragment.search(0,newText);
                    SearchFragment.offset = 1;
                    searchText = newText;
                }
                return false;
            }
        });

        /*searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {
                    searchView.setIconified(true);
                }
                Log.i(TAG, "onFocusChange: "+String.valueOf(hasFocus));
            }
        });*/



        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                selectFrag(bottomNav.getSelectedItemId());
                return false;
            }
        });

    }

    public void selectFrag(int id){
        switch (id){
            case R.id.movies :
                getSupportFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,moviesFragment).commit();
                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                break;

            case R.id.favourites :
                getSupportFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,favouritesFragment).commit();
                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                break;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,profileFragment).commit();
                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                break;

        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectFrag(menuItem.getItemId());


            return true;
        }
    };


    public static String convertTime(int time){

        int hr = time/60;
        int min = time%60;
        return hr+"h "+min+"m";
    }


}

