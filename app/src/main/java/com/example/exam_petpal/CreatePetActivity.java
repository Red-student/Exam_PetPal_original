package com.example.exam_petpal;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatePetActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private ImageView petImage;
    private TextInputLayout nameLayout;
    private TextInputLayout typeLayout;
    private TextInputLayout breedLayout;
    private TextInputLayout birthDateLayout;
    private TextInputLayout weightLayout;
    private TextInputLayout genderLayout;
    private TextInputEditText nameInput;
    private TextInputEditText typeInput;
    private TextInputEditText breedInput;
    private TextInputEditText birthDateInput;
    private TextInputEditText weightInput;
    private TextInputEditText genderInput;
    private Button saveButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private boolean isTrial;
    private Uri selectedPhotoUri;
    private PetManager petManager;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedPhotoUri = result.getData().getData();
                petImage.setImageURI(selectedPhotoUri);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet);

        // Получаем флаг пробного режима
        isTrial = getIntent().getBooleanExtra("isTrial", false);

        // Инициализация менеджера питомцев
        petManager = PetManager.getInstance(this);

        // Проверка возможности создания питомца
        if (isTrial && !petManager.canAddTrialPet()) {
            Toast.makeText(this, "Вы уже создали пробного питомца", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if (!isTrial && !petManager.canAddPet()) {
            Toast.makeText(this, "Достигнут лимит питомцев (5)", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация календаря и формата даты
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        // Инициализация views
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        petImage = findViewById(R.id.petImage);
        nameLayout = findViewById(R.id.nameLayout);
        typeLayout = findViewById(R.id.typeLayout);
        breedLayout = findViewById(R.id.breedLayout);
        birthDateLayout = findViewById(R.id.birthDateLayout);
        weightLayout = findViewById(R.id.weightLayout);
        genderLayout = findViewById(R.id.genderLayout);
        nameInput = findViewById(R.id.nameInput);
        typeInput = findViewById(R.id.typeInput);
        breedInput = findViewById(R.id.breedInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        weightInput = findViewById(R.id.weightInput);
        genderInput = findViewById(R.id.genderInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        // Обработка нажатия на изображение
        petImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openImagePicker();
            } else {
                requestStoragePermission();
            }
        });

        // Обработка нажатия на поле даты рождения
        birthDateInput.setOnClickListener(v -> showDatePicker());

        // Обработка нажатия на поле пола
        genderInput.setOnClickListener(v -> showGenderDialog());

        // Обработка нажатия на кнопку сохранения
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                savePet();
            }
        });
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Для добавления фото необходимо разрешение", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImage.launch(intent);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                birthDateInput.setText(dateFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showGenderDialog() {
        String[] genders = {"Мальчик", "Девочка"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Выберите пол")
            .setItems(genders, (dialog, which) -> {
                genderInput.setText(genders[which]);
            })
            .show();
    }

    private void savePet() {
        try {
            String name = nameInput.getText().toString().trim();
            String type = typeInput.getText().toString().trim();
            String breed = breedInput.getText().toString().trim();
            Date birthDate = dateFormat.parse(birthDateInput.getText().toString().trim());
            double weight = Double.parseDouble(weightInput.getText().toString().trim());
            String gender = genderInput.getText().toString().trim();

            String photoPath = null;
            if (selectedPhotoUri != null) {
                photoPath = petManager.savePetPhoto(this, selectedPhotoUri);
            }

            Pet pet = new Pet(name, type, breed, birthDate, weight, gender, photoPath, isTrial);
            petManager.addPet(pet);

            Toast.makeText(this, "Питомец успешно создан", Toast.LENGTH_SHORT).show();
            finish();
        } catch (ParseException e) {
            Toast.makeText(this, "Ошибка при сохранении питомца", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Проверка имени
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Введите имя питомца");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        // Проверка типа
        String type = typeInput.getText().toString().trim();
        if (TextUtils.isEmpty(type)) {
            typeLayout.setError("Введите тип питомца");
            isValid = false;
        } else {
            typeLayout.setError(null);
        }

        // Проверка породы
        String breed = breedInput.getText().toString().trim();
        if (TextUtils.isEmpty(breed)) {
            breedLayout.setError("Введите породу");
            isValid = false;
        } else {
            breedLayout.setError(null);
        }

        // Проверка даты рождения
        String birthDate = birthDateInput.getText().toString().trim();
        if (TextUtils.isEmpty(birthDate)) {
            birthDateLayout.setError("Выберите дату рождения");
            isValid = false;
        } else {
            birthDateLayout.setError(null);
        }

        // Проверка веса
        String weight = weightInput.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            weightLayout.setError("Введите вес");
            isValid = false;
        } else {
            try {
                double weightValue = Double.parseDouble(weight);
                if (weightValue <= 0) {
                    weightLayout.setError("Вес должен быть больше 0");
                    isValid = false;
                } else {
                    weightLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                weightLayout.setError("Введите корректный вес");
                isValid = false;
            }
        }

        // Проверка пола
        String gender = genderInput.getText().toString().trim();
        if (TextUtils.isEmpty(gender)) {
            genderLayout.setError("Выберите пол");
            isValid = false;
        } else {
            genderLayout.setError(null);
        }

        return isValid;
    }
} 