package com.example.gamezone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class videoPlayer extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String playerId;
    private DatabaseReference databaseReference;
    private WebView videoWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Retrieve playerId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        playerId = sharedPreferences.getString("playerId", null); // Return null if not found

        // Back button setup
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Extract video data from the intent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        Video videoData = (Video) data.getSerializable("videoData");

        // Initialize UI elements
        TextView title = findViewById(R.id.videoTitle);
        TextView desc = findViewById(R.id.videoDescription);
        videoWebView = findViewById(R.id.videoWebView);

        // Handle "Read More" button
        TextView readMore = findViewById(R.id.read_more);
        readMore.setOnClickListener(view -> {
            if (videoData != null) {
                String videoTitle = videoData.getTitle();
                fetchTournamentDetails(videoTitle);
            } else {
                Log.e("videoPlayer", "Video data is null");
            }
        });

        // Set video information
        if (videoData != null) {
            title.setText(videoData.getTitle());
            desc.setText(videoData.getDescription());

            // Set up WebView to load video URL
            String videoUrl = videoData.getVideoUrl().replace("youtu.be/", "www.youtube.com/embed/");
            videoWebView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = videoWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            videoWebView.loadUrl(videoUrl);
        } else {
            title.setText("Video not found");
            desc.setText("No description available.");
        }

        // Handle "Enroll" button click
        Button enrollButton = findViewById(R.id.joinUsButton);
        enrollButton.setOnClickListener(v -> {
            if (videoData != null) {
                String tournamentTitle = videoData.getTitle();
                if (playerId != null && !playerId.isEmpty()) {
                    registerForTournament(playerId, tournamentTitle);
                } else {
                    Log.e("videoPlayer", "Player ID is null or empty.");
                }
            }
        });
    }

    private void registerForTournament(String playerId, String tournamentTitle) {
        // Show the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering for tournament...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Reference to the player's data in the database
        DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference("players").child(playerId);

        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Check if the player data exists
                if (dataSnapshot.exists()) {
                    Player player = dataSnapshot.getValue(Player.class);

                    if (player != null) {
                        List<String> registeredTournaments = player.getRegisteredTournaments() != null
                                ? player.getRegisteredTournaments()
                                : new ArrayList<>();

                        // Check if the player is already registered for the tournament
                        if (!registeredTournaments.contains(tournamentTitle)) {
                            // Add the tournament title to the list
                            registeredTournaments.add(tournamentTitle);
                            saveRegisteredTournaments(playerId, registeredTournaments);
                        } else {
                            // Show already registered dialog
                            showAlreadyRegisteredDialog();
                        }
                    } else {
                        Log.e("videoPlayer", "Player data is null.");
                        Toast.makeText(videoPlayer.this, "Player data not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("videoPlayer", "Player not found in the database.");
                    Toast.makeText(videoPlayer.this, "Player not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.e("videoPlayer", "Database error: " + databaseError.getMessage());
                Toast.makeText(videoPlayer.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRegisteredTournaments(String playerId, List<String> registeredTournaments) {
        DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference("players").child(playerId);

        playerRef.child("RegisteredTournaments").setValue(registeredTournaments)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(videoPlayer.this, "Successfully registered for the tournament!", Toast.LENGTH_SHORT).show();
                        updateUIForSuccessfulRegistration();
                    } else {
                        Toast.makeText(videoPlayer.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUIForSuccessfulRegistration() {
        Button enrollButton = findViewById(R.id.joinUsButton);
        enrollButton.setText("Registered");
        enrollButton.setTextColor(getResources().getColor(android.R.color.white));
        enrollButton.setEnabled(false);
        showSuccessDialog();
    }

    private void showSuccessDialog() {
        DialogUtils.showSuccessDialog(this, "Thank you for registering for the tournament. Check your profile to see registered tournaments.");
    }

    private void showAlreadyRegisteredDialog() {
        DialogUtils.showAlreadyRegisteredDialog(this);
    }

    private void fetchTournamentDetails(String videoTitle) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tournaments");
        databaseReference.orderByChild("title").equalTo(videoTitle)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Tournament tournament = dataSnapshot.getChildren().iterator().next().getValue(Tournament.class);

                            if (tournament != null) {
                                Intent intent = new Intent(videoPlayer.this, TournamentDetailsActivity.class);
                                intent.putExtra("tournamentData", tournament);
                                startActivity(intent);
                            } else {
                                Log.e("videoPlayer", "Tournament data is null");
                            }
                        } else {
                            Log.e("videoPlayer", "No tournament found with title: " + videoTitle);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("videoPlayer", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (videoWebView.canGoBack()) {
            videoWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
