package com.example.gamezone;

import android.content.Context;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = allVideos.get(position);
        holder.Title.setText(video.getTitle());

        // Load the video thumbnail using Picasso
        Picasso.get().load(video.getImageUrl()).into(holder.videoImage);
    }

    @Override
    public int getItemCount() {
        return allVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImage;
        TextView Title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.video);
            Title = itemView.findViewById(R.id.textView5);
        }
    }
}
