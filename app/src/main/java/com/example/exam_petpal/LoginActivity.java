package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginBtn);
        registerButton = findViewById(R.id.registerBtn);

        // Обработка нажатия на кнопку входа
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    // Сохраняем статус авторизации
                    String email = emailInput.getText().toString().trim();
                    AuthManager.login(this, email, email); // Имя временно = email
                    Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при входе", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработка нажатия на кнопку регистрации
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Проверка email
        String email = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Введите email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Введите корректный email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // Проверка пароля
        String password = passwordInput.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Пароль должен содержать минимум 6 символов");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }
} 