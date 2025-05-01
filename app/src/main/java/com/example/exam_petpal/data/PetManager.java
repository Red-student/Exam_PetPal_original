package com.example.exam_petpal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.exam_petpal.models.Pet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return new ArrayList<>(pets);
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(pets);
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
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(photoUri));
            String fileName = "pet_photo_" + System.currentTimeMillis() + ".jpg";
            File file = new File(context.getFilesDir(), fileName);
            
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving pet photo", e);
            return null;
        }
    }

    public boolean canAddTrialPet() {
        return pets.stream().filter(Pet::isTrial).count() < 1;
    }

    public boolean canAddPet() {
        return pets.size() < 5;
    }
} 