package com.example.gamezone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.MediaController;

public class videoPlayer extends AppCompatActivity {

    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Back button setup
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Bundle data= intent.getExtras();
        Video v=(Video) data.getSerializable("tournamentData");

                TextView title = findViewById(R.id.videoTitle);
                TextView desc = findViewById(R.id.videoDescription);
                VideoView videoPlayer = findViewById(R.id.videoView3);

                title.setText(v.getTitle());
                desc.setText(v.getDescription());

                Uri videoUrl = Uri.parse(v.getVideoUrl());
                videoPlayer.setVideoURI(videoUrl);

                //buffer completion for avoiding video freezing
                videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        videoPlayer.start();
                    }
                });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
//