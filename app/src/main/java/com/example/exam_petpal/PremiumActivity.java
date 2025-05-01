package com.example.exam_petpal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PremiumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        Button buyButton = findViewById(R.id.buyPremiumButton);
        buyButton.setOnClickListener(v -> {
            Toast.makeText(this, "Покупка премиум пока недоступна (заглушка)", Toast.LENGTH_SHORT).show();
        });
    }
} 