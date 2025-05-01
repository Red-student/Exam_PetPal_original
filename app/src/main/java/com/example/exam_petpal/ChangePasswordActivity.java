package com.example.exam_petpal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.android.gms.tasks.Task;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText oldPasswordInput = findViewById(R.id.oldPasswordInput);
        EditText newPasswordInput = findViewById(R.id.newPasswordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button savePasswordButton = findViewById(R.id.savePasswordButton);

        savePasswordButton.setOnClickListener(v -> {
            String oldPass = oldPasswordInput.getText().toString().trim();
            String newPass = newPasswordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.getEmail() == null) {
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                return;
            }
            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), oldPass))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(this, "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(this, "Ошибка смены пароля", Toast.LENGTH_SHORT).show();
                                }
                            });
                    } else {
                        Toast.makeText(this, "Старый пароль неверен", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
} 