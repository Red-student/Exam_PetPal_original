package com.example.exam_petpal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;

public class PetVaccinesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_vaccines);

        if (savedInstanceState == null) {
            long petId = getIntent().getLongExtra("pet_id", -1);
            if (petId != -1) {
                Pet pet = null;
                for (Pet p : PetManager.getInstance(this).getPets()) {
                    if (p.getId().equals(String.valueOf(petId))) {
                        pet = p;
                        break;
                    }
                }
                if (pet != null) {
                    PetVaccinesFragment fragment = PetVaccinesFragment.newInstance(pet);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.commit();
                }
            }
        }
    }
} 