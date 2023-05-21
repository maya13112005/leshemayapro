package com.example.leshemayapro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.BakingRecipe;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.PrefManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.ActivityRecipeBinding;
import com.example.leshemayapro.utilities.services.BackgroundService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity
{
    private final String TAG = this.getClass().getSimpleName();

    private String action;
    private PrefManager prefManager;
    private Recipe recipe;
    private ActivityRecipeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        binding = ActivityRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        handleIntentValues();
        populateChipGroup();
        handleSharedPref();
        updateViews();
        setListeners();
    }

    private void populateChipGroup()
    {
        for (String tag : recipe.getTags())
        {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(false);
            binding.chipGroup.addView(chip);
        }
    }

    private void handleSharedPref()
    {
        prefManager = new PrefManager(this);
        action = prefManager.getPrefString("action indicator", "");
        switch (action)
        {
            case "explore":
                binding.saveOrDeleteButton.setText("save");
                break;
            case "profile":
                binding.saveOrDeleteButton.setText("delete");
                break;
            case "saved":
                binding.saveOrDeleteButton.setText("un-save");
                break;
            case "draft":
                binding.saveOrDeleteButton.setText("post");
        }
    }

    private void updateViews()
    {
        binding.title.setText(recipe.getTitle());
        binding.numberOfPeople.setText("no. of people: " + recipe.getNumberOfPeople());
        binding.timeOfMaking.setText("making time: " + recipe.getMakingTime());
        binding.description.setText(recipe.getDescription());
        for (String ingredient: recipe.getIngredients())
        {
            MaterialCheckBox checkBox = new MaterialCheckBox(this);
            checkBox.setText(ingredient);
            binding.ingredients.addView(checkBox);
        }
        binding.stepsOfMaking.setText(recipe.getDirections());
        if (recipe instanceof BakingRecipe )
        {
            BakingRecipe r = (BakingRecipe) recipe;
            if (r.getTime() != null && r.getTemp() != null)
            {
                binding.requireBaking.setVisibility(View.VISIBLE);
                binding.temp.setVisibility(View.VISIBLE);
                binding.clock.setVisibility(View.VISIBLE);
                binding.timerHandle.setVisibility(View.VISIBLE);
                binding.temp.setText(r.getTemp());
                binding.clock.setText(r.getTime());
            }
        }
    }

    private void handleIntentValues()
    {
        Intent myIntent = getIntent();
        recipe = myIntent.getSerializableExtra("recipe", Recipe.class);
    }

    private void setListeners()
    {
        StorageReference recipeImage = FirebaseManager.getStorageRef("Recipes/" + recipe.getId());
        recipeImage.getDownloadUrl().addOnSuccessListener(uri ->
                Glide
                        .with(this)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.recipeImage));
        binding.saveOrDeleteButton.setOnClickListener(v -> saveOrDelete());
        if (recipe instanceof BakingRecipe)
        {
            BakingRecipe r = (BakingRecipe) recipe;
            if (r.getTime() != null && r.getTemp() != null) {

                binding.timerHandle.setOnClickListener(v -> startCountdown(r));
            }
        }
    }

    /**
     * Starting/Stopping the service.
     *
     * @param r the baking recipe object.
     */
    private void startCountdown(BakingRecipe r)
    {
        if (!isMyServiceRunning()) {
            // Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            Intent serviceIntent = new Intent(this, BackgroundService.class);

            prefManager.setPref("hours plus minutes in millis", Long.parseLong(r.getTime().split(":")[0]) * 3_600_000 + Long.parseLong(r.getTime().split(":")[1]) * 60_000);
            prefManager.setPref("recipe id", r.getId());
//        serviceIntent.putExtra("hours plus minutes in millis", Long.parseLong(r.getTime().split(":")[0]) * 3_600_000 + Long.parseLong(r.getTime().split(":")[1]) * 60_000);
            serviceIntent.setAction("ACTION_START_FOREGROUND_SERVICE");
//        Context context = getApplicationContext();
//        Intent intent = new Intent(...); // Build the intent for the service
//        context.startForegroundService(intent);
//        context.startForegroundService(serviceIntent);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (BackgroundService.class.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }

    /* CountDown */
    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broadcast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG, "Unregistered broadcast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private void updateGUI(Intent intent)
    {

        if (intent.getExtras() != null && recipe.getId().equals(prefManager.getPrefString("recipe id", "")))
        {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            long seconds = (millisUntilFinished / 1000) % 60;
            long minutes = (millisUntilFinished / (1000 * 60)) % 60;
            long hours = (millisUntilFinished / (1000 * 60 * 60)) % 60;
            String time = (hours + " : " + minutes + ":" + seconds);
            binding.clock.setText(time);
        }
    }

    private void saveOrDelete()
    {
        switch (action)
        {
            case "explore":
                save();
                break;
            case "profile":
                delete();
                break;
            case "saved":
                removeFromSaved();
                break;
            case "draft":
                post();
        }
        //todo: delete
    }

    private void post()
    {
        DatabaseReference myReference = FirebaseManager.getDataRef("users/" + FirebaseManager.getUid() + "/drafts");
        myReference.removeValue();
        Toast.makeText(this, "removed!", Toast.LENGTH_SHORT).show();
        myReference = FirebaseManager.getDataRef("Recipes/" + recipe.getId());
        myReference.setValue(recipe);
        Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
    }

    private void removeFromSaved()
    {
        DatabaseReference myReference = FirebaseManager.getDataRef("users/" + FirebaseManager.getUid() + "/saved/" + recipe.getId());
        myReference.removeValue();
        Toast.makeText(this, "removed!", Toast.LENGTH_SHORT).show();
    }

    private void delete()
    {
        DatabaseReference myReference = FirebaseManager.getDataRef("Recipes/" + recipe.getId());
        myReference.removeValue();
        Toast.makeText(this, "removed!", Toast.LENGTH_SHORT).show();
    }

    private void save()
    {
        ArrayList<Recipe> saved = new ArrayList<>();
        DatabaseReference myReference = FirebaseManager.getDataRef("users/" + FirebaseManager.getUid() + "/saved");
        myReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    Recipe current = matchSnapshot.getValue(Recipe.class);
                    if (!saved.contains(current)) {
                        saved.add(current);
                    }
                    else Toast.makeText(RecipeActivity.this, "Already saved", Toast.LENGTH_SHORT).show();
                }
                if (!saved.contains(recipe))
                    saved.add(recipe);
                else Toast.makeText(RecipeActivity.this, "Already saved", Toast.LENGTH_SHORT).show();
                for (Recipe r: saved)
                {
                    myReference.child(r.getId()).setValue(r);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}