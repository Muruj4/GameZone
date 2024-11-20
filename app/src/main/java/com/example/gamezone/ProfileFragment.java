package com.example.gamezone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, registeredTournamentsTextView;
    private Button logoutButton;
    private ShapeableImageView profileImageView; // ShapeableImageView for the profile picture
    private String playerId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the UI elements
        nameTextView = view.findViewById(R.id.profile_name);
        emailTextView = view.findViewById(R.id.profile_email);
        registeredTournamentsTextView = view.findViewById(R.id.registered_tournaments);
        logoutButton = view.findViewById(R.id.logout_button);
        profileImageView = view.findViewById(R.id.profile_image); // ShapeableImageView for profile image

        // Retrieve the player ID from SharedPreferences
        playerId = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("playerId", null);

        // Fetch and display the player's profile information
        fetchPlayerProfile(playerId);

        // Set up the logout button
        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clears all data from SharedPreferences
            editor.apply(); // Apply changes

            // Navigate back to the Login activity
            Intent intent = new Intent(getActivity(), Login.class);  // Replace Login.class with your Login Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Clear the back stack
            startActivity(intent);
            getActivity().finish();  // Optionally finish the current activity
        });

        return view;
    }

    private void fetchPlayerProfile(String playerId) {
        DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference("players").child(playerId);
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Player player = dataSnapshot.getValue(Player.class);
                    if (player != null) {
                        nameTextView.setText(player.getFullName());
                        emailTextView.setText(player.getEmail());

                        // Load profile image using Glide if imageUrl exists
                        String imageUrl = player.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(getActivity())
                                    .load(imageUrl) // URL of the image
                                    .into(profileImageView); // Load the image into the ShapeableImageView
                        }

                        List<String> registeredTournaments = player.getRegisteredTournaments();
                        if (registeredTournaments != null && !registeredTournaments.isEmpty()) {
                            StringBuilder tournamentsText = new StringBuilder();
                            for (String tournament : registeredTournaments) {
                                tournamentsText.append("- ").append(tournament).append("\n");
                            }
                            registeredTournamentsTextView.setText(tournamentsText.toString());
                        } else {
                            registeredTournamentsTextView.setText("No registered tournaments");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data fetch
            }
        });
    }
}
