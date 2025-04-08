package com.example.farm_to_table;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm_to_table.databinding.ActivityPhotoConfirmationBinding;
import com.example.farm_to_table.databinding.ActivityTrackOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PhotoConfirmation extends AppCompatActivity {

    private RecyclerView messageRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton imageButton;
    private MessageAdapter adapter;
    private ArrayList<Message> messageList;
    private ActivityPhotoConfirmationBinding binding;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                        messageList.add(new Message(selectedImageUri.toString(), true, timestamp));
                        adapter.notifyItemInserted(messageList.size() - 1);
                        messageRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityPhotoConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        imageButton = findViewById(R.id.CameraButton); // Make sure you have this in your layout

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                messageList.add(new Message(text, true, timestamp));
                adapter.notifyItemInserted(messageList.size() - 1);
                messageEditText.setText("");
                messageRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        setupButtons();

    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, TrackOrder.class));
            finish();
        });
    }

    

}