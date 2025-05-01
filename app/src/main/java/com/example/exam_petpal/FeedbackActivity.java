package com.example.exam_petpal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText messageInput;
    private RatingBar ratingBar;
    private Button submitButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();

        // Инициализация UI элементов
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        messageInput = findViewById(R.id.message_input);
        ratingBar = findViewById(R.id.rating_bar);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("name", name);
        feedback.put("email", email);
        feedback.put("message", message);
        feedback.put("rating", rating);
        feedback.put("timestamp", System.currentTimeMillis());

        db.collection("feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Спасибо за ваш отзыв!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Ошибка при отправке отзыва", Toast.LENGTH_SHORT).show();
                });
    }
} 