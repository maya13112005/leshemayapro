package com.example.leshemayapro.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.leshemayapro.R;
import com.example.leshemayapro.databinding.ActivityLoginBinding;
import com.example.leshemayapro.databinding.ActivityRecipeBinding;

import java.util.Objects;

public class RecipeActivity extends AppCompatActivity
{
    private ActivityRecipeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        binding = ActivityRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        setListeners();
    }

    private void setListeners()
    {
        binding.saveOrDeleteButton.setOnClickListener(v -> saveOrDelete());
    }

    private void saveOrDelete()
    {

    }
}