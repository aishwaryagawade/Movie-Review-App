package com.example.trainingproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserReviewsAdapter extends RecyclerView.Adapter<UserReviewsAdapter.ItemHolder> {


    private static final String TAG = "ItemAdapter";


    List<MovieRating> reviews;
    LayoutInflater inflater;
    Context ctx;

    public UserReviewsAdapter(Context ctx) {

        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_user_review, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewsAdapter.ItemHolder holder, int position) {

        Picasso.with(ctx).load(reviews.get(position).getMovie().getImageurl()).placeholder(R.drawable.placeholder).fit().into(holder.posterImg);
        holder.movieTitle.setText(reviews.get(position).getMovie().getTitle()+" ("+reviews.get(position).getMovie().getReleasedate()+")");
        holder.userRating.setText(String.valueOf(reviews.get(position).getRating()));
        if(!reviews.get(position).getComment().equals(""))
        holder.userComment.setText("\""+reviews.get(position).getComment()+"\"");

    }


    @Override
    public int getItemCount() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        return reviews.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        ImageView posterImg;
        TextView movieTitle;
        TextView userRating;
        TextView userComment;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            posterImg = itemView.findViewById(R.id.userMovieReviewPoster);
            movieTitle = itemView.findViewById(R.id.userReviewMovieTitle);
            userRating = itemView.findViewById(R.id.userViewRating);
            userComment = itemView.findViewById(R.id.userMovieComment);

        }
    }

    public void setData(List<MovieRating> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}