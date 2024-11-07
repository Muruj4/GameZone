package com.example.gamezone;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {

    private RecyclerView videoList;
    private VideoAdapter videoAdapter;
    private TextView noVideosText;
    private SearchView searchView;
    private MaterialButtonToggleGroup segmentedControl;
    private static final String TAG = "MainActivity";
    private List<Video> allVideos = new ArrayList<>();
    private DatabaseReference tournamentDatabaseReference;
    private boolean isCategorizedView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        videoList = findViewById(R.id.videoList);
        noVideosText = findViewById(R.id.noVideosText);
        searchView = findViewById(R.id.searchView);
        segmentedControl = findViewById(R.id.segmentedControl);

        // Set up RecyclerView with VideoAdapter
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, allVideos);
        videoList.setAdapter(videoAdapter);

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

        // Set up segmented control listener for toggling between views
        segmentedControl.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnCategorizedView) {
                    isCategorizedView = true;
                    updateCategoryList();
                } else if (checkedId == R.id.btnAllVideos) {
                    isCategorizedView = false;
                    displayAllVideos();
                }
                // Update button appearance
                updateButtonAppearance(checkedId);
            }
        });
    }

    private void updateButtonAppearance(int checkedId) {
        int activeColor = getResources().getColor(R.color.active_button_color, null); // Define active color in colors.xml
        int inactiveColor = getResources().getColor(R.color.inactive_button_color, null); // Define inactive color in colors.xml

        findViewById(R.id.btnCategorizedView).setBackgroundTintList(
                checkedId == R.id.btnCategorizedView ? ColorStateList.valueOf(activeColor) : ColorStateList.valueOf(inactiveColor));

        findViewById(R.id.btnAllVideos).setBackgroundTintList(
                checkedId == R.id.btnAllVideos ? ColorStateList.valueOf(activeColor) : ColorStateList.valueOf(inactiveColor));
    }


    private void getJsonData() {
        new Thread(() -> {
            String URL = "https://raw.githubusercontent.com/YaraAlqarni/GameZone/main/gaming_videos.json";
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                    response -> {
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

                            allVideos.removeIf(Objects::isNull);  // Remove null entries

                            Log.d(TAG, "JSON Data Loaded: " + allVideos.size());
                            runOnUiThread(() -> {
                                noVideosText.setVisibility(allVideos.isEmpty() ? View.VISIBLE : View.GONE);
                                updateCategoryList();
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    },
                    error -> Log.d(TAG, "onErrorResponse: " + error.getMessage())
            );

            requestQueue.add(jsonObjectRequest);
        }).start();
    }

    private void fetchAndMatchFirebaseData() {
        new Thread(() -> {
            tournamentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Video> videoMap = new HashMap<>();
                    for (Video video : allVideos) {
                        videoMap.put(video.getTitle(), video);
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tournament tournament = snapshot.getValue(Tournament.class);
                        if (tournament != null && tournament.getTitle() != null) {
                            Video video = videoMap.get(tournament.getTitle());

                            if (video != null) {
                                if (tournament.getMode() != null) {
                                    video.setModes(new ArrayList<>(tournament.getMode()));
                                    Log.d(TAG, "Video Modes Loaded from Firebase for " + video.getTitle() + ": " + video.getModes());
                                }
                                video.setPrizeAmount(getParsedPrize(tournament.getPrize()));
                                video.setLevel(tournament.getLevel());
                                assignCategories(video);
                            }
                        }
                    }

                    allVideos.removeIf(Objects::isNull);  // Remove null entries

                    runOnUiThread(() -> updateCategoryList());
                    Log.d(TAG, "Firebase Data Loaded and Merged: " + allVideos.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase Data Fetch Error: " + error.getMessage());
                }
            });
        }).start();
    }

    private double getParsedPrize(Object prize) {
        try {
            if (prize instanceof Long) {
                return (double) (Long) prize;
            } else if (prize instanceof Double) {
                return (Double) prize;
            } else if (prize instanceof String) {
                return Double.parseDouble(((String) prize).replaceAll("[$,]", ""));
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse prize amount", e);
        }
        return 0.0;
    }

    private void assignCategories(Video video) {
        if (video.getPrizeAmount() > 1000000) {
            video.setCategory("Top Prizes");
        } else if (video.getModes() != null && video.getModes().contains("singleplayer")) {
            video.setCategory("Single Player");
        } else if (video.getModes() != null && video.getModes().contains("multiplayer")) {
            video.setCategory("Multiplayer");
        } else {
            video.setCategory("Other");
        }
    }

    private void updateCategoryList() {
        if (isCategorizedView) {
            Map<String, List<Video>> categorizedVideos = new HashMap<>();
            for (Video video : allVideos) {
                String category = video.getCategory();
                categorizedVideos.computeIfAbsent(category, k -> new ArrayList<>()).add(video);
            }
            videoAdapter.setCategorizedData(categorizedVideos);
        } else {
            displayAllVideos();
        }
    }

    private void displayAllVideos() {
        videoAdapter.setFilteredList(allVideos);
        noVideosText.setVisibility(allVideos.isEmpty() ? View.VISIBLE : View.GONE);
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
        assignCategoriesForFilteredList(filteredVideos);
        videoAdapter.setFilteredList(filteredVideos);
    }

    private void assignCategoriesForFilteredList(List<Video> filteredVideos) {
        for (Video video : filteredVideos) {
            assignCategories(video);
        }
    }
}
