package com.example.gamezone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gamezone.databinding.ActivityMain2Binding;

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
                replaceFragment(new MapFragment());  // Navigate to Map fragment
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());  // Navigate to Profile fragment
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
