package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация views
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerBtn);
        loginButton = findViewById(R.id.loginBtn);

        // Обработка нажатия на кнопку регистрации
        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    // Сохраняем данные пользователя
                    String name = nameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();
                    AuthManager.register(this, name, email, password);
                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при регистрации", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработка нажатия на кнопку входа
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Проверка имени
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Введите имя");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

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

        // Проверка подтверждения пароля
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Подтвердите пароль");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Пароли не совпадают");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return isValid;
    }
} 