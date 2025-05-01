package com.example.exam_petpal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exam_petpal.R;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> pets;
    private Context context;
    private OnPetClickListener listener;
    private PetManager petManager;
    private boolean showArchived = false;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
        void onEditClick(Pet pet);
        void onDeleteClick(Pet pet);
        void onAddVaccineClick(Pet pet);
    }

    public PetAdapter(Context context, List<Pet> pets, OnPetClickListener listener) {
        this.context = context;
        this.pets = pets;
        this.listener = listener;
        this.petManager = PetManager.getInstance(context);
    }

    public void setShowArchived(boolean showArchived) {
        this.showArchived = showArchived;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        if (!showArchived && pet.isHidden()) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.bind(pet);
        }
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public void updatePets(List<Pet> newPets) {
        this.pets = newPets;
        notifyDataSetChanged();
    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        private ImageView petPhoto;
        private TextView petName;
        private TextView petInfo;
        private ImageButton hideButton;
        private MaterialButton editButton;
        private MaterialButton deleteButton;
        private MaterialButton addVaccineButton;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petPhoto = itemView.findViewById(R.id.petPhoto);
            petName = itemView.findViewById(R.id.petName);
            petInfo = itemView.findViewById(R.id.petInfo);
            hideButton = itemView.findViewById(R.id.hideButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addVaccineButton = itemView.findViewById(R.id.addVaccineButton);
        }

        public void bind(Pet pet) {
            petName.setText(pet.getName());
            petInfo.setText(String.format("%s • %s • %s", pet.getType(), pet.getBreed(), pet.getGender()));

            if (pet.getPhotoUri() != null) {
                Glide.with(context)
                    .load(pet.getPhotoUri())
                    .placeholder(R.drawable.ic_paw)
                    .error(R.drawable.ic_paw)
                    .centerCrop()
                    .into(petPhoto);
            } else {
                petPhoto.setImageResource(R.drawable.ic_paw);
            }

            hideButton.setOnClickListener(v -> {
                pet.setHidden(!pet.isHidden());
                petManager.updatePet(pet);
                Toast.makeText(context, 
                    pet.isHidden() ? "Питомец скрыт" : "Питомец восстановлен", 
                    Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(pet);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(pet);
                }
            });

            addVaccineButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddVaccineClick(pet);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPetClick(pet);
                }
            });
        }

        private String calculateAge(Date birthDate) {
            if (birthDate == null) return "";

            Calendar today = Calendar.getInstance();
            Calendar birth = Calendar.getInstance();
            birth.setTime(birthDate);

            int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            int months = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH);

            if (today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
                months--;
            }

            if (years > 0) {
                return years + " лет";
            } else if (months > 0) {
                return months + " месяцев";
            } else {
                long days = TimeUnit.MILLISECONDS.toDays(
                    today.getTimeInMillis() - birth.getTimeInMillis()
                );
                return days + " дней";
            }
        }
    }
} 