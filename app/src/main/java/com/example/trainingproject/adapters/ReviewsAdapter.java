package com.example.trainingproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ItemHolder> {



    private static final String TAG = "ItemAdapter";


    List<MovieRating> reviews;
    LayoutInflater inflater;
    Context ctx;
    boolean isMoviePage;
    public ReviewsAdapter(Context ctx , boolean isMoviePage){

        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.isMoviePage = isMoviePage;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_review, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ItemHolder holder, int position) {

        holder.reviewRating.setText(String.valueOf(reviews.get(position).getRating()));
        holder.reviewDate.setText(converDate(reviews.get(position).getTimestamp()));
        holder.reviewComment.setText(reviews.get(position).getComment());

    }



    @Override
    public int getItemCount() {
        if(reviews==null|| reviews.isEmpty()){
            return  0;
        }
        else if(isMoviePage){
            return 1;
        }
        return reviews.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{

        TextView reviewRating;
        TextView reviewDate;
        TextView reviewComment;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            reviewRating = itemView.findViewById(R.id.reviewRating);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            reviewComment = itemView.findViewById(R.id.reviewComment);

        }
    }

    public void setData(List<MovieRating> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }


    public String converDate(String sDate)  {
        String dateFormatted ="";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(sDate);
            dateFormatted = new SimpleDateFormat("dd-MMM-yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }


}
