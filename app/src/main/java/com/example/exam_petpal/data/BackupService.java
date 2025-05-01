package com.example.exam_petpal.data;

import android.content.Context;
import android.util.Log;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Event;
import com.example.exam_petpal.models.Vaccine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BackupService {
    private static final String TAG = "BackupService";
    private static final String BACKUP_DIR = "backups";
    private static final String PETS_FILE = "pets.json";
    private static final String EVENTS_FILE = "events.json";
    private static final String VACCINES_FILE = "vaccines.json";

    private Context context;
    private Gson gson;
    private static BackupService instance;

    private BackupService(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
    }

    public static synchronized BackupService getInstance(Context context) {
        if (instance == null) {
            instance = new BackupService(context);
        }
        return instance;
    }

    public void createBackup() {
        try {
            // Создаем директорию для бэкапов
            File backupDir = new File(context.getFilesDir(), BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // Бэкап питомцев
            List<Pet> pets = PetManager.getInstance(context).getAllPets();
            saveToFile(backupDir, PETS_FILE, pets);

            // Бэкап событий
            List<Event> events = new ArrayList<>(); // Получить события из базы данных
            saveToFile(backupDir, EVENTS_FILE, events);

            // Бэкап вакцин
            List<Vaccine> vaccines = new ArrayList<>(); // Получить вакцины из базы данных
            saveToFile(backupDir, VACCINES_FILE, vaccines);

            Log.d(TAG, "Backup created successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error creating backup", e);
        }
    }

    public void restoreBackup() {
        try {
            File backupDir = new File(context.getFilesDir(), BACKUP_DIR);
            if (!backupDir.exists()) {
                Log.e(TAG, "Backup directory does not exist");
                return;
            }

            // Восстановление питомцев
            List<Pet> pets = loadFromFile(backupDir, PETS_FILE, new TypeToken<List<Pet>>(){}.getType());
            if (pets != null) {
                PetManager.getInstance(context).restorePets(pets);
            }

            // Восстановление событий
            List<Event> events = loadFromFile(backupDir, EVENTS_FILE, new TypeToken<List<Event>>(){}.getType());
            if (events != null) {
                // Восстановить события в базу данных
            }

            // Восстановление вакцин
            List<Vaccine> vaccines = loadFromFile(backupDir, VACCINES_FILE, new TypeToken<List<Vaccine>>(){}.getType());
            if (vaccines != null) {
                // Восстановить вакцины в базу данных
            }

            Log.d(TAG, "Backup restored successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error restoring backup", e);
        }
    }

    private <T> void saveToFile(File dir, String filename, T data) throws IOException {
        File file = new File(dir, filename);
        String json = gson.toJson(data);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(json.getBytes());
        }
    }

    private <T> T loadFromFile(File dir, String filename, Type type) throws IOException {
        File file = new File(dir, filename);
        if (!file.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            String json = new String(buffer);
            return gson.fromJson(json, type);
        }
    }
} 