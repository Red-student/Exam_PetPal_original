package com.example.exam_petpal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exam_petpal.AddPetActivity;
import com.example.exam_petpal.AddVaccineActivity;
import com.example.exam_petpal.PetDetailActivity;
import com.example.exam_petpal.R;
import com.example.exam_petpal.adapters.PetAdapter;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements PetAdapter.OnPetClickListener {
    private RecyclerView petsRecyclerView;
    private PetAdapter petAdapter;
    private PetManager petManager;
    private FloatingActionButton addPetFab;
    private SwitchMaterial visibilitySwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets, container, false);
        
        petManager = PetManager.getInstance(requireContext());
        
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        petsRecyclerView = view.findViewById(R.id.petsRecyclerView);
        addPetFab = view.findViewById(R.id.addPetFab);
        visibilitySwitch = view.findViewById(R.id.visibilitySwitch);
    }

    private void setupRecyclerView() {
        petAdapter = new PetAdapter(requireContext(), petManager.getPets(), this);
        petsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        petsRecyclerView.setAdapter(petAdapter);
    }

    private void setupListeners() {
        addPetFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddPetActivity.class);
            startActivity(intent);
        });

        visibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            petAdapter.setShowArchived(isChecked);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        petAdapter.updatePets(petManager.getPets());
    }

    @Override
    public void onPetClick(Pet pet) {
        Intent intent = new Intent(requireContext(), PetDetailActivity.class);
        intent.putExtra("pet_id", pet.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Pet pet) {
        Intent intent = new Intent(requireContext(), AddPetActivity.class);
        intent.putExtra("pet_id", pet.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Pet pet) {
        petManager.deletePet(pet.getId());
        petAdapter.updatePets(petManager.getPets());
        Toast.makeText(requireContext(), "Питомец удален", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddVaccineClick(Pet pet) {
        Intent intent = new Intent(requireContext(), AddVaccineActivity.class);
        intent.putExtra("pet_id", pet.getId());
        startActivity(intent);
    }

    public void filterPets(String query) {
        if (query == null || query.isEmpty()) {
            petAdapter.updatePets(petManager.getPets());
            return;
        }

        List<Pet> filteredPets = petManager.getPets().stream()
            .filter(pet -> pet.getName().toLowerCase().contains(query.toLowerCase()) ||
                         pet.getType().toLowerCase().contains(query.toLowerCase()) ||
                         pet.getBreed().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());

        petAdapter.updatePets(filteredPets);
    }
} 