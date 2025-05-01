package com.example.exam_petpal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.exam_petpal.models.Pet;

public class PetDetailPagerAdapter extends FragmentStateAdapter {
    private final Pet pet;

    public PetDetailPagerAdapter(@NonNull FragmentActivity fa, Pet pet) {
        super(fa);
        this.pet = pet;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return PetInfoFragment.newInstance(pet);
        if (position == 1) return PetVaccinesFragment.newInstance(pet);
        return PetEventsFragment.newInstance(pet);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
} 