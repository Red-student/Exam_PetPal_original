package com.example.exam_petpal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exam_petpal.R;
import com.example.exam_petpal.models.Pet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> pets;
    private OnPetClickListener listener;
    private boolean showArchived = false;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetAdapter(List<Pet> pets, OnPetClickListener listener) {
        this.pets = pets;
        this.listener = listener;
    }

    public void setShowArchived(boolean showArchived) { this.showArchived = showArchived; }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pet, parent, false);
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
            holder.itemView.setOnClickListener(v -> {
                listener.onPetClick(pet);
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (showArchived && pet.isHidden()) {
                    pet.setHidden(false);
                    notifyItemChanged(position);
                    Toast.makeText(holder.itemView.getContext(), "Питомец восстановлен", Toast.LENGTH_SHORT).show();
                } else if (!showArchived) {
                    pet.setHidden(true);
                    notifyItemChanged(position);
                    Toast.makeText(holder.itemView.getContext(), "Питомец скрыт", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
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
        private ImageView petImage;
        private TextView petName;
        private TextView petInfo;
        private TextView petAge;

        PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.petImage);
            petName = itemView.findViewById(R.id.petName);
            petInfo = itemView.findViewById(R.id.petInfo);
            petAge = itemView.findViewById(R.id.petAge);
        }

        void bind(Pet pet) {
            petName.setText(pet.getName());
            petInfo.setText(String.format("%s, %s", pet.getType(), pet.getBreed()));
            petAge.setText(calculateAge(pet.getBirthDate()));

            if (pet.getPhotoUri() != null) {
                petImage.setImageURI(android.net.Uri.parse(pet.getPhotoUri()));
            } else {
                petImage.setImageResource(R.drawable.ic_add_photo);
            }
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