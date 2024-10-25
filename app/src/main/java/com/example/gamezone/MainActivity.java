package com.example.gamezone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView videoList;
    VideoAdapter adapter;
    TextView noVideosText;  // TextView to show when no videos are available
    private static final String TAG = "MainActivity";
    List<Video> allVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and TextView
        videoList = findViewById(R.id.videoList);
        noVideosText = findViewById(R.id.noVideosText);

        allVideos = new ArrayList<>();
        videoList.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter with the initial empty list
        adapter = new VideoAdapter(this, allVideos);
        videoList.setAdapter(adapter);

        // Fetch and load the JSON data
        getJsonData();
    }

    private void getJsonData() {
        // Use your JSON URL
        String URL = "https://raw.githubusercontent.com/YaraAlqarni/GameZone/refs/heads/main/gaming_videos.json";

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
                                    JSONArray videoUrl =video.getJSONArray("sources");
                                    v.setVideoUrl(videoUrl.getString(0));  // Ensure this key exists
                                    v.setImageUrl(video.getString("thumb"));  // Ensure this key exists
                                    Log.d(TAG,"onResponse: "+ v.getVideoUrl());

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
}
