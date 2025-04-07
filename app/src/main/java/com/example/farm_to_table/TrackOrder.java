package com.example.farm_to_table;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.farm_to_table.databinding.ActivityFarmDetailBinding;
import com.example.farm_to_table.databinding.ActivityPhotoConfirmationBinding;
import com.example.farm_to_table.databinding.ActivityTrackOrderBinding;

public class TrackOrder extends AppCompatActivity {

    private ActivityTrackOrderBinding binding;

    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupButtons();

        binding.bottomNavInclude.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, FarmListActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, HistoryActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, Cart.class);
            startActivity(intent);
            finish();
        });
        binding.OpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, PhotoConfirmation.class);
            startActivity(intent);
            finish();
        });


    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

    }

}

