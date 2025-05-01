package com.example.exam_petpal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 101;
    private ImageView profilePhoto;
    private EditText nameInput;
    private EditText passwordInput;
    private Button saveButton;
    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePhoto = findViewById(R.id.profilePhoto);
        nameInput = findViewById(R.id.editNameInput);
        passwordInput = findViewById(R.id.editPasswordInput);
        saveButton = findViewById(R.id.saveProfileButton);

        nameInput.setText(AuthManager.getUserName(this));
        // Фото профиля можно хранить в SharedPreferences (добавить поле при необходимости)

        profilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
                return;
            }
            // Здесь можно добавить валидацию пароля и сохранение фото
            AuthManager.login(this, name, AuthManager.getUserEmail(this));
            Toast.makeText(this, "Профиль обновлён", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoUri);
                profilePhoto.setImageBitmap(bitmap);
                // Здесь можно сохранить Uri фото в SharedPreferences
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 