package com.example.exam_petpal;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PetStatsActivity extends AppCompatActivity {
    private LineChart weightChart;
    private LineChart activityChart;
    private FirebaseFirestore db;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_stats);

        petId = getIntent().getStringExtra("petId");
        db = FirebaseFirestore.getInstance();

        weightChart = findViewById(R.id.weight_chart);
        activityChart = findViewById(R.id.activity_chart);

        setupCharts();
        loadData();
    }

    private void setupCharts() {
        // Настройка графика веса
        weightChart.getDescription().setText("Изменение веса");
        weightChart.getDescription().setTextColor(Color.BLACK);
        weightChart.setDrawGridBackground(false);
        weightChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        weightChart.getAxisRight().setEnabled(false);
        weightChart.getLegend().setEnabled(true);
        weightChart.animateX(1000);

        // Настройка графика активности
        activityChart.getDescription().setText("Уровень активности");
        activityChart.getDescription().setTextColor(Color.BLACK);
        activityChart.setDrawGridBackground(false);
        activityChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityChart.getAxisRight().setEnabled(false);
        activityChart.getLegend().setEnabled(true);
        activityChart.animateX(1000);
    }

    private void loadData() {
        // Загрузка данных о весе
        db.collection("pets").document(petId)
                .collection("weight_history")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Entry> weightEntries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        float weight = document.getDouble("weight").floatValue();
                        long timestamp = document.getLong("timestamp");
                        weightEntries.add(new Entry(timestamp, weight));
                    }
                    updateWeightChart(weightEntries);
                });

        // Загрузка данных об активности
        db.collection("pets").document(petId)
                .collection("activity_history")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Entry> activityEntries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        float activity = document.getDouble("activity_level").floatValue();
                        long timestamp = document.getLong("timestamp");
                        activityEntries.add(new Entry(timestamp, activity));
                    }
                    updateActivityChart(activityEntries);
                });
    }

    private void updateWeightChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Вес (кг)");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);
        weightChart.invalidate();
    }

    private void updateActivityChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Уровень активности");
        dataSet.setColor(Color.GREEN);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        activityChart.setData(lineData);
        activityChart.invalidate();
    }
} 