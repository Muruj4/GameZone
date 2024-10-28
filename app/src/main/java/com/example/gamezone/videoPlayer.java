package com.example.gamezone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class videoPlayer extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        videoWebView.loadUrl(videoUrl);

        // Setup the Google Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Open Google Maps App with the location
        Button openMapsButton = findViewById(R.id.openMapsButton);
        openMapsButton.setOnClickListener(vvv -> {
            Uri gmmIntentUri = Uri.parse("geo:24.774265,46.738586?q=24.774265,46.738586(Event Location)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Coordinates of the location from the link you shared
        LatLng location = new LatLng(24.774265, 46.738586);
        mMap.addMarker(new MarkerOptions().position(location).title("Event Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
