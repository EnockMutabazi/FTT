package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.example.farm_to_table.databinding.ActivityFarmListBinding;

import java.util.ArrayList;
import java.util.List;

public class FarmListActivity extends AppCompatActivity {

    private ActivityFarmListBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<MapViewActivity.Farm> farms = new ArrayList<>();
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding
        binding = ActivityFarmListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        search= binding.searchView;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                boolean hasVisibleFarms = false;

                for (int i = 0; i < binding.farmListContainer.getChildCount(); i++) {
                    View farmItemView = binding.farmListContainer.getChildAt(i);
                    TextView farmNameTextView = farmItemView.findViewById(R.id.tv_farm_name);
                    String farmName = farmNameTextView.getText().toString();

                    if (farmName.toLowerCase().contains(newText.toLowerCase())) {
                        farmItemView.setVisibility(View.VISIBLE);
                        hasVisibleFarms = true;
                    } else {
                        farmItemView.setVisibility(View.GONE);
                    }
                }

                // Show/hide no results message
                if (hasVisibleFarms || newText.isEmpty()) {
                    binding.tvNoResults.setVisibility(View.GONE);
                    binding.scrollView2.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoResults.setVisibility(View.VISIBLE);
                    binding.scrollView2.setVisibility(View.GONE);
                }

                return true;
            }
        });

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Farm List Screen");

        // Setup back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Setup farm data
        setupFarmData();

        // Populate the linear layout with farm items
        populateFarmList();
        // Update cart badge
        updateCartBadge();
    }
    private void updateCartBadge() {
        // Get the current cart count
        int cartCount = CartManager.getInstance().getItemCount();

        // If you have a cart badge view in the layout, update it here
        if (binding.bottomNavInclude.bottomCartBadge != null) {
            if (cartCount > 0) {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.VISIBLE);
                binding.bottomNavInclude.bottomCartBadge.setText(String.valueOf(cartCount));
            } else {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.GONE);
            }
        }
        TextView bottomCartBadge = binding.getRoot().findViewById(R.id.bottom_cart_badge);
        if (bottomCartBadge != null) {
            if (cartCount > 0) {
                bottomCartBadge.setVisibility(View.VISIBLE);
                bottomCartBadge.setText(String.valueOf(cartCount));
            } else {
                bottomCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void setupFarmData() {
        // Same farm data as in MapViewActivity
        farms.add(new MapViewActivity.Farm("Paynter's Fruit Market", new LatLng(49.8342, -119.6501),
                "cherries and apples",R.drawable.cherries));
        farms.add(new MapViewActivity.Farm("Arlo's Honey Farm", new LatLng(49.8887, -119.4962),
                "Local honey producer with bee tours and natural products",R.drawable.honey));
        farms.add(new MapViewActivity.Farm("Okanagan Lavender & Herb Farm", new LatLng(49.8348, -119.4887),
                "herb gardens with artisanal products",R.drawable.herbal));
        farms.add(new MapViewActivity.Farm("Don-O-Ray Vegetables", new LatLng(49.8883, -119.4015),
                "Local vegetable farm with fresh seasonal produce",R.drawable.vegetables));
        farms.add(new MapViewActivity.Farm("Hillcrest Farm Market", new LatLng(50.0283, -119.4143),
                "fresh fruits and vegetables",R.drawable.fruits));
    }

    private void populateFarmList() {
        LinearLayout farmListContainer = binding.farmListContainer;

        for (MapViewActivity.Farm farm : farms) {
            // Inflate the farm item view
            View farmItemView = getLayoutInflater().inflate(R.layout.item_farm, farmListContainer, false);

            // Set farm data to views
            TextView farmNameTextView = farmItemView.findViewById(R.id.tv_farm_name);
            TextView farmDescriptionTextView = farmItemView.findViewById(R.id.tv_farm_description);
            ImageView farmImageView = farmItemView.findViewById(R.id.iv_farm_image);


            farmNameTextView.setText(farm.getName());
            farmDescriptionTextView.setText(farm.getDescription());
            farmImageView.setImageResource(farm.getImageResourceId());


            // Set click listener
            farmItemView.setOnClickListener(v -> {
                // Track selection
                trackFarmSelection(farm.getName());

                // Navigate to FarmDetailActivity
                Intent intent = new Intent(FarmListActivity.this, FarmDetailActivity.class);
                intent.putExtra("FARM_NAME", farm.getName());
                intent.putExtra("FARM_DESCRIPTION", farm.getDescription());
                intent.putExtra("FARM_LAT", farm.getLocation().latitude);
                intent.putExtra("FARM_LNG", farm.getLocation().longitude);
                intent.putExtra("FARM_IMAGE", farm.getImageResourceId());
                startActivity(intent);
            });

            // Add the view to the container
            farmListContainer.addView(farmItemView);
        }
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
        mFirebaseAnalytics.logEvent("farm_list_selection", bundle);
    }
}