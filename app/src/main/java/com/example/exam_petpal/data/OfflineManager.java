package com.example.exam_petpal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Event;
import com.example.exam_petpal.models.Vaccine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OfflineManager {
    private static final String TAG = "OfflineManager";
    private static final String PREFS_NAME = "OfflinePrefs";
    private static final String KEY_PETS = "offline_pets";
    private static final String KEY_EVENTS = "offline_events";
    private static final String KEY_VACCINES = "offline_vaccines";
    private static final String KEY_LAST_SYNC = "last_sync_time";

    private Context context;
    private SharedPreferences prefs;
    private Gson gson;
    private static OfflineManager instance;

    private OfflineManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public static synchronized OfflineManager getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineManager(context);
        }
        return instance;
    }

    public void savePetsOffline(List<Pet> pets) {
        String json = gson.toJson(pets);
        prefs.edit().putString(KEY_PETS, json).apply();
        updateLastSyncTime();
    }

    public void saveEventsOffline(List<Event> events) {
        String json = gson.toJson(events);
        prefs.edit().putString(KEY_EVENTS, json).apply();
        updateLastSyncTime();
    }

    public void saveVaccinesOffline(List<Vaccine> vaccines) {
        String json = gson.toJson(vaccines);
        prefs.edit().putString(KEY_VACCINES, json).apply();
        updateLastSyncTime();
    }

    public List<Pet> getOfflinePets() {
        String json = prefs.getString(KEY_PETS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Pet>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public List<Event> getOfflineEvents() {
        String json = prefs.getString(KEY_EVENTS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Event>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public List<Vaccine> getOfflineVaccines() {
        String json = prefs.getString(KEY_VACCINES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Vaccine>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public long getLastSyncTime() {
        return prefs.getLong(KEY_LAST_SYNC, 0);
    }

    private void updateLastSyncTime() {
        prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply();
    }

    public void clearOfflineData() {
        prefs.edit().clear().apply();
    }

    public boolean hasOfflineData() {
        return prefs.contains(KEY_PETS) || 
               prefs.contains(KEY_EVENTS) || 
               prefs.contains(KEY_VACCINES);
    }

    public void syncWithServer() {
        // Здесь должна быть логика синхронизации с сервером
        // Например, отправка изменений на сервер и получение обновлений
        Log.d(TAG, "Syncing with server...");
    }
} 