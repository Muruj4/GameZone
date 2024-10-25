package com.example.gamezone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> allVideos;
    private Context context;

    public VideoAdapter(Context context, List<Video> allVideos) {
        this.context = context;
        this.allVideos = allVideos;
    }
////
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Video video = allVideos.get(position);
        holder.Title.setText(allVideos.get(position).getTitle());

        // Load the video thumbnail using Picasso
        Picasso.get().load(allVideos.get(position).getImageUrl()).into(holder.videoImage);
        holder.vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Bundle b = new Bundle() ;
               b.putSerializable("tournamentData",allVideos.get(position));
                Intent i =new Intent(context, videoPlayer.class);
                i.putExtras(b);
                view.getContext().startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return allVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImage;
        TextView Title;
        View vv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.video);
            Title = itemView.findViewById(R.id.textView5);
            vv=itemView;
        }
    }
}
