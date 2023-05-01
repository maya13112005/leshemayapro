package com.example.leshemayapro.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.leshemayapro.R;
import com.example.leshemayapro.databinding.ActivityMainBinding;
import com.example.leshemayapro.fragments.ExploreFragment;
import com.example.leshemayapro.fragments.NewPostFragment;
import com.example.leshemayapro.fragments.ProfileFragment;
import com.example.leshemayapro.fragments.SearchFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Dialog d;
    private FragmentManager fragmentManager;
    private ActivityMainBinding binding;
    private ActionBar actionBar;
    Intent intent;

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
    @Override

    public boolean onCreateOptionsMenu(Menu menu) //יצירת תפריט וקישורו ל-xml
    {

        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == R.id.action_aboutProgrammer)
        {
            createAboutProgrammerDialog();

        }
        else if (id == R.id.action_aboutProgram)
        {
            createAboutProgramDialog();
        }
        else if (id == R.id.action_exit)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Going back")
                    .setMessage("are you sure you want to return?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishActivity();
                        }
                    })
                    .setNegativeButton("no",null)
                    .show();
            return true;
        }
        return false;
    }
    private void finishActivity() {
        finishAndRemoveTask();
        finishAffinity();
    }

    public void createAboutProgrammerDialog()//יצירת דיאלוג התחברות
    {
        d= new Dialog(this);
        d.setContentView(R.layout.activity_aboutprogrammer);
        d.setTitle("Login");
        d.setCancelable(true);
        d.show();

    }
    public void createAboutProgramDialog()//יצירת דיאלוג התחברות
    {
        d= new Dialog(this);
        d.setContentView(R.layout.activity_aboutprogram);
        d.setTitle("Login");
        d.setCancelable(true);
        d.show();

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