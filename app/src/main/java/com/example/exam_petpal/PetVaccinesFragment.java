package com.example.exam_petpal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.view.Gravity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class PetVaccinesFragment extends Fragment {
    private static final String ARG_PET = "pet";
    private Pet pet;
    private LinearLayout vaccinesList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static PetVaccinesFragment newInstance(Pet pet) {
        PetVaccinesFragment fragment = new PetVaccinesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PET, pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_vaccines, container, false);
        if (getArguments() != null) {
            pet = (Pet) getArguments().getSerializable(ARG_PET);
        }
        vaccinesList = view.findViewById(R.id.vaccinesList);
        Button addVaccineBtn = view.findViewById(R.id.addVaccineBtn);
        addVaccineBtn.setOnClickListener(v -> showAddVaccineDialog());
        updateVaccinesList();
        return view;
    }

    private void updateVaccinesList() {
        vaccinesList.removeAllViews();
        for (int i = 0; i < pet.getVaccines().size(); i++) {
            final int index = i;
            Vaccine vaccine = pet.getVaccines().get(i);
            LinearLayout card = new LinearLayout(getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(32, 24, 32, 24);
            card.setBackground(createCardBackground());
            card.setElevation(8f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 24);
            card.setLayoutParams(params);

            TextView tv = new TextView(getContext());
            tv.setText(vaccine.getName() + " (" + sdf.format(vaccine.getDate()) + ")\n" + vaccine.getDescription());
            tv.setTextColor(Color.parseColor("#212121"));
            tv.setTextSize(16f);
            card.addView(tv);

            LinearLayout btnRow = new LinearLayout(getContext());
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            btnRow.setGravity(Gravity.END);

            Button editBtn = new Button(getContext());
            editBtn.setText("Редактировать");
            editBtn.setTextColor(Color.WHITE);
            editBtn.setBackgroundColor(getResources().getColor(R.color.accent_secondary));
            editBtn.setOnClickListener(v -> showEditVaccineDialog(index));
            btnRow.addView(editBtn);

            Button delBtn = new Button(getContext());
            delBtn.setText("Удалить");
            delBtn.setTextColor(Color.WHITE);
            delBtn.setBackgroundColor(getResources().getColor(R.color.error));
            delBtn.setOnClickListener(v -> {
                pet.getVaccines().remove(index);
                updateVaccinesList();
            });
            btnRow.addView(delBtn);

            card.addView(btnRow);
            vaccinesList.addView(card);
        }
    }

    private GradientDrawable createCardBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(32f);
        drawable.setStroke(2, getResources().getColor(R.color.accent_primary));
        return drawable;
    }

    private void showEditVaccineDialog(int index) {
        Vaccine vaccine = pet.getVaccines().get(index);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_vaccine, null);
        EditText nameInput = dialogView.findViewById(R.id.vaccineNameInput);
        EditText dateInput = dialogView.findViewById(R.id.vaccineDateInput);
        EditText expiryInput = dialogView.findViewById(R.id.vaccineExpiryInput);
        EditText descInput = dialogView.findViewById(R.id.vaccineDescInput);
        nameInput.setText(vaccine.getName());
        dateInput.setText(sdf.format(vaccine.getDate()));
        expiryInput.setText(vaccine.getExpirationDate() != null ? sdf.format(vaccine.getExpirationDate()) : "");
        descInput.setText(vaccine.getDescription());
        Calendar calendar = Calendar.getInstance();
        dateInput.setOnClickListener(v -> showDatePicker(dateInput, calendar));
        expiryInput.setOnClickListener(v -> showDatePicker(expiryInput, calendar));
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Редактировать вакцину")
            .setView(dialogView)
            .setPositiveButton("Сохранить", (dialog, which) -> {
                String name = nameInput.getText().toString().trim();
                String dateStr = dateInput.getText().toString().trim();
                String expiryStr = expiryInput.getText().toString().trim();
                String desc = descInput.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dateStr)) {
                    Toast.makeText(getContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Date date = sdf.parse(dateStr);
                    Date expiry = TextUtils.isEmpty(expiryStr) ? null : sdf.parse(expiryStr);
                    vaccine.setName(name);
                    vaccine.setDate(date);
                    vaccine.setExpirationDate(expiry);
                    vaccine.setDescription(desc);
                    updateVaccinesList();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Ошибка даты", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void showAddVaccineDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_vaccine, null);
        EditText nameInput = dialogView.findViewById(R.id.vaccineNameInput);
        EditText dateInput = dialogView.findViewById(R.id.vaccineDateInput);
        EditText expiryInput = dialogView.findViewById(R.id.vaccineExpiryInput);
        EditText descInput = dialogView.findViewById(R.id.vaccineDescInput);
        Calendar calendar = Calendar.getInstance();
        dateInput.setOnClickListener(v -> showDatePicker(dateInput, calendar));
        expiryInput.setOnClickListener(v -> showDatePicker(expiryInput, calendar));
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Добавить вакцину")
            .setView(dialogView)
            .setPositiveButton("Добавить", (dialog, which) -> {
                String name = nameInput.getText().toString().trim();
                String dateStr = dateInput.getText().toString().trim();
                String expiryStr = expiryInput.getText().toString().trim();
                String desc = descInput.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dateStr)) {
                    Toast.makeText(getContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Date date = sdf.parse(dateStr);
                    Date expiry = TextUtils.isEmpty(expiryStr) ? null : sdf.parse(expiryStr);
                    Vaccine vaccine = new Vaccine(name, date, expiry, desc);
                    pet.addVaccine(vaccine);
                    updateVaccinesList();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Ошибка даты", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void showDatePicker(EditText target, Calendar calendar) {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            target.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
} 