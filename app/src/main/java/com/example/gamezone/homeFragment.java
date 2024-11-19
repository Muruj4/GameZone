package com.example.gamezone;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class homeFragment extends Fragment {

    private RecyclerView videoList;
    private VideoAdapter videoAdapter;
    private TextView noVideosText;
    private SearchView searchView;
    private MaterialButtonToggleGroup segmentedControl;
    private static final String TAG = "homeFragment";
    private List<Video> allVideos = new ArrayList<>();
    private DatabaseReference tournamentDatabaseReference;
    private boolean isCategorizedView = true;
    private static final Map<String, Integer> CATEGORY_ICONS = new HashMap<String, Integer>() {{
        put("Top Prizes", R.drawable.ic_top_prizes);
        put("Single Player", R.drawable.ic_single_player);
        put("Multiplayer", R.drawable.ic_multiplayer);
    }};
    private static final List<String> CATEGORY_ORDER = Arrays.asList("Top Prizes", "Single Player", "Multiplayer", "Other");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        videoList = view.findViewById(R.id.videoList);
        noVideosText = view.findViewById(R.id.noVideosText);
        searchView = view.findViewById(R.id.searchView);
        segmentedControl = view.findViewById(R.id.segmentedControl);

        // Set up RecyclerView with VideoAdapter
        videoList.setLayoutManager(new LinearLayoutManager(getContext()));
        videoAdapter = new VideoAdapter(getContext(), allVideos);
        videoList.setAdapter(videoAdapter);

        // Initialize Firebase reference
        tournamentDatabaseReference = FirebaseDatabase.getInstance().getReference("tournaments");

        // Check and add tournament data
        addTournamentIfNotExists();
        // Load JSON data
        getJsonData();
        // Load Firebase data and match with JSON titles
        fetchAndMatchFirebaseData();

        // Setup search functionality
        setupSearchView(view);

        // Setup segmented control listener for toggling between views
        setupSegmentedControl();

        return view;
    }

    private void setupSearchView(View view) {
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
    }

    private void setupSegmentedControl() {
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
        MaterialButton btnCategorizedView = getView().findViewById(R.id.btnCategorizedView);
        MaterialButton btnAllVideos = getView().findViewById(R.id.btnAllVideos);

        int activeColor = getResources().getColor(R.color.active_button_color, null);
        int inactiveColor = getResources().getColor(R.color.inactive_button_color, null);

        btnCategorizedView.setBackgroundTintList(
                ColorStateList.valueOf(checkedId == R.id.btnCategorizedView ? activeColor : inactiveColor));
        btnAllVideos.setBackgroundTintList(
                ColorStateList.valueOf(checkedId == R.id.btnAllVideos ? activeColor : inactiveColor));
    }

    private void getJsonData() {
        String URL = "https://raw.githubusercontent.com/YaraAlqarni/GameZone/main/gaming_videos.json";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                        noVideosText.setVisibility(allVideos.isEmpty() ? View.VISIBLE : View.GONE);
                        updateCategoryList();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> Log.d(TAG, "onErrorResponse: " + error.getMessage())
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchAndMatchFirebaseData() {
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
                                video.setMode(tournament.getMode());
                            }
                            video.setPrizeAmount(getParsedPrize(tournament.getPrize()));
                            video.setLevel(tournament.getLevel());
                            assignCategories(video);
                        }
                    }
                }

                allVideos.removeIf(Objects::isNull); // Remove null entries
                Log.d("AllVideos", "Total videos: " + allVideos.size());
                updateCategoryList();
                Log.d(TAG, "Firebase Data Loaded and Merged: " + allVideos.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Data Fetch Error: " + error.getMessage());
            }
        });
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
        if (video.getCategories() == null) {
            video.setCategories(new ArrayList<>());
        }

        if (video.getPrizeAmount() > 40000000) {
            video.getCategories().add("Top Prizes");
        }

        if (video.getMode() != null) {
            if (video.getMode().toLowerCase().contains("singleplayer")) {
                video.getCategories().add("Single Player");
            }
            if (video.getMode().toLowerCase().contains("multiplayer")) {
                video.getCategories().add("Multiplayer");
            }
        }

        if (video.getCategories().isEmpty()) {
            video.getCategories().add("Other");
        }
    }

    private void updateCategoryList() {
        if (isCategorizedView) {
            Map<String, List<Video>> categorizedVideos = new HashMap<>();
            for (Video video : allVideos) {
                if (video.getCategories() == null) {
                    Log.e("UpdateCategoryList", "Video " + video.getTitle() + " has null categories.");
                    continue;
                }

                for (String category : video.getCategories()) {
                    categorizedVideos.computeIfAbsent(category, k -> new ArrayList<>()).add(video);
                }
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

    private void addTournamentIfNotExists() {
        List<Tournament> tournaments = Arrays.asList(
                new Tournament("T01", "Gamers8", "Gamers8 is one of the biggest esports festivals in Saudi Arabia, featuring global tournaments across various popular games, with millions in prize money and live concerts.", "multiplayer", convertPrizeToLong("$45,000,000"), "8 weeks", "July", "Professional Level", Arrays.asList("Rocket League", "Fortnite", "Tekken 8", "Dota 2", "Starcraft 2")),
                new Tournament("T02", "PUBG Mobile Star Challenge", "The PUBG Mobile Star Challenge World Cup was held in Riyadh, Saudi Arabia, where top teams from around the world competed in intense matches for the championship title and significant cash prizes.", "multiplayer", convertPrizeToLong("$250,000"), "4 weeks", "June", "Intermediate Level", Arrays.asList("Pubg")),
                new Tournament("T03", "Rocket League MENA Cup", "The Rocket League MENA Cup, hosted in Riyadh, Saudi Arabia, attracted the best Rocket League teams from the Middle East and North Africa for a regional championship with high-stakes gameplay.", "multiplayer", convertPrizeToLong("$4,300,000"), "1 week", "February", "Professional Level", Arrays.asList("Rocket League")),
                new Tournament("T04", "Intel Arabian Cup", "The Intel Arabian Cup, supported by Intel and Riot Games, is a League of Legends tournament that brings together the best players and teams from Saudi Arabia and the MENA region for exciting esports action and valuable prizes.", "multiplayer", convertPrizeToLong("$100,000"), "5 weeks", "May", "Beginner Level", Arrays.asList("League of Legends")),
                new Tournament("T05", "FIFA Esports World Cup", "The FIFA eWorld Cup Qualifiers in Saudi Arabia...", "multiplayer", convertPrizeToLong("$60,000,000"), "8 weeks", "July", "Professional Level", Arrays.asList("Dota 2", "League of Legends", "FIFA")),
                new Tournament("T06", "Apex Legends Global Series", "The Apex Legends Global Series showcases the finest teams as they engage in thrilling battle royale matches, pushing their skills and strategies to the limit. Fans worldwide tune in to witness electrifying gameplay and intense competition.", "singleplayer", convertPrizeToLong("$3,000,000"), "1 week", "January", "Intermediate Level", Arrays.asList("Apex Legend")),
                new Tournament("T07", "Tekken 8 World Tour", "The Tekken 8 World Tour features the world's top Tekken players competing in a series of high-stakes tournaments. The event highlights remarkable skills and fierce rivalries, delivering a captivating experience for both players and fans.", "singleplayer", convertPrizeToLong("$300,000"), "6 weeks", "April", "Professional Level", Arrays.asList("Tekken 8")),
                new Tournament("T08", "Clash Royale League", "The Clash Royale League is a premier global tournament where elite Clash Royale players and teams battle for supremacy. With real-time strategy and thrilling gameplay, this league offers fans unforgettable moments and intense competition.", "singleplayer", convertPrizeToLong("$50,000"), "5 weeks", "March", "Beginner Level", Arrays.asList("Clash Royale")),
                new Tournament("T09", "Call of Duty WSOW", "The Call of Duty World Series of Warzone (WSOW) gathers top-tier Call of Duty players and teams in an exhilarating tournament format. Intense gunplay and strategic teamwork are on full display as they vie for victory and fame.", "multiplayer", convertPrizeToLong("$300,000"), "1 week", "July", "Professional Level", Arrays.asList("Call Of Duty")),
                new Tournament("T10", "Global Esports Games", "The Riyadh Global Esports Games is a global, multi-title esports competition that showcases esports' energy through competitions and a dynamic celebration of esports culture and entertainment at the GEG Festival.", "multiplayer", convertPrizeToLong("$10,000,000"), "1 week", "December", "Professional Level", Arrays.asList("Valorant", "PUBG", "Tekken 8", "Dota 2", "Counter-Strike 2"))
        );

        for (Tournament tournament : tournaments) {
            tournamentDatabaseReference.child(tournament.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        tournamentDatabaseReference.child(tournament.getId()).setValue(tournament)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Tournament " + tournament.getId() + " added successfully.");
                                    } else {
                                        Log.e(TAG, "Failed to add tournament " + tournament.getId() + ": " + task.getException());
                                    }
                                });
                    } else {
                        Log.d(TAG, "Tournament " + tournament.getId() + " already exists.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    private Long convertPrizeToLong(String prize) {
        try {
            return Long.parseLong(prize.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse prize: " + prize);
            return 0L;
        }
    }
}
////