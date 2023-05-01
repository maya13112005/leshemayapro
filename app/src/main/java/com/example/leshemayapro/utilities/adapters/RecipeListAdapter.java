package com.example.leshemayapro.utilities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.leshemayapro.R;
import com.example.leshemayapro.activities.RecipeActivity;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.RecipeCardViewBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipes;

    public RecipeListAdapter(@NonNull Context context, ArrayList<Recipe> recipeList) {
        this.context = context;
        this.recipes = recipeList;
    }

    private void setValues(RecipeListAdapter.MyViewHolder holder, Recipe recipe) {
        holder.binding.rating.setText(recipe.getRating());
        holder.binding.description.setText(recipe.getDescription());
        holder.binding.heading.setText(recipe.getTitle());
        holder.binding.makingTime.setText(recipe.getMakingTime());
    }

    @NonNull
    @Override
    public RecipeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout.
        RecipeCardViewBinding binding = RecipeCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.MyViewHolder holder, int position) {
        // assign values to the views in the xml file, based on the position of the recycler view.
        Recipe recipe = recipes.get(position);
        holder.binding.baseCardView.setOnClickListener(view -> {
            Intent myIntent = new Intent(context, RecipeActivity.class);
            
            this.context.startActivity(myIntent);
            // todo: to recipe page animation.
        });

        // todo: fetch image from firebase.
        StorageReference recipeImage = FirebaseManager.getStorageRef("Recipes/" + recipe.getId());
        recipeImage.getDownloadUrl().addOnSuccessListener(uri ->
                Glide
                        .with(this.context)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.binding.icon));

        setValues(holder, recipe);
    }

    @Override
    public int getItemCount() {
        // number of total items
        return recipes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from the xml file
        public RecipeCardViewBinding binding;

        public MyViewHolder(RecipeCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
