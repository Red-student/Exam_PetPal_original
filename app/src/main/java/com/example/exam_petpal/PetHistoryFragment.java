package com.example.exam_petpal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Vaccine;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PetHistoryFragment extends Fragment {
    private static final String ARG_PET = "pet";
    private Pet pet;

    public static PetHistoryFragment newInstance(Pet pet) {
        PetHistoryFragment fragment = new PetHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PET, pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_history, container, false);
        if (getArguments() != null) {
            pet = (Pet) getArguments().getSerializable(ARG_PET);
        }
        LinearLayout historyList = view.findViewById(R.id.historyList);
        if (pet != null && pet.getVaccines() != null) {
            List<Vaccine> vaccines = pet.getVaccines();
            Collections.sort(vaccines, new Comparator<Vaccine>() {
                @Override
                public int compare(Vaccine v1, Vaccine v2) {
                    return v2.getDate().compareTo(v1.getDate()); // по убыванию даты
                }
            });
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            for (Vaccine v : vaccines) {
                TextView tv = new TextView(getContext());
                String text = v.getName() + "\nДата: " + sdf.format(v.getDate());
                if (v.getExpirationDate() != null) {
                    text += "\nДействует до: " + sdf.format(v.getExpirationDate());
                }
                if (v.getDescription() != null && !v.getDescription().isEmpty()) {
                    text += "\n" + v.getDescription();
                }
                tv.setText(text);
                tv.setPadding(0, 0, 0, 32);
                historyList.addView(tv);
            }
        }
        return view;
    }
} 