package com.example.leshemayapro.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.leshemayapro.R;
import com.example.leshemayapro.databinding.ActivityMainBinding;
import com.example.leshemayapro.fragments.ExploreFragment;
import com.example.leshemayapro.fragments.NewPostFragment;
import com.example.leshemayapro.fragments.ProfileFragment;
import com.example.leshemayapro.fragments.SearchFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ActivityMainBinding binding;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        actionBar = getSupportActionBar();
        binding.bottomNavigation.setOnItemSelectedListener(item ->
                handleItemSelected(item.getItemId()));

        handleItemSelected(R.id.explore);
        binding.bottomNavigation.setSelectedItemId(R.id.explore);
    }

    private boolean handleItemSelected(int itemId) {
        int titleId = R.string.explore;

        Fragment selectedFragment = new ExploreFragment();
//        if (itemId == R.id.search)
//        {
//            selectedFragment = new SearchFragment();
//            titleId = R.string.search;
//        }
        if (itemId == R.id.new_post)
        {
            selectedFragment = new NewPostFragment();
            titleId = R.string.new_post;
        }
        else if (itemId == R.id.profile)
        {
            selectedFragment = new ProfileFragment();
            titleId = R.string.profile;
        }
        else if (itemId == R.id.explore)
        {
            selectedFragment = new ExploreFragment();
            titleId = R.string.explore;
        }


        Objects.requireNonNull(actionBar).setTitle(titleId);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();
        return true;
    }
}