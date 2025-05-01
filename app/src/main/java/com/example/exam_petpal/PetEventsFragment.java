package com.example.exam_petpal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exam_petpal.models.Event;
import com.example.exam_petpal.models.Pet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.view.Gravity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AdapterView;

public class PetEventsFragment extends Fragment {
    private static final String ARG_PET = "pet";
    private Pet pet;
    private List<Event> events = new ArrayList<>();
    private LinearLayout eventsList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final String PREFS_NAME = "pet_events_prefs";
    private static final String EVENTS_KEY_PREFIX = "events_";
    private CalendarView calendarView;
    private Date selectedDate = null;
    private EditText searchEventInput;
    private Spinner typeFilterSpinner;
    private String searchQuery = "";
    private String selectedType = "Все";
    private final String[] eventTypes = {"Все", "custom", "вакцинация", "прогулка", "кормление"};

    public static PetEventsFragment newInstance(Pet pet) {
        PetEventsFragment fragment = new PetEventsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PET, pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_events, container, false);
        if (getArguments() != null) {
            pet = (Pet) getArguments().getSerializable(ARG_PET);
        }
        eventsList = view.findViewById(R.id.eventsList);
        Button addEventBtn = view.findViewById(R.id.addEventBtn);
        addEventBtn.setOnClickListener(v -> showAddEventDialog());
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            selectedDate = cal.getTime();
            updateEventsList();
        });
        searchEventInput = view.findViewById(R.id.searchEventInput);
        searchEventInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                updateEventsList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        typeFilterSpinner = view.findViewById(R.id.typeFilterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilterSpinner.setAdapter(adapter);
        typeFilterSpinner.setSelection(0);
        typeFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedType = eventTypes[position];
                updateEventsList();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        loadEvents();
        updateEventsList();
        return view;
    }

    private void updateEventsList() {
        eventsList.removeAllViews();
        List<Event> filtered = new ArrayList<>();
        for (Event e : events) {
            boolean matchesDate = true;
            if (selectedDate != null) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(selectedDate);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(e.getDate());
                matchesDate = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            }
            boolean matchesType = selectedType.equals("Все") || e.getType().getDisplayName().equals(selectedType);
            boolean matchesQuery = searchQuery.isEmpty() || e.getTitle().toLowerCase().contains(searchQuery.toLowerCase());
            if (matchesDate && matchesType && matchesQuery) {
                filtered.add(e);
            }
        }
        for (int i = 0; i < filtered.size(); i++) {
            final int index = events.indexOf(filtered.get(i));
            Event event = filtered.get(i);
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
            tv.setText(event.getTitle() + " (" + sdf.format(event.getDate()) + ")\n" + event.getDescription());
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
            editBtn.setOnClickListener(v -> showEditEventDialog(index));
            btnRow.addView(editBtn);

            Button delBtn = new Button(getContext());
            delBtn.setText("Удалить");
            delBtn.setTextColor(Color.WHITE);
            delBtn.setBackgroundColor(getResources().getColor(R.color.error));
            delBtn.setOnClickListener(v -> {
                cancelNotification(event);
                events.remove(index);
                saveEvents();
                updateEventsList();
            });
            btnRow.addView(delBtn);

            card.addView(btnRow);
            eventsList.addView(card);
        }
    }

    private GradientDrawable createCardBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(32f);
        drawable.setStroke(2, getResources().getColor(R.color.accent_tertiary));
        return drawable;
    }

    private void showAddEventDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_event, null);
        EditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
        EditText dateInput = dialogView.findViewById(R.id.eventDateInput);
        EditText descInput = dialogView.findViewById(R.id.eventDescInput);
        CheckBox repeatWeeklyCheck = dialogView.findViewById(R.id.repeatWeeklyCheck);
        CheckBox repeatMonthlyCheck = dialogView.findViewById(R.id.repeatMonthlyCheck);
        Calendar calendar = Calendar.getInstance();
        dateInput.setOnClickListener(v -> showDatePicker(dateInput, calendar));
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Добавить событие")
            .setView(dialogView)
            .setPositiveButton("Добавить", (dialog, which) -> {
                String title = titleInput.getText().toString().trim();
                String dateStr = dateInput.getText().toString().trim();
                String desc = descInput.getText().toString().trim();
                boolean repeatWeekly = repeatWeeklyCheck.isChecked();
                boolean repeatMonthly = repeatMonthlyCheck.isChecked();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(dateStr)) {
                    Toast.makeText(getContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Date date = sdf.parse(dateStr);
                    Event event = new Event(
                        String.valueOf(System.currentTimeMillis()),
                        pet.getId(),
                        title,
                        desc,
                        date,
                        Event.EventType.OTHER,
                        repeatWeekly,
                        repeatMonthly
                    );
                    events.add(event);
                    saveEvents();
                    updateEventsList();
                    scheduleNotification(event);
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

    private void showEditEventDialog(int index) {
        Event event = events.get(index);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_event, null);
        EditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
        EditText dateInput = dialogView.findViewById(R.id.eventDateInput);
        EditText descInput = dialogView.findViewById(R.id.eventDescInput);
        CheckBox repeatWeeklyCheck = dialogView.findViewById(R.id.repeatWeeklyCheck);
        CheckBox repeatMonthlyCheck = dialogView.findViewById(R.id.repeatMonthlyCheck);
        titleInput.setText(event.getTitle());
        dateInput.setText(sdf.format(event.getDate()));
        descInput.setText(event.getDescription());
        repeatWeeklyCheck.setChecked(event.isRepeatWeekly());
        repeatMonthlyCheck.setChecked(event.isRepeatMonthly());
        Calendar calendar = Calendar.getInstance();
        dateInput.setOnClickListener(v -> showDatePicker(dateInput, calendar));
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Редактировать событие")
            .setView(dialogView)
            .setPositiveButton("Сохранить", (dialog, which) -> {
                String title = titleInput.getText().toString().trim();
                String dateStr = dateInput.getText().toString().trim();
                String desc = descInput.getText().toString().trim();
                boolean repeatWeekly = repeatWeeklyCheck.isChecked();
                boolean repeatMonthly = repeatMonthlyCheck.isChecked();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(dateStr)) {
                    Toast.makeText(getContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Date date = sdf.parse(dateStr);
                    cancelNotification(event);
                    event.setTitle(title);
                    event.setDate(date);
                    event.setDescription(desc);
                    event.setRepeatWeekly(repeatWeekly);
                    event.setRepeatMonthly(repeatMonthly);
                    saveEvents();
                    updateEventsList();
                    scheduleNotification(event);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Ошибка даты", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void scheduleNotification(Event event) {
        Context context = getContext();
        if (context == null) return;
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("desc", event.getDescription());
        int requestCode = event.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long triggerAtMillis = event.getDate().getTime();
        if (event.isRepeatWeekly()) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        } else if (event.isRepeatMonthly()) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private void cancelNotification(Event event) {
        Context context = getContext();
        if (context == null) return;
        Intent intent = new Intent(context, ReminderReceiver.class);
        int requestCode = event.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void saveEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        for (Event e : events) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", e.getId());
                obj.put("petId", e.getPetId());
                obj.put("title", e.getTitle());
                obj.put("description", e.getDescription());
                obj.put("date", e.getDate().getTime());
                obj.put("type", e.getType().name());
                obj.put("repeatWeekly", e.isRepeatWeekly());
                obj.put("repeatMonthly", e.isRepeatMonthly());
                arr.put(obj);
            } catch (JSONException ex) { ex.printStackTrace(); }
        }
        prefs.edit().putString(EVENTS_KEY_PREFIX + pet.getId(), arr.toString()).apply();
    }

    private void loadEvents() {
        events.clear();
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(EVENTS_KEY_PREFIX + pet.getId(), null);
        if (json != null) {
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Event e = new Event(
                        obj.getString("id"),
                        obj.getString("petId"),
                        obj.getString("title"),
                        obj.getString("description"),
                        new Date(obj.getLong("date")),
                        Event.EventType.valueOf(obj.getString("type")),
                        obj.getBoolean("repeatWeekly"),
                        obj.getBoolean("repeatMonthly")
                    );
                    events.add(e);
                }
            } catch (JSONException ex) { ex.printStackTrace(); }
        }
    }
} 