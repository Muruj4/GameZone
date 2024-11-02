package com.example.gamezone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

public class MainActivity extends AppCompatActivity {

    private RecyclerView videoList;
    private VideoAdapter adapter;
    private TextView noVideosText;
    private SearchView searchView;
    private Button toggleFilterButton, applyFiltersButton;
    private LinearLayout filterDrawer;
    private ChipGroup chipGroupMode;
    private RangeSlider sliderPrizeRange;

    private static final String TAG = "MainActivity";
    private List<Video> allVideos = new ArrayList<>();
    private DatabaseReference tournamentDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        videoList = findViewById(R.id.videoList);
        noVideosText = findViewById(R.id.noVideosText);
        searchView = findViewById(R.id.searchView);
        toggleFilterButton = findViewById(R.id.toggleFilterButton);
        filterDrawer = findViewById(R.id.filterDrawer);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        sliderPrizeRange = findViewById(R.id.sliderPrizeRange);
        sliderPrizeRange.setValues(10000f, 100000000f); // Set initial slider values
        applyFiltersButton = findViewById(R.id.applyFiltersButton);

        adapter = new VideoAdapter(this, allVideos);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videoList.setAdapter(adapter);

        // Initialize Firebase reference
        tournamentDatabaseReference = FirebaseDatabase.getInstance().getReference("tournaments");

        // Load JSON data
        getJsonData();

        // Load Firebase data and match with JSON titles
        fetchAndMatchFirebaseData();

        // Search functionality
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVideos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVideos(newText);
                return true;
            }
        });

        // Toggle filter drawer
        toggleFilterButton.setOnClickListener(v -> {
            if (filterDrawer.getVisibility() == View.GONE) {
                filterDrawer.setVisibility(View.VISIBLE);
            } else {
                filterDrawer.setVisibility(View.GONE);
            }
        });

        // Apply filter button functionality
        applyFiltersButton.setOnClickListener(v -> applyFilters());
    }

    private void getJsonData() {
        String URL = "https://raw.githubusercontent.com/YaraAlqarni/GameZone/main/gaming_videos.json";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            JSONObject categoriesData = categories.getJSONObject(0);
                            JSONArray videos = categoriesData.getJSONArray("videos");

                            for (int i = 0; i < videos.length(); i++) {
                                JSONObject videoJson = videos.getJSONObject(i);
                                Video video = new Video();
                                video.setTitle(videoJson.getString("title"));
                                video.setDescription(videoJson.getString("description"));
                                video.setAuthor(videoJson.getString("subtitle"));
                                video.setImageUrl(videoJson.getString("thumb"));
                                JSONArray videoUrlArray = videoJson.getJSONArray("sources");
                                video.setVideoUrl(videoUrlArray.getString(0));

                                allVideos.add(video);
                            }

                            Log.d(TAG, "JSON Data Loaded: " + allVideos.size());
                            noVideosText.setVisibility(allVideos.isEmpty() ? View.VISIBLE : View.GONE);
                            adapter.notifyDataSetChanged();  // Ensure adapter updates
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchAndMatchFirebaseData() {
        tournamentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tournament tournament = snapshot.getValue(Tournament.class);
                    if (tournament != null) {
                        for (Video video : allVideos) {
                            if (tournament.getMode() != null) {
                                video.setModes(tournament.getMode());


                                // Handle prize as Long or String

                                Object prize = tournament.getPrize();
                                try {
                                    if (prize instanceof Long) {
                                        video.setPrizeAmount((double) (Long) prize);
                                    } else if (prize instanceof String) {
                                        // Remove any unwanted characters (like "$" or ",") before parsing
                                        video.setPrizeAmount(Double.parseDouble(((String) prize).replaceAll("[$,]", "")));
                                    } else {
                                        // Handle unexpected type by setting a default value
                                        video.setPrizeAmount(0.0);
                                        Log.w(TAG, "Unexpected prize format for tournament: " + tournament.getTitle());
                                    }
                                } catch (NumberFormatException e) {
                                    video.setPrizeAmount(0.0); // Set a default or error value if parsing fails
                                    Log.e(TAG, "Failed to parse prize amount for tournament: " + tournament.getTitle(), e);
                                }



                                video.setLevel(tournament.getLevel());
                                break;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Firebase Data Loaded and Merged: " + allVideos.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Data Fetch Error: " + error.getMessage());
            }
        });
    }


    private void applyFilters() {
        List<Video> filteredVideos = new ArrayList<>();

        // Get selected mode from ChipGroup
        String selectedMode = null;
        int checkedChipId = chipGroupMode.getCheckedChipId();
        if (checkedChipId != -1) {
            Chip selectedChip = chipGroupMode.findViewById(checkedChipId);
            selectedMode = selectedChip != null ? selectedChip.getText().toString() : null;
        }

        // Get selected prize range from RangeSlider
        List<Float> values = sliderPrizeRange.getValues();
        float minPrize = values.get(0);
        float maxPrize = values.get(1);

        for (Video video : allVideos) {
            List<String> videoModes = video.getModes();
            double videoPrize = video.getPrizeAmount();

            // Check if selectedMode is in the list of modes
            boolean matchesMode = (selectedMode == null) ||
                    (videoModes != null && videoModes.contains(selectedMode.toLowerCase()));

            boolean matchesPrize = (videoPrize >= minPrize) && (videoPrize <= maxPrize);

            if (matchesMode && matchesPrize) {
                filteredVideos.add(video);
            }

            // Logging for debugging
            Log.d(TAG, "Video Title: " + video.getTitle() + ", Modes: " + videoModes + ", Prize: " + videoPrize);
            Log.d(TAG, "Matches Mode: " + matchesMode + ", Matches Prize: " + matchesPrize);
        }

        if (filteredVideos.isEmpty()) {
            Toast.makeText(this, "No videos found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filteredVideos);
            filterDrawer.setVisibility(View.GONE); // Hide drawer after applying filters
        }
    }







    // Helper function to check for an exact mode match
    private boolean containsExactMode(String videoMode, String selectedMode) {
        String[] modes = videoMode.split(",\\s*");  // Split by comma and trim whitespace
        for (String mode : modes) {
            if (mode.equalsIgnoreCase(selectedMode)) {
                return true;
            }
        }
        return false;
    }









    private void filterVideos(String query) {
        List<Video> filteredVideos = new ArrayList<>();
        String searchQuery = query.toLowerCase();
        for (Video video : allVideos) {
            if (video.getTitle().toLowerCase().contains(searchQuery) ||
                    video.getDescription().toLowerCase().contains(searchQuery)) {
                filteredVideos.add(video);
            }
        }
        adapter.setFilteredList(filteredVideos);
    }
}

