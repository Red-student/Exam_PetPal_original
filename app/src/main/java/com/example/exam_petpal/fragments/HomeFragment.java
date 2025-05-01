package com.example.exam_petpal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exam_petpal.R;
import com.example.exam_petpal.adapters.PetAdapter;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.AuthManager;
import com.example.exam_petpal.AddPetActivity;
import com.example.exam_petpal.PetDetailActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PetAdapter.OnPetClickListener {
    private RecyclerView petsRecyclerView;
    private PetAdapter petAdapter;
    private PetManager petManager;
    private List<Pet> allPets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Инициализация менеджера питомцев
        petManager = PetManager.getInstance(requireContext());

        // Инициализация RecyclerView
        petsRecyclerView = view.findViewById(R.id.petsRecyclerView);
        petsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allPets = petManager.getPets();
        petAdapter = new PetAdapter(allPets, this);
        petsRecyclerView.setAdapter(petAdapter);

        // Инициализация FAB
        ExtendedFloatingActionButton addPetFab = view.findViewById(R.id.addPetFab);
        // Ограничение для незарегистрированного пользователя
        if (!AuthManager.isLoggedIn(requireContext())) {
            if (!petManager.canAddTrialPet()) {
                addPetFab.setVisibility(View.GONE);
            } else {
                addPetFab.setVisibility(View.VISIBLE);
            }
        } else {
            addPetFab.setVisibility(View.VISIBLE);
        }

        // Обработка нажатия на FAB
        addPetFab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddPetActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePetsList();
    }

    @Override
    public void onPetClick(Pet pet) {
        try {
            Intent intent = new Intent(getActivity(), PetDetailActivity.class);
            intent.putExtra("pet_id", pet.getId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при открытии деталей питомца", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePetsList() {
        List<Pet> pets = new ArrayList<>();
        for (Pet pet : petManager.getPets()) {
            if (!pet.isHidden()) {
                pets.add(pet);
            }
        }
        allPets = petManager.getPets();
        petAdapter.setShowArchived(false);
        petAdapter.updatePets(pets);
    }

    public void filterPets(String query) {
        List<Pet> filtered = new ArrayList<>();
        for (Pet pet : allPets) {
            if (!pet.isHidden() && pet.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(pet);
            }
        }
        petAdapter.updatePets(filtered);
    }
} 