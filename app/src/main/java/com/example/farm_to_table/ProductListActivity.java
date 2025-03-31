package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm_to_table.databinding.ActivityProductListBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ActivityProductListBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Farm data
    private String farmName;
    private String farmDescription;
    private int farmImageId;
    private SearchView search;
    private ProductAdapter adapter;

    // List of products for this farm
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Use view binding
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize search view
        search = binding.searchView;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // product filter
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Product List Screen");

        // Get farm data from intent
        Intent intent = getIntent();
        if (intent != null) {
            farmName = intent.getStringExtra("FARM_NAME");
            farmDescription = intent.getStringExtra("FARM_DESCRIPTION");
            farmImageId = intent.getIntExtra("FARM_IMAGE", R.drawable.product);
        }

        // Set up the UI with farm data
        setupUI();

        // Generate sample products for this farm
        generateSampleProducts();

        // Populate product list
        populateProductList();

        // Set up button click listeners
        setupButtons();

        // Update cart badge
        updateCartBadge();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to this activity
        updateCartBadge();
    }



    private void updateCartBadge() {
        // Get the current cart count
        int cartCount = CartManager.getInstance().getItemCount();

        // Update the cart badge in the bottom navigation
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

    private void setupUI() {
        // Set farm name as title
        //binding.tvTitle.setText(farmName + " Products");

        // Set farm image
        binding.ivFarmImage.setImageResource(farmImageId);
    }

    private void generateSampleProducts() {
        // Clear any existing products
        products.clear();

        // Create sample products based on the farm name
        if (farmName.contains("Fruit") || farmName.equals("Paynter's Fruit Market") ||
                farmName.equals("Hillcrest Farm Market")) {
            products.add(new Product("Fresh Apples", "Locally grown, crisp and juicy apples.", 3.99, R.drawable.cherries));
            products.add(new Product("Cherries", "Sweet cherries picked at peak ripeness.", 5.99, R.drawable.cherries));
            products.add(new Product("Peaches", "Juicy peaches freshly harvested.", 4.49, R.drawable.product));
        }

        if (farmName.contains("Vegetable") || farmName.equals("Don-O-Ray Vegetables")) {
            products.add(new Product("Carrots", "Organic carrots harvested this morning.", 2.99, R.drawable.vegetables));
            products.add(new Product("Tomatoes", "Vine-ripened tomatoes, grown locally.", 3.49, R.drawable.product));
            products.add(new Product("Lettuce", "Fresh, crisp lettuce perfect for salads.", 2.49, R.drawable.product));
        }

        if (farmName.contains("Honey") || farmName.equals("Arlo's Honey Farm")) {
            products.add(new Product("Raw Honey", "Unprocessed, pure local honey.", 8.99, R.drawable.honey));
            products.add(new Product("Honeycomb", "Natural honeycomb straight from our hives.", 12.99, R.drawable.product));
            products.add(new Product("Beeswax Candles", "Hand-crafted beeswax candles.", 15.99, R.drawable.product));
        }

        if (farmName.contains("Herb") || farmName.contains("Lavender")) {
            products.add(new Product("Lavender Bundle", "Dried lavender bundle for aromatherapy.", 7.99, R.drawable.herbal));
            products.add(new Product("Herb Mix", "Fresh herb mixture for cooking.", 4.99, R.drawable.product));
            products.add(new Product("Lavender Soap", "Handmade lavender soap.", 6.99, R.drawable.product));
        }

        // If no specific products were added, add some generic ones
        if (products.isEmpty()) {
            products.add(new Product("Fresh Produce", "Locally grown seasonal produce.", 3.99, R.drawable.product));
            products.add(new Product("Farm Eggs", "Free-range eggs from happy hens.", 5.99, R.drawable.product));
            products.add(new Product("Artisan Jam", "Homemade jam from our own fruit.", 7.99, R.drawable.product));
        }
    }

    private void populateProductList() {
        RecyclerView recyclerView = binding.rvProducts;
        TextView noProductsText = binding.tvNoProducts;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the class field adapter instead of creating a local variable
        adapter = new ProductAdapter(products, product -> {
            trackProductSelection(product.getName());
            Intent intent = new Intent(ProductListActivity.this, ProductDetail.class);
            intent.putExtra("PRODUCT_NAME", product.getName());
            intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            intent.putExtra("PRODUCT_IMAGE", product.getImageResourceId());
            intent.putExtra("FARM_NAME", farmName);
            startActivity(intent);
        });

        adapter.setOnFilterListener(new ProductAdapter.OnFilterListener() {
            @Override
            public void onEmptyResult() {
                recyclerView.setVisibility(View.GONE);
                noProductsText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResult() {
                recyclerView.setVisibility(View.VISIBLE);
                noProductsText.setVisibility(View.GONE);
            }
        });

        recyclerView.setAdapter(adapter);
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

    private void setupBottomNavigation() {
        ImageButton homeBtn = binding.bottomNavInclude.btnHome;
        ImageButton trackingBtn = binding.bottomNavInclude.btnTracking;
        ImageButton cartBtn = binding.bottomNavInclude.btnCart;
        ImageButton historyBtn = binding.bottomNavInclude.btnHistory;
        ImageButton settingsBtn = binding.bottomNavInclude.btnSettings;

        // Home button
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Cart button
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, Cart.class));

                // Track event
                Bundle bundle = new Bundle();
                bundle.putString("button_name", "cart_bottom_nav");
                mFirebaseAnalytics.logEvent("navigation_click", bundle);
            }
        });
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void trackProductSelection(String productName) {
        Bundle bundle = new Bundle();
        bundle.putString("product_name", productName);
        bundle.putString("farm_name", farmName);
        mFirebaseAnalytics.logEvent("product_selected", bundle);
    }
}