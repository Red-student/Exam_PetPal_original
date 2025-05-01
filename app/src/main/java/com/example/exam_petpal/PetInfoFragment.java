package com.example.exam_petpal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PetInfoFragment extends Fragment {
    private static final String ARG_PET = "pet";
    private static final int PICK_IMAGE = 102;
    private Pet pet;
    private ImageView photo;

    public static PetInfoFragment newInstance(Pet pet) {
        PetInfoFragment fragment = new PetInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PET, pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_info, container, false);
        if (getArguments() != null) {
            pet = (Pet) getArguments().getSerializable(ARG_PET);
        }
        if (pet != null) {
            TextView name = view.findViewById(R.id.petName);
            TextView type = view.findViewById(R.id.petType);
            TextView breed = view.findViewById(R.id.petBreed);
            TextView birthDate = view.findViewById(R.id.petBirthDate);
            TextView weight = view.findViewById(R.id.petWeight);
            TextView gender = view.findViewById(R.id.petGender);
            photo = view.findViewById(R.id.petPhoto);
            Button historyBtn = view.findViewById(R.id.historyBtn);
            Button archiveBtn = view.findViewById(R.id.archiveBtn);

            name.setText(pet.getName());
            type.setText(pet.getType());
            breed.setText(pet.getBreed());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            birthDate.setText(sdf.format(pet.getBirthDate()));
            weight.setText(String.valueOf(pet.getWeight()));
            gender.setText(pet.getGender());
            if (pet.getPhotoUri() != null) {
                photo.setImageURI(android.net.Uri.parse(pet.getPhotoUri()));
            } else {
                photo.setImageResource(R.drawable.ic_add_photo);
            }
            if (AuthManager.isLoggedIn(requireContext())) {
                photo.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE);
                });
                archiveBtn.setVisibility(View.VISIBLE);
                archiveBtn.setOnClickListener(v -> {
                    pet.setHidden(true);
                    PetManager.getInstance(requireContext()).addPet(pet);
                    Toast.makeText(requireContext(), "Питомец скрыт (архивирован)", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                });
            } else {
                photo.setOnClickListener(null);
                archiveBtn.setVisibility(View.GONE);
            }
            historyBtn.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup)getView().getParent()).getId(), PetHistoryFragment.newInstance(pet))
                    .addToBackStack(null)
                    .commit();
            });
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && pet != null) {
            Uri selectedPhotoUri = data.getData();
            if (selectedPhotoUri != null) {
                photo.setImageURI(selectedPhotoUri);
                // Сохраняем фото через PetManager
                String photoPath = PetManager.getInstance(requireContext()).savePetPhoto(requireContext(), selectedPhotoUri);
                pet.setPhotoUri(photoPath);
                PetManager.getInstance(requireContext()).addPet(pet); // обновляем питомца
                Toast.makeText(requireContext(), "Фото обновлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 