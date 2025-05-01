package com.example.exam_petpal;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.data.PetManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPetActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText typeInput;
    private EditText breedInput;
    private EditText birthDateInput;
    private EditText weightInput;
    private EditText genderInput;
    private Button saveButton;
    private PetManager petManager;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_pet);

        petManager = PetManager.getInstance(this);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        typeInput = findViewById(R.id.typeInput);
        breedInput = findViewById(R.id.breedInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        weightInput = findViewById(R.id.weightInput);
        genderInput = findViewById(R.id.genderInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> savePet());
    }

    private void savePet() {
        String name = nameInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        String breed = breedInput.getText().toString().trim();
        String birthDateStr = birthDateInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();
        String gender = genderInput.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || breed.isEmpty() || birthDateStr.isEmpty() || weightStr.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date birthDate = dateFormat.parse(birthDateStr);
            double weight = Double.parseDouble(weightStr);

            Pet pet = new Pet();
            pet.setName(name);
            pet.setType(type);
            pet.setBreed(breed);
            pet.setBirthDate(birthDate);
            pet.setWeight(weight);
            pet.setGender(gender);

            petManager.addPet(pet);
            Toast.makeText(this, R.string.pet_added, Toast.LENGTH_SHORT).show();
            finish();
        } catch (ParseException e) {
            Toast.makeText(this, R.string.invalid_date_format, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_weight_format, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 