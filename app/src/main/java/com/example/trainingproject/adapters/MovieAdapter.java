package com.example.trainingproject.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingproject.activities.HomeScreenActivity;
import com.example.trainingproject.models.Movie;
import com.example.trainingproject.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ItemHolder> {

        public static final int MOVIE_CARD = 0;
        public static final int LOADING_MOVIE_CARD = 1;
        private boolean loading = true;
        private boolean offline = false;

        private static final String TAG = "ItemAdapter";


        List<Movie> movies;
        LayoutInflater inflater;
        Context ctx;
        public MovieAdapter(Context ctx){
            this.inflater = LayoutInflater.from(ctx);
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

            View view;
            if(viewType==MOVIE_CARD){
                view = inflater.inflate(R.layout.card_movie, parent, false);
            }else {
                view = inflater.inflate(R.layout.placeholder_movie_card, parent, false);
            }

            return new ItemHolder(view, viewType);
        }



        @Override
        public void onBindViewHolder(@NonNull MovieAdapter.ItemHolder holder, final int position) {
            if(movies.size()>position){
                if(getItemViewType(position)==MOVIE_CARD){
                    if(movies.get(position).getAvgrating()==null){
                        holder.avgRating.setText("-");
                    }else {
                        holder.avgRating.setText(movies.get(position).getAvgrating());
                    }

                    holder.movieTitle.setText(movies.get(position).getTitle());
                    holder.movieYear.setText(movies.get(position).getReleasedate());
                    holder.movieRate.setText(movies.get(position).getRating());
                    holder.movieLength.setText(HomeScreenActivity.convertTime(movies.get(position).getLength()));
                    if(offline){
                        ContextWrapper cw = new ContextWrapper(ctx);
                        File directory = cw.getDir("favourite_movies", Context.MODE_PRIVATE);
                        File myImageFile = new File(directory, movies.get(position).getTitle());
                        Picasso.with(ctx).load(myImageFile).placeholder(R.drawable.placeholder).fit().into(holder.movieImg);
                    }else {
                        Log.i(TAG, "this movie"+movies.get(position).getTitle());
                        Picasso.with(ctx).load(movies.get(position).getImageurl()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).fit().into(holder.movieImg, new Callback() {
                            @Override
                            public void onSuccess() {
                               // Log.i(TAG, "onSuccess: "+movies.get(position).getTitle()+ " "+movies.get(position).getImageurl());
                            }

                            @Override
                            public void onError() {
                                Log.i(TAG, "onError: ");
                            }
                        });
                    }


                }else {
                    holder.shimmer_card.startShimmer();
                }

            }
        }




        @Override
        public int getItemCount() {
            if(movies==null){
                return  0;
            }
            return movies.size();
        }

        public boolean isLoading() {
            return loading;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }


        public boolean isOffline() {
            return offline;
        }

        public void setOffline(boolean offline) {
            this.offline = offline;
        }

        public class ItemHolder extends RecyclerView.ViewHolder{

            ImageView movieImg;
            TextView movieTitle;
            TextView avgRating;
            TextView movieRate;
            TextView movieLength;
            TextView movieYear;
            ShimmerFrameLayout shimmer_card;

            public ItemHolder(@NonNull View itemView, int viewType) {
                super(itemView);
                    if(viewType == MOVIE_CARD){
                        movieImg = itemView.findViewById(R.id.movieImg);
                        movieTitle = itemView.findViewById(R.id.movieitle);
                        avgRating = itemView.findViewById(R.id.avgRating);
                        movieRate = itemView.findViewById(R.id.movieRate);
                        movieLength = itemView.findViewById(R.id.movieLength);
                        movieYear = itemView.findViewById(R.id.movieYear);

                    }
                    else {
                        shimmer_card = itemView.findViewById(R.id.movieShimmerCard);
                    }



            }
        }

        public void setData(List<Movie> movies){
            this.movies = movies;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            Log.i(TAG, String.valueOf(position));
            return super.getItemId(position);

        }

        @Override
        public int getItemViewType(int position) {

            if(loading){
                return MOVIE_CARD;
            }
            else {
                return LOADING_MOVIE_CARD;
            }
        }

        public void datasetChanged(){
            notifyDataSetChanged();
        }
    }


