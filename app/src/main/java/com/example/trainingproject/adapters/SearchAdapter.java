package com.example.trainingproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingproject.models.Movie;
import com.example.trainingproject.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {


    private static final String TAG = "ItemAdapter";

    public static final int SEARCH_CARD = 0;
    public static final int LOADING_SEARCH_CARD = 1;

    List<Movie> movies;
    LayoutInflater inflater;
    Context ctx;
    private boolean loading = true;

    public SearchAdapter(Context ctx ){


        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == SEARCH_CARD){
            view = inflater.inflate(R.layout.card_search, parent, false);
        }else {
            view = inflater.inflate(R.layout.placeholder_search_movie,parent,false);
        }


        return new ItemHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ItemHolder holder, int position) {
        if(getItemViewType(position)==SEARCH_CARD){
            if(movies.get(position).getAvgrating()==null){
                holder.avgRating.setText("-");
            }else {
                holder.avgRating.setText(movies.get(position).getAvgrating());
            }

            holder.movieTitle.setText(movies.get(position).getTitle()+" ("+movies.get(position).getReleasedate()+")");

            Picasso.with(ctx).load(movies.get(position).getImageurl()).placeholder(R.drawable.placeholder).fit().into(holder.movieImg, new Callback() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: ");
                }

                @Override
                public void onError() {
                    Log.i(TAG, "onError: ");
                }
            });
        }else {
            holder.shimmer_search.startShimmer();
        }


    }


    @Override
    public int getItemCount() {
        if(movies==null){
            return  0;
        }
        return movies.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{

        ImageView movieImg;
        TextView movieTitle;
        TextView avgRating;
        ShimmerFrameLayout shimmer_search;

        public ItemHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType==SEARCH_CARD){
                movieImg = itemView.findViewById(R.id.searchPoster);
                movieTitle = itemView.findViewById(R.id.searchTitle);
                avgRating = itemView.findViewById(R.id.searchAvgRate);
            }
            else {
                shimmer_search = itemView.findViewById(R.id.searchShimmerCard);
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
            return SEARCH_CARD;
        }
        else {
            return LOADING_SEARCH_CARD;
        }
    }


    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}



