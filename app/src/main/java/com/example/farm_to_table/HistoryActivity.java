
package com.example.farm_to_table;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.View;
import com.example.farm_to_table.HistoryManager;
import com.example.farm_to_table.Product;
import com.example.farm_to_table.ProductAdapter;
import com.example.farm_to_table.databinding.ActivityHistoryBinding;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private ActivityHistoryBinding binding;
    private ProductAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupButtons();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rvHistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Product> historyItems = HistoryManager.getInstance().getHistoryItems();
        historyAdapter = new ProductAdapter(historyItems, this);
        recyclerView.setAdapter(historyAdapter);

        // Show/hide empty state
        updateEmptyState();
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnClearHistory.setOnClickListener(v -> {
            HistoryManager.getInstance().clearHistory();
            historyAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        List<Product> historyItems = HistoryManager.getInstance().getHistoryItems();
        if (historyItems.isEmpty()) {
            binding.emptyHistoryLayout.setVisibility(View.VISIBLE);
            binding.rvHistory.setVisibility(View.GONE);
        } else {
            binding.emptyHistoryLayout.setVisibility(View.GONE);
            binding.rvHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetail.class);
        intent.putExtra("PRODUCT_NAME", product.getName());
        intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
        intent.putExtra("PRODUCT_PRICE", product.getPrice());
        intent.putExtra("PRODUCT_IMAGE", product.getImageResourceId());
        intent.putExtra("FARM_NAME", product.getFarmName());
        startActivity(intent);
    }
}