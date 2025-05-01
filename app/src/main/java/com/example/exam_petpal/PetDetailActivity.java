package com.example.exam_petpal;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PetDetailActivity extends AppCompatActivity {
    private Pet pet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        String petId = getIntent().getStringExtra("pet_id");
        pet = null;
        for (Pet p : PetManager.getInstance(this).getPets()) {
            if (p.getId().equals(petId)) {
                pet = p;
                break;
            }
        }
        if (pet == null) {
            Toast.makeText(this, "Питомец не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        PetDetailPagerAdapter adapter = new PetDetailPagerAdapter(this, pet);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Инфо");
            else if (position == 1) tab.setText("Вакцины");
            else if (position == 2) tab.setText("События");
        }).attach();
    }
} 