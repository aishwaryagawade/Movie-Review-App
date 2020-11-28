package com.example.trainingproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingproject.models.Actor;
import com.example.trainingproject.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ActorHolder> {

    private static final String TAG = "ActorsAdapter";
    ArrayList<Actor> actors;
    LayoutInflater inflater;
    Context ctx;
    public ActorsAdapter(Context ctx, ArrayList<Actor> actors){
        this.ctx = ctx;
        this.actors = actors;
        this.inflater = LayoutInflater.from(ctx);

    }

    @NonNull
    @Override
    public ActorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_actor,parent,false);
        return new ActorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorHolder holder, int position) {

        holder.actorName.setText(actors.get(position).getFirstname()+"\n"+actors.get(position).getLastname());
        Picasso.with(ctx).load(actors.get(position).getImageurl()).placeholder(R.drawable.person_placeholder).resize(120, 180).centerCrop().into(holder.actorImg, new Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ");
            }

            @Override
            public void onError() {

            }
        });

    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ActorHolder extends  RecyclerView.ViewHolder{

        ImageView actorImg;
        TextView actorName;

        public ActorHolder(@NonNull View itemView) {
            super(itemView);
            actorImg = itemView.findViewById(R.id.actorImg);
            actorName = itemView.findViewById(R.id.actorName);

        }
    }

}
