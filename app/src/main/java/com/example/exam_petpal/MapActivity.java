package com.example.exam_petpal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";
    private static final String API_KEY = "7b5a77a9-4c3c-4130-ade8-224f80b8da67";
    private static final String BASE_URL = "https://catalog.api.2gis.com/3.0/items";
    private static final String YANDEX_API_KEY = "e26934a8-6c9f-46d1-ac0b-e32f29b0195a";
    private WebView webView;
    private String currentType = "vet";
    private List<Place> places = new ArrayList<>();
    private ProgressBar progressBar;
    private Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Карта");

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        progressBar = findViewById(R.id.progressBar);
        setupCategorySpinner();
        loadPlaces();
    }

    private void setupCategorySpinner() {
        categorySpinner = findViewById(R.id.categorySpinner);
        String[] categories = {"Ветеринарные клиники", "Зоомагазины", "Питомники", "Груминг салоны"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: currentType = "vet"; break;
                    case 1: currentType = "petshop"; break;
                    case 2: currentType = "kennel"; break;
                    case 3: currentType = "grooming"; break;
                }
                loadPlaces();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Ничего не делаем, если ничего не выбрано
            }
        });
    }

    private void loadPlaces() {
        showLoading(true);
        new Thread(() -> {
            try {
                String url = BASE_URL + "?q=" + getSearchQuery() + "&key=" + API_KEY + "&region_id=42&fields=items.point,items.name,items.address_name,items.rating";
                Log.d(TAG, "Loading places from URL: " + url);
                
                List<Place> newPlaces = fetchPlaces(url, getPlaceType());
                Log.d(TAG, "Fetched " + newPlaces.size() + " places");
                
                places.clear();
                places.addAll(newPlaces);
                
                runOnUiThread(() -> {
                    updateMap();
                    showLoading(false);
                });
            } catch (IOException e) {
                Log.e(TAG, "Error loading places", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                });
            }
        }).start();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String getSearchQuery() {
        switch (currentType) {
            case "vet":
                return "ветеринарная клиника";
            case "petshop":
                return "зоомагазин";
            case "kennel":
                return "питомник собак";
            case "grooming":
                return "груминг салон";
            default:
                return "ветеринарная клиника";
        }
    }

    private String getPlaceType() {
        switch (currentType) {
            case "vet":
                return "Ветеринарная клиника";
            case "petshop":
                return "Зоомагазин";
            case "kennel":
                return "Питомник";
            case "grooming":
                return "Груминг салон";
            default:
                return "Ветеринарная клиника";
        }
    }

    private List<Place> fetchPlaces(String url, String type) throws IOException {
        List<Place> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "API request failed: " + response);
                throw new IOException("Unexpected response " + response);
            }

            String responseBody = response.body().string();
            Log.d(TAG, "API response: " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject resultObj = jsonResponse.getJSONObject("result");
            JSONArray items = resultObj.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (item.has("point")) {
                    JSONObject point = item.getJSONObject("point");
                    Place place = new Place(
                        item.getString("name"),
                        point.getDouble("lat"),
                        point.getDouble("lon"),
                        type,
                        item.optDouble("rating", 0.0)
                    );
                    result.add(place);
                    Log.d(TAG, "Added place: " + place.name + " at " + place.lat + "," + place.lon);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing API response", e);
        }
        return result;
    }

    private void updateMap() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body>");
        html.append("<div id=\"map\" style=\"width:100%;height:100vh;\"></div>");
        html.append("<script src=\"https://api-maps.yandex.ru/2.1/?apikey=").append(YANDEX_API_KEY).append("&lang=ru_RU\"></script>");
        html.append("<script>");
        html.append("ymaps.ready(function() {");
        html.append("var map = new ymaps.Map('map', {");
        html.append("center: [54.8728, 69.1403],");
        html.append("zoom: 12");
        html.append("});");

        Log.d(TAG, "Adding " + places.size() + " markers to map");
        for (Place place : places) {
            html.append("var placemark = new ymaps.Placemark([")
                .append(place.lat).append(",").append(place.lon).append("], {");
            html.append("balloonContent: '")
                .append(place.name)
                .append("<br>")
                .append(place.type)
                .append("<br>Рейтинг: ")
                .append(String.format("%.1f", place.rating))
                .append("'");
            html.append("}, {");
            html.append("preset: 'islands#blueStretchyIcon',");
            html.append("balloonMaxWidth: 200");
            html.append("});");
            html.append("map.geoObjects.add(placemark);");
        }

        html.append("map.events.add('load', function() {");
        html.append("console.log('Map loaded successfully');");
        html.append("});");
        html.append("map.events.add('error', function(e) {");
        html.append("console.error('Map error:', e);");
        html.append("});");

        html.append("});");
        html.append("</script></body></html>");

        String htmlContent = html.toString();
        Log.d(TAG, "Loading map HTML: " + htmlContent);
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private static class Place {
        String name;
        double lat;
        double lon;
        String type;
        double rating;

        Place(String name, double lat, double lon, String type, double rating) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
            this.type = type;
            this.rating = rating;
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