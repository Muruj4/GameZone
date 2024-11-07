package com.example.gamezone;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_CATEGORY_HEADER = 1;

    private List<Video> allVideos;
    private Map<String, List<Video>> categorizedVideos;
    private List<Object> displayList;
    private Context context;

    public VideoAdapter(Context context, List<Video> allVideos) {
        this.context = context;
        this.allVideos = allVideos;
        this.categorizedVideos = new HashMap<>();
        this.displayList = new ArrayList<>(allVideos);
    }

    public void setCategorizedData(Map<String, List<Video>> categorizedVideos) {
        this.categorizedVideos = categorizedVideos;
        updateDisplayListWithCategories();
        notifyDataSetChanged();
    }

    public void setFilteredList(List<Video> filteredVideos) {
        this.displayList = new ArrayList<>(filteredVideos);
        notifyDataSetChanged();
    }

    private void updateDisplayListWithCategories() {
        displayList.clear();
        for (Map.Entry<String, List<Video>> entry : categorizedVideos.entrySet()) {
            displayList.add(entry.getKey());
            displayList.addAll(entry.getValue());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (displayList.get(position) instanceof String) ? TYPE_CATEGORY_HEADER : TYPE_VIDEO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_header, parent, false);
            return new CategoryHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview, parent, false);
            return new VideoViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryHeaderViewHolder) {
            String category = (String) displayList.get(position);
            ((CategoryHeaderViewHolder) holder).bind(category);
        } else if (holder instanceof VideoViewHolder) {
            Video video = (Video) displayList.get(position);
            ((VideoViewHolder) holder).bind(video);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ViewHolder for category headers
    // ViewHolder for category headers
    public static class CategoryHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;

        public CategoryHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
        }


        public void bind(String category) {
            categoryTitle.setText(category);
            categoryTitle.setAlpha(0f);
            categoryTitle.animate().alpha(1f).setDuration(500).start(); // Fade-in effect

            switch (category) {
                case "Single Player":
                    categoryTitle.setBackgroundColor(Color.parseColor("#FF0B3347"));
                    break;
                case "Top Prizes":
                    categoryTitle.setBackgroundColor(Color.parseColor("#FF0B3347"));
                    break;
                case "Multiplayer":
                    categoryTitle.setBackgroundColor(Color.parseColor("#FF0B3347"));
                    break;
                default:
                    categoryTitle.setBackgroundColor(Color.parseColor("#FF0B3347"));
            }
        }

    }


    // ViewHolder for video items

    // ViewHolder for video items
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView videoImage;
        private final TextView title;
        private final View videoContainer;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.video);
            title = itemView.findViewById(R.id.textView5);
            videoContainer = itemView;
        }

        public void bind(Video video) {
            if (video != null) {  // Check if video is not null
                title.setText(video.getTitle());
                Picasso.get().load(video.getImageUrl()).into(videoImage);

                videoContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(context, videoPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videoData", video);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                });
            } else {
                Log.e("VideoAdapter", "Null video object encountered in bind method.");
            }
        }
    }
}
