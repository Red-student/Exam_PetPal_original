package com.example.exam_petpal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SocialIntegrationActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_integration);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);

        // Инициализация Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Настройка кнопок
        Button whatsappButton = findViewById(R.id.whatsapp_button);
        Button googleButton = findViewById(R.id.google_button);
        Button vkButton = findViewById(R.id.vk_button);

        whatsappButton.setOnClickListener(v -> openWhatsApp());
        googleButton.setOnClickListener(v -> signIn());
        vkButton.setOnClickListener(v -> openVkAuth());
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.whatsapp_button).setEnabled(!show);
        findViewById(R.id.google_button).setEnabled(!show);
        findViewById(R.id.vk_button).setEnabled(!show);
    }

    private void openWhatsApp() {
        try {
            showLoading(true);
            String phoneNumber = "87052683304";
            String message = "Здравствуйте! Я хочу войти в приложение PetPal.";
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp не установлен", Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }

    private void signIn() {
        showLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void openVkAuth() {
        try {
            showLoading(true);
            String vkAuthUrl = "https://oauth.vk.com/authorize?client_id=YOUR_VK_APP_ID&redirect_uri=YOUR_REDIRECT_URI&scope=email";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vkAuthUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при открытии VK", Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(SocialIntegrationActivity.this, "Успешный вход через Google", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SocialIntegrationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SocialIntegrationActivity.this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showLoading(false);
                Toast.makeText(this, "Ошибка входа через Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLoading(false);
    }
} 