package com.example.exam_petpal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.exam_petpal.fragments.*;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // Инициализация Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация нижней навигации
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_events) {
                loadFragment(new CalendarFragment());
                return true;
            } else if (itemId == R.id.navigation_map) {
                loadFragment(new MapFragment());
                return true;
            } else if (itemId == R.id.navigation_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        // Загружаем домашний фрагмент по умолчанию
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Поиск по имени питомца");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
                if (homeFragment != null) {
                    homeFragment.filterPets(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
                if (homeFragment != null) {
                    homeFragment.filterPets(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            loadFragment(new ProfileFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 