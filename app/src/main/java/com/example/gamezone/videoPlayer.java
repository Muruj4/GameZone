package com.example.gamezone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        // Extract video data from the intent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        Video v = (Video) data.getSerializable("tournamentData");

        TextView title = findViewById(R.id.videoTitle);
        TextView desc = findViewById(R.id.videoDescription);
        WebView videoWebView = findViewById(R.id.videoWebView);

        title.setText(v.getTitle());
        desc.setText(v.getDescription());

        // Set up WebView to load video URL
        String videoUrl = v.getVideoUrl().replace("youtu.be/", "www.youtube.com/embed/");
        videoWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load the video URL in WebView
        videoWebView.loadUrl(videoUrl);
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
