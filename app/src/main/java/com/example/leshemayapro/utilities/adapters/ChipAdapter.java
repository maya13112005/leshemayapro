package com.example.leshemayapro.utilities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.ChipLayoutBinding;
import com.example.leshemayapro.databinding.RecipeCardViewBinding;

import java.util.ArrayList;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<String> tags;

    public ChipAdapter(@NonNull Context context, ArrayList<String> tagList) {
        this.context = context;
        this.tags = tagList;
    }

    @NonNull
    @Override
    public ChipAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChipLayoutBinding binding = ChipLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new ChipAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipAdapter.MyViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.binding.chip.setText(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ChipLayoutBinding binding;

        public MyViewHolder(ChipLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
