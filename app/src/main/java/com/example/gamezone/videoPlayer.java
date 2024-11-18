package com.example.gamezone;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class videoPlayer extends AppCompatActivity {

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

        // Extract video data from the intent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        Video v = (Video) data.getSerializable("videoData"); // Changed key to "videoData"

        // Initialize UI elements
        TextView title = findViewById(R.id.videoTitle);
        TextView desc = findViewById(R.id.videoDescription);
        WebView videoWebView = findViewById(R.id.videoWebView);

        // Set video information
        if (v != null) {
            title.setText(v.getTitle());
            desc.setText(v.getDescription());

            // Set up WebView to load video URL
            String videoUrl = v.getVideoUrl().replace("youtu.be/", "www.youtube.com/embed/");
            videoWebView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = videoWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            videoWebView.loadUrl(videoUrl);
        } else {
            // Handle the case where video data is null
            title.setText("Video not found");
            desc.setText("No description available.");
        }
    }
}