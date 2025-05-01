package com.example.exam_petpal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class AddPetActivity extends AppCompatActivity {
    private EditText nameInput;
    private Spinner typeInput;
    private Spinner breedInput;
    private EditText birthDateInput;
    private EditText weightInput;
    private Spinner genderInput;
    private Button saveButton;
    private ImageView petPhoto;
    private PetManager petManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();
    private Uri selectedPhotoUri;

    private final String[] petTypes = {"Собака", "Кошка", "Птица", "Грызун", "Рыбка", "Другое"};
    private final String[][] petBreeds = {
        {"Лабрадор", "Немецкая овчарка", "Хаски", "Дворняжка", "Другое"}, // Породы собак
        {"Сиамская", "Персидская", "Мейн-кун", "Сфинкс", "Дворовая", "Другое"}, // Породы кошек
        {"Волнистый попугай", "Канарейка", "Ара", "Другое"}, // Виды птиц
        {"Хомяк", "Морская свинка", "Кролик", "Другое"}, // Виды грызунов
        {"Золотая рыбка", "Гуппи", "Скалярия", "Другое"}, // Виды рыб
        {"Другое"} // Другое
    };
    private final String[] genders = {"Мальчик", "Девочка"};

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedPhotoUri = result.getData().getData();
                Glide.with(this)
                    .load(selectedPhotoUri)
                    .placeholder(R.drawable.default_pet)
                    .error(R.drawable.default_pet)
                    .centerCrop()
                    .into(petPhoto);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Добавить питомца");

        petManager = PetManager.getInstance(this);

        initializeViews();
        setupTypeSpinner();
        setupGenderSpinner();
        setupDatePicker();
        setupSaveButton();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.petNameInput);
        typeInput = findViewById(R.id.petTypeSpinner);
        breedInput = findViewById(R.id.petBreedSpinner);
        birthDateInput = findViewById(R.id.petBirthDateInput);
        weightInput = findViewById(R.id.petWeightInput);
        genderInput = findViewById(R.id.petGenderInput);
        saveButton = findViewById(R.id.saveButton);
        petPhoto = findViewById(R.id.petPhoto);

        // Добавляем анимацию появления
        View rootView = findViewById(R.id.rootLayout);
        rootView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // Обработка нажатия на фото
        petPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
    }

    private void setupTypeSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, petTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeInput.setAdapter(typeAdapter);

        typeInput.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateBreedSpinner(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderInput.setAdapter(genderAdapter);
    }

    private void updateBreedSpinner(int typePosition) {
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, petBreeds[typePosition]);
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedInput.setAdapter(breedAdapter);
    }

    private void setupDatePicker() {
        birthDateInput.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            birthDateInput.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                savePet();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(nameInput.getText())) {
            ((TextInputLayout) nameInput.getParent()).setError("Введите имя питомца");
            isValid = false;
        } else {
            ((TextInputLayout) nameInput.getParent()).setError(null);
        }

        if (typeInput.getSelectedItem() == null) {
            isValid = false;
        }

        if (TextUtils.isEmpty(birthDateInput.getText())) {
            ((TextInputLayout) birthDateInput.getParent()).setError("Выберите дату рождения");
            isValid = false;
        } else {
            ((TextInputLayout) birthDateInput.getParent()).setError(null);
        }

        return isValid;
    }

    private void savePet() {
        try {
            String name = nameInput.getText().toString().trim();
            String type = typeInput.getSelectedItem().toString();
            String breed = breedInput.getSelectedItem().toString();
            Date birthDate = dateFormat.parse(birthDateInput.getText().toString());
            double weight = TextUtils.isEmpty(weightInput.getText()) ? 0.0 : 
                Double.parseDouble(weightInput.getText().toString());
            String gender = genderInput.getSelectedItem().toString();

            String photoPath = null;
            if (selectedPhotoUri != null) {
                photoPath = petManager.savePetPhoto(this, selectedPhotoUri);
            }

            Pet pet = new Pet();
            pet.setName(name);
            pet.setType(type);
            pet.setBreed(breed);
            pet.setBirthDate(birthDate);
            pet.setWeight(weight);
            pet.setGender(gender);
            pet.setPhotoUri(photoPath);

            petManager.addPet(pet);
            Toast.makeText(this, "Питомец успешно добавлен", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при сохранении питомца", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 