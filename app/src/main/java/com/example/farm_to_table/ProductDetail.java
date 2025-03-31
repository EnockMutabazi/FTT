package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farm_to_table.databinding.ActivityProductDetailBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ProductDetail extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Product data
    private String productName;
    private String productDescription;
    private double productPrice;
    private int productImageId;
    private String farmName;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Product Detail Screen");

        // Get product data from intent
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("PRODUCT_NAME");
            productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
            productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
            productImageId = intent.getIntExtra("PRODUCT_IMAGE", R.drawable.product);
            farmName = intent.getStringExtra("FARM_NAME");
        }

        // Set up the UI with product data
        setupUI();

        // Set up button click listeners
        setupButtons();

        // Update cart badge
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to this screen
        updateCartBadge();
    }

    private void updateCartBadge() {
        // Get the current cart count
        int cartCount = CartManager.getInstance().getItemCount();

        // If you have a cart badge view in the layout, update it here
        if (binding.cartBadge != null) {
            if (cartCount > 0) {
                binding.cartBadge.setVisibility(View.VISIBLE);
                binding.cartBadge.setText(String.valueOf(cartCount));
            } else {
                binding.cartBadge.setVisibility(View.GONE);
            }
        }
        TextView bottomCartBadge = binding.bottomNavInclude.bottomCartBadge;
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
        // Set product details
        binding.tvProductName.setText(productName);
        binding.tvProductDescription.setText(productDescription);
        binding.tvProductPrice.setText(String.format("$%.2f", productPrice));
        binding.ivProductImage.setImageResource(productImageId);
        binding.tvFarmName.setText("From: " + farmName);
        binding.tvQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();
    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Decrease quantity button
        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    binding.tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            }
        });

        // Increase quantity button
        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Add to cart button
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track event
                trackAddToCart(productName, quantity, productPrice * quantity);

                // Add to cart
                CartManager.getInstance().addToCart(
                        new CartItem(productName, productPrice, quantity, productImageId, farmName)
                );

                Toast.makeText(ProductDetail.this,
                        quantity + " " + productName + " added to cart",
                        Toast.LENGTH_SHORT).show();

                // Update the cart badge instead of navigating to cart
                updateCartBadge();

                // Reset quantity to 1 for next add
                quantity = 1;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Cart icon click listener (if you have one in the layout)
        binding.bottomNavInclude.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to cart
                Intent intent = new Intent(ProductDetail.this, Cart.class);
                startActivity(intent);
            }
        });
    }

    private void updateTotalPrice() {
        double total = productPrice * quantity;
        binding.tvTotalPrice.setText(String.format("Total: $%.2f", total));
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void trackAddToCart(String productName, int quantity, double total) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName);
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, quantity);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, total);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "CAD");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }
}