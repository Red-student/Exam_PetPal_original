package com.example.exam_petpal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Event;
import com.example.exam_petpal.models.Pet;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class AddEventActivity extends AppCompatActivity {
    private EditText titleInput;
    private EditText dateInput;
    private EditText timeInput;
    private EditText descriptionInput;
    private Button saveButton;
    private PetManager petManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Добавить событие");

        petManager = PetManager.getInstance(this);

        titleInput = findViewById(R.id.eventTitleInput);
        dateInput = findViewById(R.id.eventDateInput);
        timeInput = findViewById(R.id.eventTimeInput);
        descriptionInput = findViewById(R.id.eventDescriptionInput);
        saveButton = findViewById(R.id.saveButton);

        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> saveEvent());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateInput.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            timeInput.setText(timeFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveEvent() {
        String title = titleInput.getText().toString().trim();
        String dateStr = dateInput.getText().toString().trim();
        String timeStr = timeInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
            return;
        }

        try {
            Date eventDate = calendar.getTime();
            Event event = new Event();
            event.setTitle(title);
            event.setDate(eventDate);
            event.setDescription(description);
            event.setType("custom");
            
            // Добавляем событие первому питомцу (в реальном приложении нужно добавить выбор питомца)
            if (!petManager.getPets().isEmpty()) {
                Pet pet = petManager.getPets().get(0);
                pet.addEvent(event);
                petManager.updatePet(pet);
            }
            
            finish();
        } catch (Exception e) {
            e.printStackTrace();
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