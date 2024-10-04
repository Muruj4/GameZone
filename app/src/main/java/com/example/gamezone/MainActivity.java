package com.example.gamezone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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
    private static final String TAG = "MainActivity";  // For logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Corrected the layout reference

        videoList = findViewById(R.id.videoList);  // Use R.id instead of just 'id'
        videoList.setLayoutManager(new LinearLayoutManager(this));
        getJsonData();  // Fetch the data and initialize adapter
    }

    private void getJsonData() {

        String URL = "file:///C:/Users/96654/AndroidStudioProjects/GameZone/app/src/main/assets/gaming_videos.json";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Video> videoListData = new ArrayList<>();  // Store parsed videos here

                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            JSONObject categoriesData = categories.getJSONObject(0);
                            JSONArray videos = categoriesData.getJSONArray("videos");
                            Log.d(TAG, "onResponse: " + videos);

                            for (int i = 0; i < videos.length(); i++) {
                                JSONObject video = videos.getJSONObject(i);

                                Video v = new Video();
                                v.setTitle(video.getString("title"));
                                v.setDescription(video.getString("description"));

                                // Fetch video URL from the "sources" array
                                JSONArray videoUrl = video.getJSONArray("sources");
                                v.setVideoUrl(videoUrl.getString(0));

                                // Fetch image URL from "thumb"
                                v.setImageUrl(video.getString("thumb"));

                                // Add the parsed video to the list
                                videoListData.add(v);

                                Log.d(TAG, "onResponse: " + v.getVideoUrl());
                            }

                            // Pass the video list to the adapter and set it on the RecyclerView
                            adapter = new VideoAdapter(videoListData);
                            videoList.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
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
