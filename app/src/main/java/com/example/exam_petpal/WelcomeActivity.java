package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Находим кнопки
        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        MaterialButton trialButton = findViewById(R.id.trialButton);

        // Обработка нажатия кнопки "Войти"
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Обработка нажатия кнопки "Регистрация"
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Обработка нажатия кнопки "Создать пробного питомца"
        trialButton.setOnClickListener(v -> {
            // Проверяем, есть ли уже пробный питомец
            if (isTrialPetExists()) {
                Toast.makeText(this, "У вас уже есть пробный питомец", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(WelcomeActivity.this, AddPetActivity.class);
            intent.putExtra("isTrial", true);
            startActivity(intent);
        });
    }

    private boolean isTrialPetExists() {
        // TODO: Проверить в базе данных наличие пробного питомца
        return false;
    }
} 