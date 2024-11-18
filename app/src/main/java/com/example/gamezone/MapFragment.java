package com.example.gamezone;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class mapFragment extends Fragment implements OnMapReadyCallback {

        private GoogleMap mMap;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_map, container, false);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);  // Set up the map when it's ready
            }

            return view;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // New coordinates for VHCF+QCR, Al Qirawan, Riyadh 13545
            LatLng location = new LatLng(24.7887, 46.7224); // Coordinates for the new location
            mMap.addMarker(new MarkerOptions().position(location).title("VHCF+QCR, Al Qirawan"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            // Zoom in on the location
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
            mMap.animateCamera(zoom);
        }
    }