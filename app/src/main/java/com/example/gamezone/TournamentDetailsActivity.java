package com.example.gamezone;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class TournamentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_details);

        // Get the tournament data passed from the previous activity
        Tournament tournament = (Tournament) getIntent().getSerializableExtra("tournamentData");

        // Initialize the TextViews
        TextView titleTextView = findViewById(R.id.tournamentTitle);
        TextView modeTextView = findViewById(R.id.tournamentMode);
        TextView prizeTextView = findViewById(R.id.tournamentPrize);
        TextView durationTextView = findViewById(R.id.tournamentDuration);
        TextView startDateTextView = findViewById(R.id.tournamentStartDate);
        TextView levelTextView = findViewById(R.id.tournamentLevel);
        TextView gamesTextView = findViewById(R.id.tournamentGames);

        // Set the text of each TextView based on the tournament data
        if (tournament != null) {
            titleTextView.setText(String.format("Title: %s", tournament.getTitle()));
            modeTextView.setText(String.format("Mode: %s", tournament.getMode()));

            // Format the prize to include currency symbol and commas
            String formattedPrize = formatPrize(tournament.getPrize());
            prizeTextView.setText(String.format("Prize: %s", formattedPrize));

            durationTextView.setText(String.format("Duration: %s", tournament.getDuration()));
            startDateTextView.setText(String.format("Start Date: %s", tournament.getStartDate()));
            levelTextView.setText(String.format("Level: %s", tournament.getLevel()));
            gamesTextView.setText(String.format("Games: %s", tournament.getGames()));
        }

        // Back button setup
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());  // Calls the built-in onBackPressed method

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Automatically finishes the current activity and returns to the previous one
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Handles the back press from the action bar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to format prize value as currency (e.g., $45,000,000)
    private String formatPrize(long prize) {
        // Format the prize as a currency string
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(prize);
    }
}
