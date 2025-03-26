package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.example.farm_to_table.databinding.ActivityMapViewBinding;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button selectFarmButton;
    private ActivityMapViewBinding binding;
    private List<Farm> farms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding instead of findViewById
        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Map View Screen");

        // Using view binding to access UI elements
        selectFarmButton = binding.btnSelectFarm;

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Add sample farms in Kelowna area
        setupFarmData();

        // Set up button click listeners
        selectFarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to farm selection or details screen
                // For now, just go back to MainActivity
                startActivity(new Intent(MapViewActivity.this, MainActivity.class));
            }
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void setupFarmData() {
        // Kelowna area farm data
        farms.add(new Farm("Paynter's Fruit Market", new LatLng(49.8342, -119.6501), "cherries and apples"));
        farms.add(new Farm("Arlo's Honey Farm", new LatLng(49.8887, -119.4962), "Local honey producer with bee tours and natural products"));
        farms.add(new Farm("Okanagan Lavender & Herb Farm", new LatLng(49.8348, -119.4887), "herb gardens with artisanal products"));
        farms.add(new Farm("Don-O-Ray Vegetables", new LatLng(49.8883, -119.4015), "Local vegetable farm with fresh seasonal produce"));
        farms.add(new Farm("Hillcrest Farm Market", new LatLng(50.0283, -119.4143), "fresh fruits and vegetables"));
    }

    private void setupBottomNavigation() {
        ImageButton homeBtn = binding.btnHome;
        ImageButton trackingBtn = binding.btnTracking;
        ImageButton cartBtn = binding.btnCart;
        ImageButton historyBtn = binding.btnHistory;
        ImageButton settingsBtn = binding.btnSettings;

        // Implement these once you have the activities created
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, MainActivity.class));
            }
        });

        // Uncomment these once you have the activities implemented
        /*
        trackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, TrackingActivity.class));
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, CartActivity.class));
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, HistoryActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, SettingsActivity.class));
            }
        });
        */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add markers for farms with orange color (as shown in the picture)
        for (Farm farm : farms) {
            map.addMarker(new MarkerOptions()
                    .position(farm.getLocation())
                    .title(farm.getName())
                    .snippet(farm.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }

        // Center map on Kelowna area (adjust zoom level to show all markers)
        LatLng kelownaCenter = new LatLng(49.8951, -119.4947);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(kelownaCenter, 10));

        // Configure map UI settings
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setCompassEnabled(true);

        // Set up marker click listener for analytics
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                trackFarmSelection(marker.getTitle());
                return false; // false to show the info window
            }
        });
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void trackFarmSelection(String farmName) {
        Bundle bundle = new Bundle();
        bundle.putString("farm_name", farmName);
        mFirebaseAnalytics.logEvent("farm_marker_selected", bundle);
    }

    // Farm data model
    private static class Farm {
        private String name;
        private LatLng location;
        private String description;

        public Farm(String name, LatLng location, String description) {
            this.name = name;
            this.location = location;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public LatLng getLocation() {
            return location;
        }

        public String getDescription() {
            return description;
        }
    }
}