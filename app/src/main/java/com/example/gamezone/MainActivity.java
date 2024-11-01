package com.example.gamezone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import android.widget.SearchView;
public class MainActivity extends AppCompatActivity {

    RecyclerView videoList;
    VideoAdapter adapter;
    TextView noVideosText;  // TextView to show when no videos are available
    private static final String TAG = "MainActivity";
    List<Video> allVideos;
    SearchView searchView;
    private List<Tournament> allTournaments = new ArrayList<>();
    // Firebase database reference for tournaments
    private DatabaseReference tournamentDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and TextView
        videoList = findViewById(R.id.videoList);
        noVideosText = findViewById(R.id.noVideosText);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        allVideos = new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // تنفيذ الفلترة عند إدخال المستخدم النص الكامل والضغط على "بحث"
                filterVideos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // تنفيذ الفلترة عند كل تغيير في النص المدخل
                filterVideos(newText);
                return true;
            }
        });
        videoList.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter with the initial empty list
        adapter = new VideoAdapter(this, allVideos);
        videoList.setAdapter(adapter);

        // Initialize Firebase database reference for tournaments
        tournamentDatabaseReference = FirebaseDatabase.getInstance().getReference("tournaments");

        // Check and add tournament data
        addTournamentIfNotExists();

        // Fetch and load the JSON data
        getJsonData();
    }

    private void getJsonData() {
        // Use your JSON URL
        String URL = "https://raw.githubusercontent.com/YaraAlqarni/GameZone/main/gaming_videos.json";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            JSONObject categoriesData = categories.getJSONObject(0);
                            JSONArray videos = categoriesData.getJSONArray("videos");

                            // Check if the videos array is empty
                            if (videos.length() == 0) {
                                Log.d(TAG, "No videos found.");
                                noVideosText.setVisibility(View.VISIBLE);  // Show the "No videos available" message
                            } else {
                                noVideosText.setVisibility(View.GONE);  // Hide the message if videos are available

                                for (int i = 0; i < videos.length(); i++) {
                                    JSONObject video = videos.getJSONObject(i);

                                    Video v = new Video();
                                    v.setTitle(video.getString("title"));
                                    v.setDescription(video.getString("description"));
                                    v.setAuthor(video.getString("subtitle"));
                                    JSONArray videoUrl = video.getJSONArray("sources");
                                    v.setVideoUrl(videoUrl.getString(0));  // Ensure this key exists
                                    v.setImageUrl(video.getString("thumb"));  // Ensure this key exists
                                    Log.d(TAG, "onResponse: " + v.getVideoUrl());

                                    allVideos.add(v);  // Add the video to the list
                                }

                                // Notify the adapter that the data has changed
                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void addTournamentIfNotExists() {
        List<Tournament> tournaments = Arrays.asList(
                new Tournament("T1", "Gamers8", "Gamers8 is one of the biggest esports festivals in Saudi Arabia, featuring global tournaments across various popular games, with millions in prize money and live concerts.", Arrays.asList("multiplayer","singleplayer"), "$45,000,000", "8 weeks", "July","Professional Level", Arrays.asList("Rocket League", "Fortnite", "Tekken 8", "Dota 2", "Starcraft 2")),
                new Tournament("T2", "PUBG Mobile Star Challenge", "The PUBG Mobile Star Challenge World Cup was held in Riyadh, Saudi Arabia, where top teams from around the world competed in intense matches for the championship title and significant cash prizes.", Arrays.asList("multiplayer"), "$250,000", "4 weeks", "June", "Intermediate Level",Arrays.asList("Pubg")),
                new Tournament("T3", "Rocket League MENA Cup",  "The Rocket League MENA Cup, hosted in Riyadh, Saudi Arabia, attracted the best Rocket League teams from the Middle East and North Africa for a regional championship with high-stakes gameplay.", Arrays.asList("multiplayer"), "$4,300,000", "1 week", "February","Professional Level", Arrays.asList("Rocket League")),
                new Tournament("T4", "Intel Arabian Cup", "The Intel Arabian Cup, supported by Intel and Riot Games, is a League of Legends tournament that brings together the best players and teams from Saudi Arabia and the MENA region for exciting esports action and valuable prizes.", Arrays.asList("multiplayer"), "$100,000", "5 weeks", "May","Beginner Level", Arrays.asList("League of Legends")),
                new Tournament("T5", "FIFA Esports world Cup", "The FIFA eWorld Cup Qualifiers in Saudi Arabia allowed top FIFA players from the MENA region to compete for a place in the prestigious FIFA eWorld Cup, showcasing elite soccer gaming skills.", Arrays.asList("multiplayer","singleplayer"), "$60,000,000", "8 weeks", "July","Professional Level", Arrays.asList("Dota 2", "League of Legends", "Rainbow six siege", "Dota 2", "Overwatch 2","Street Fighter","FIFA","Honor of Kings")),
                new Tournament("T6", "Apex Legends Global Series", "The Apex Legends Global Series showcases the finest teams as they engage in thrilling battle royale matches, pushing their skills and strategies to the limit. Fans worldwide tune in to witness electrifying gameplay and intense competition.", Arrays.asList("singleplayer"), "$3,000,000", "1 week", "January","Intermediate Level", Arrays.asList("Apex Legend")),
                new Tournament("T7", "Tekken 8 World Tour", "The Tekken 8 World Tour features the world's top Tekken players competing in a series of high-stakes tournaments. The event highlights remarkable skills and fierce rivalries, delivering a captivating experience for both players and fans.", Arrays.asList("singleplayer"), "$300,000", "6 weeks", "April","Professional Level", Arrays.asList("Tekken 8")),
                new Tournament("T8", "Clash Royale League", "The Clash Royale League is a premier global tournament where elite Clash Royale players and teams battle for supremacy. With real-time strategy and thrilling gameplay, this league offers fans unforgettable moments and intense competition.", Arrays.asList("singleplayer"), "$50,000", "5 weeks", "March","Beginner Level", Arrays.asList("Clash Royale")),
                new Tournament("T9", "Call of Duty WSOW", "The Call of Duty World Series of Warzone (WSOW) gathers top-tier Call of Duty players and teams in an exhilarating tournament format. Intense gunplay and strategic teamwork are on full display as they vie for victory and fame.", Arrays.asList("multiplayer"), "$300,000", "1 week", "July","Professional Level", Arrays.asList("Call Of Duty")),
                new Tournament("T10", "Global Esports Games", "The Riyadh Global Esports Games is a global, multi-title esports competition that showcases esports' energy through competitions and a dynamic celebration of esports culture and entertainment at the GEG Festival.", Arrays.asList("multiplayer","singleplayer"), "$10,000,000", "1 week", "December","Professional Level", Arrays.asList("Valorant", "PUBG", "Tekken 8", "Dota 2", "Counter-Strike 2"))
        );

        for (Tournament tournament : tournaments) {
            tournamentDatabaseReference.child(tournament.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // Tournament does not exist, create it
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



    private void filterVideos(String query) {
        List<Video> filteredVideos = new ArrayList<>();

        // Convert query to lowercase for case-insensitive search
        String searchQuery = query.toLowerCase();
        for (Video video : allVideos) {
            if (video.getTitle().toLowerCase().contains(searchQuery) ||
                    video.getDescription().toLowerCase().contains(searchQuery)) {
                // إذا تطابق الفيديو، إضافته إلى القائمة المفلترة
                filteredVideos.add(video);
            }
        }


        if (filteredVideos.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        } else {

            adapter.setFilteredList(filteredVideos);
        }
    }

}