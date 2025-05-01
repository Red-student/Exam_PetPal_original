package com.example.exam_petpal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exam_petpal.AddVaccineActivity;
import com.example.exam_petpal.R;
import com.example.exam_petpal.adapters.VaccineAdapter;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VaccinesFragment extends Fragment implements VaccineAdapter.OnVaccineClickListener {
    private RecyclerView vaccinesRecyclerView;
    private VaccineAdapter vaccineAdapter;
    private PetManager petManager;
    private FloatingActionButton addVaccineFab;
    private TextView emptyView;
    private String petId;

    public static VaccinesFragment newInstance(String petId) {
        VaccinesFragment fragment = new VaccinesFragment();
        Bundle args = new Bundle();
        args.putString("pet_id", petId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccines, container, false);
        
        if (getArguments() != null) {
            petId = getArguments().getString("pet_id");
        }
        
        petManager = PetManager.getInstance(requireContext());
        
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        updateVaccinesList();
        
        return view;
    }

    private void initializeViews(View view) {
        vaccinesRecyclerView = view.findViewById(R.id.vaccinesRecyclerView);
        addVaccineFab = view.findViewById(R.id.addVaccineFab);
        emptyView = view.findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        vaccineAdapter = new VaccineAdapter(requireContext(), new ArrayList<>(), this);
        vaccinesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        vaccinesRecyclerView.setAdapter(vaccineAdapter);
    }

    private void setupListeners() {
        addVaccineFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddVaccineActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });
    }

    private void updateVaccinesList() {
        Pet pet = petManager.getPetById(petId);
        if (pet != null) {
            List<Vaccine> vaccines = petManager.getVaccinesForPet(petId);
            vaccineAdapter.updateVaccines(vaccines);
            
            if (vaccines.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                vaccinesRecyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                vaccinesRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateVaccinesList();
    }

    @Override
    public void onDeleteClick(Vaccine vaccine) {
        petManager.deleteVaccine(vaccine.getId());
        Toast.makeText(requireContext(), "Вакцина удалена", Toast.LENGTH_SHORT).show();
        updateVaccinesList();
    }
} 