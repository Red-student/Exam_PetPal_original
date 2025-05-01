package com.example.exam_petpal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PetManager {
    private static final String PREF_NAME = "pet_data";
    private static final String KEY_PETS = "pets";
    private static final String TAG = "PetManager";

    private static PetManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private List<Pet> pets;

    private PetManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadPets();
    }

    public static synchronized PetManager getInstance(Context context) {
        if (instance == null) {
            instance = new PetManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadPets() {
        String json = preferences.getString(KEY_PETS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Pet>>(){}.getType();
            pets = gson.fromJson(json, type);
        } else {
            pets = new ArrayList<>();
        }
    }

    private void savePets() {
        String json = gson.toJson(pets);
        preferences.edit().putString(KEY_PETS, json).apply();
    }

    public List<Pet> getPets() {
        return new ArrayList<>(pets.stream()
            .filter(pet -> !pet.isHidden())
            .collect(Collectors.toList()));
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(pets);
    }

    public Pet getPetById(String petId) {
        for (Pet pet : pets) {
            if (pet.getId().equals(petId)) {
                return pet;
            }
        }
        return null;
    }

    public void addPet(Pet pet) {
        pets.add(pet);
        savePets();
    }

    public void updatePet(Pet pet) {
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId().equals(pet.getId())) {
                pets.set(i, pet);
                savePets();
                break;
            }
        }
    }

    public void deletePet(String petId) {
        pets.removeIf(pet -> pet.getId().equals(petId));
        savePets();
    }

    public void hidePet(String petId) {
        for (Pet pet : pets) {
            if (pet.getId().equals(petId)) {
                pet.setHidden(true);
                savePets();
                break;
            }
        }
    }

    public void unhidePet(String petId) {
        for (Pet pet : pets) {
            if (pet.getId().equals(petId)) {
                pet.setHidden(false);
                savePets();
                break;
            }
        }
    }

    public void clearAllPets() {
        pets.clear();
        savePets();
    }

    public void restorePets(List<Pet> petsToRestore) {
        pets.clear();
        pets.addAll(petsToRestore);
        savePets();
    }

    public static Pet fromMap(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(map), Pet.class);
    }

    public String savePetPhoto(Context context, Uri photoUri) {
        try {
            // Создаем директорию для фото, если она не существует
            File photosDir = new File(context.getFilesDir(), "pet_photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            // Создаем уникальное имя файла
            String fileName = "pet_" + System.currentTimeMillis() + ".jpg";
            File photoFile = new File(photosDir, fileName);

            // Копируем фото из Uri в файл
            InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
            if (inputStream != null) {
                // Читаем и сжимаем изображение
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                // Сжимаем изображение
                FileOutputStream outputStream = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();

                // Возвращаем путь к сохраненному файлу
                return photoFile.getAbsolutePath();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving pet photo", e);
        }
        return null;
    }

    public boolean canAddTrialPet() {
        return pets.stream().filter(Pet::isTrial).count() < 1;
    }

    public boolean canAddPet() {
        return pets.size() < 5;
    }

    public void addVaccine(Vaccine vaccine) {
        Pet pet = getPetById(vaccine.getPetId());
        if (pet != null) {
            pet.addVaccine(vaccine);
            savePets();
        }
    }

    public void deleteVaccine(String vaccineId) {
        for (Pet pet : pets) {
            List<Vaccine> vaccines = pet.getVaccines();
            for (int i = 0; i < vaccines.size(); i++) {
                if (vaccines.get(i).getId().equals(vaccineId)) {
                    vaccines.remove(i);
                    savePets();
                    return;
                }
            }
        }
    }

    public List<Vaccine> getVaccinesForPet(String petId) {
        Pet pet = getPetById(petId);
        return pet != null ? pet.getVaccines() : new ArrayList<>();
    }
} 