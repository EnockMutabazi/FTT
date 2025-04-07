package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvSubtotalValue, tvDeliveryValue, tvTotalValue;
    private RadioGroup rgPaymentOptions;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Adding the navbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Crating Views
        tvSubtotalValue = findViewById(R.id.tvSubtotalValue);
        tvDeliveryValue = findViewById(R.id.tvDeliveryValue);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        rgPaymentOptions = findViewById(R.id.rgPaymentOptions);
        btnPay = findViewById(R.id.btnPay);

        // Getting the total from cart
        double totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        double deliveryFee = 2.00;
        double subtotal = totalAmount - deliveryFee;

        tvSubtotalValue.setText(String.format("$%.2f", subtotal));
        tvDeliveryValue.setText(String.format("$%.2f", deliveryFee));
        tvTotalValue.setText(String.format("$%.2f", totalAmount));

        // Updating for the total
        btnPay.setText("Pay " + String.format("$%.2f", totalAmount));

        btnPay.setOnClickListener(v -> {
            int selectedPaymentOption = rgPaymentOptions.getCheckedRadioButtonId();
            if (selectedPaymentOption == -1) {
                Toast.makeText(PaymentActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}