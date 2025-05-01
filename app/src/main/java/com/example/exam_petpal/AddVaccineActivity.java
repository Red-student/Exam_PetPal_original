package com.example.exam_petpal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddVaccineActivity extends AppCompatActivity {
    private EditText vaccineNameInput;
    private EditText vaccineDateInput;
    private EditText nextVaccineDateInput;
    private EditText vaccineNotesInput;
    private MaterialButton saveButton;
    private PetManager petManager;
    private String petId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        petId = getIntent().getStringExtra("pet_id");
        if (petId == null) {
            Toast.makeText(this, "Ошибка: питомец не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        petManager = PetManager.getInstance(this);
        Pet pet = petManager.getPetById(petId);
        if (pet == null) {
            Toast.makeText(this, "Ошибка: питомец не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar(pet.getName());
        initializeViews();
        setupDatePickers();
        setupSaveButton();
    }

    private void setupToolbar(String petName) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Добавить вакцину для " + petName);
    }

    private void initializeViews() {
        vaccineNameInput = findViewById(R.id.vaccineNameInput);
        vaccineDateInput = findViewById(R.id.vaccineDateInput);
        nextVaccineDateInput = findViewById(R.id.nextVaccineDateInput);
        vaccineNotesInput = findViewById(R.id.vaccineNotesInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupDatePickers() {
        vaccineDateInput.setOnClickListener(v -> showDatePicker(vaccineDateInput));
        nextVaccineDateInput.setOnClickListener(v -> showDatePicker(nextVaccineDateInput));
    }

    private void showDatePicker(final EditText dateInput) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateInput.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
           calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveVaccine();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(vaccineNameInput.getText())) {
            ((TextInputLayout) vaccineNameInput.getParent().getParent())
                .setError("Введите название вакцины");
            isValid = false;
        }

        if (TextUtils.isEmpty(vaccineDateInput.getText())) {
            ((TextInputLayout) vaccineDateInput.getParent().getParent())
                .setError("Выберите дату вакцинации");
            isValid = false;
        }

        return isValid;
    }

    private void saveVaccine() {
        try {
            String name = vaccineNameInput.getText().toString().trim();
            Date date = dateFormat.parse(vaccineDateInput.getText().toString());
            Date nextDate = TextUtils.isEmpty(nextVaccineDateInput.getText()) ? null :
                dateFormat.parse(nextVaccineDateInput.getText().toString());
            String notes = vaccineNotesInput.getText().toString().trim();

            Vaccine vaccine = new Vaccine(petId, name, date, nextDate, notes);
            petManager.addVaccine(vaccine);

            Toast.makeText(this, "Вакцина добавлена", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при сохранении: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
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