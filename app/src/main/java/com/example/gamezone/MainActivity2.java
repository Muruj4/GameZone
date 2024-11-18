package com.example.gamezone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import com.example.gamezone.R;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gamezone.databinding.ActivityMain2Binding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import com.example.gamezone.databinding.ActivityMainBinding;

import com.example.gamezone.databinding.ActivityMainBinding;

public class MainActivity2 extends AppCompatActivity {

    ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the binding and the layout
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // Only set content view once

        // Handle item selection from the BottomNavigationView
        binding.BottomNavigationView.setOnItemSelectedListener(item -> {
            // Handle different navigation items
            if (item.getItemId() == R.id.home) {
                replaceFragment(new homeFragment());  // Navigate to Home fragment
            } else if (item.getItemId() == R.id.map) {
                replaceFragment(new mapFragment());  // Navigate to Map fragment
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new profileFragment());  // Navigate to Profile fragment
            }
            return true;  // Item was handled
        });

        // Set initial fragment (Optional: If you want to load a default fragment)
        if (savedInstanceState == null) {
            replaceFragment(new homeFragment());  // Default fragment on app start
        }
    }////

    private void replaceFragment(Fragment fragment) {
        // Start fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);  // Ensure correct ID for your FrameLayout
        fragmentTransaction.commit();
    }
}
