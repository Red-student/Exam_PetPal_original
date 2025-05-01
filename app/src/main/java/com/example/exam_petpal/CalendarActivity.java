package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.CalendarView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.example.exam_petpal.adapters.EventAdapter;
import com.example.exam_petpal.models.Event;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> allEvents;
    private PetManager petManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        petManager = PetManager.getInstance(this);
        allEvents = new ArrayList<>();
        for (Pet pet : petManager.getPets()) {
            allEvents.addAll(pet.getEvents());
        }

        calendarView = findViewById(R.id.calendarView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(new ArrayList<>());
        eventsRecyclerView.setAdapter(eventAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            updateEventsList(selectedDate.getTime());
        });

        FloatingActionButton addEventFab = findViewById(R.id.addEventFab);
        addEventFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        });

        // Показываем события на текущую дату
        updateEventsList(new Date());
    }

    private void updateEventsList(Date selectedDate) {
        List<Event> eventsForDate = new ArrayList<>();
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = selectedCal.getTime();
        selectedCal.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = selectedCal.getTime();

        for (Event event : allEvents) {
            if (event.getDate().after(startOfDay) && event.getDate().before(endOfDay)) {
                eventsForDate.add(event);
            }
        }
        eventAdapter.updateEvents(eventsForDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем список событий при возвращении на экран
        allEvents.clear();
        for (Pet pet : petManager.getPets()) {
            allEvents.addAll(pet.getEvents());
        }
        updateEventsList(new Date(calendarView.getDate()));
    }
} 