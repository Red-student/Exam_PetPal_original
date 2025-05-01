package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        TextView profileInfo = findViewById(R.id.profileInfo);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        TextView aboutText = findViewById(R.id.aboutText);
        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button premiumButton = findViewById(R.id.premiumButton);
        Button backupButton = findViewById(R.id.backupButton);
        Button restoreButton = findViewById(R.id.restoreButton);
        Button feedbackButton = findViewById(R.id.feedbackButton);
        Button faqButton = findViewById(R.id.faqButton);

        if (AuthManager.isLoggedIn(this)) {
            // Авторизованный пользователь
            String name = AuthManager.getUserName(this);
            String email = AuthManager.getUserEmail(this);
            profileInfo.setText("Имя: " + name + "\nEmail: " + email);
            profileInfo.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            aboutText.setVisibility(View.VISIBLE);
            editProfileButton.setVisibility(View.VISIBLE);
        } else {
            // Гость
            profileInfo.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            aboutText.setVisibility(View.VISIBLE);
            editProfileButton.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        logoutButton.setOnClickListener(v -> {
            AuthManager.logout(this);
            recreate();
        });
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        backupButton.setOnClickListener(v -> {
            backupToCloud();
        });
        restoreButton.setOnClickListener(v -> {
            restoreFromCloud();
        });
        feedbackButton.setOnClickListener(v -> showFeedbackDialog());
        faqButton.setOnClickListener(v -> startActivity(new Intent(this, FAQActivity.class)));
        premiumButton.setOnClickListener(v -> startActivity(new Intent(this, PremiumActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backupToCloud() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnSuccessListener(result -> backupToCloudInternal())
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка авторизации Firebase", Toast.LENGTH_SHORT).show());
        } else {
            backupToCloudInternal();
        }
    }

    private void backupToCloudInternal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Pet> pets = PetManager.getInstance(this).getPets();
        Map<String, Object> data = new HashMap<>();
        data.put("pets", pets);
        db.collection("backups").document(user.getUid())
            .set(data, SetOptions.merge())
            .addOnSuccessListener(unused -> Toast.makeText(this, "Данные успешно сохранены в облако", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(this, "Ошибка сохранения в облако", Toast.LENGTH_SHORT).show());
    }

    private void restoreFromCloud() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnSuccessListener(result -> restoreFromCloudInternal())
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка авторизации Firebase", Toast.LENGTH_SHORT).show());
        } else {
            restoreFromCloudInternal();
        }
    }

    private void restoreFromCloudInternal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("backups").document(user.getUid())
            .get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists() && snapshot.contains("pets")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> petsData = (List<Map<String, Object>>) snapshot.get("pets");
                    PetManager petManager = PetManager.getInstance(this);
                    petManager.clearAllPets();
                    for (Map<String, Object> petMap : petsData) {
                        Pet pet = PetManager.fromMap(petMap);
                        petManager.addPet(pet);
                    }
                    Toast.makeText(this, "Данные успешно восстановлены из облака", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Нет резервной копии в облаке", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Ошибка восстановления из облака", Toast.LENGTH_SHORT).show());
    }

    private void showFeedbackDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Ваше сообщение...");
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Обратная связь")
            .setView(input)
            .setPositiveButton("Отправить", (dialog, which) -> {
                String message = input.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendFeedbackEmail(message);
                } else {
                    Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void sendFeedbackEmail(String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"petpal.support@example.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Обратная связь PetPal");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(intent, "Выберите почтовый клиент"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Не найдено почтовых клиентов", Toast.LENGTH_SHORT).show();
        }
    }
} 