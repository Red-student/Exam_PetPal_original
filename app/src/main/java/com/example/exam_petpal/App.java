package com.example.exam_petpal;

import android.app.Application;
import androidx.work.Configuration;
import androidx.work.WorkManager;

public class App extends Application implements Configuration.Provider {
    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация WorkManager
        WorkManager.getInstance(this);
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build();
    }
} 