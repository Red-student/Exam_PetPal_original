package com.example.exam_petpal.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exam_petpal.R;
import com.example.exam_petpal.models.Vaccine;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder> {
    private List<Vaccine> vaccines;
    private Context context;
    private OnVaccineClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnVaccineClickListener {
        void onDeleteClick(Vaccine vaccine);
    }

    public VaccineAdapter(Context context, List<Vaccine> vaccines, OnVaccineClickListener listener) {
        this.context = context;
        this.vaccines = vaccines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaccine, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        holder.bind(vaccines.get(position));
    }

    @Override
    public int getItemCount() {
        return vaccines.size();
    }

    public void updateVaccines(List<Vaccine> newVaccines) {
        this.vaccines = newVaccines;
        notifyDataSetChanged();
    }

    class VaccineViewHolder extends RecyclerView.ViewHolder {
        private TextView vaccineName;
        private TextView vaccineDate;
        private TextView nextVaccineDate;
        private TextView vaccineNotes;
        private ImageButton deleteButton;

        public VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            vaccineName = itemView.findViewById(R.id.vaccineName);
            vaccineDate = itemView.findViewById(R.id.vaccineDate);
            nextVaccineDate = itemView.findViewById(R.id.nextVaccineDate);
            vaccineNotes = itemView.findViewById(R.id.vaccineNotes);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Vaccine vaccine) {
            vaccineName.setText(vaccine.getName());
            vaccineDate.setText("Дата вакцинации: " + dateFormat.format(vaccine.getDate()));
            
            if (vaccine.getNextDate() != null) {
                nextVaccineDate.setVisibility(View.VISIBLE);
                nextVaccineDate.setText("Следующая вакцинация: " + 
                    dateFormat.format(vaccine.getNextDate()));
            } else {
                nextVaccineDate.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(vaccine.getNotes())) {
                vaccineNotes.setVisibility(View.VISIBLE);
                vaccineNotes.setText(vaccine.getNotes());
            } else {
                vaccineNotes.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(vaccine);
                }
            });
        }
    }
} 