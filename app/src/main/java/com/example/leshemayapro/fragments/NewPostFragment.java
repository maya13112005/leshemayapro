package com.example.leshemayapro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.BakingRecipe;
import com.example.leshemayapro.classes.BetterActivityResult;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.FragmentNewPostBinding;
//import com.example.leshemayapro.utilities.adapters.ChipAdapter;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment {
    public final String TAG = this.getClass().getSimpleName();
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String title, description, directions, makingTime, numberOfPeople, bakingTime, temp;
    ArrayList<String> ingredients, tags;

    private StorageReference imageReference;
    private boolean isFromCamera;
    private Bitmap matchBitmap;
    private Uri imagePath;
    private FragmentNewPostBinding binding;
    private int numberOfLines = 0;

    public NewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        populateChipGroup();
//        ArrayList<String> allTags = new ArrayList<>();
//        extractTagList(allTags);
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        adapter = new ChipAdapter(this.requireContext(), allTags);
//        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
//        binding.chipGroup.setLayoutManager(llm);
//        binding.chipGroup.setAdapter(adapter);
        setListeners();
        return binding.getRoot();
    }

    private void populateChipGroup()
    {
        String[] chipValues = getResources().getStringArray(R.array.tags);
        for (String tag : chipValues)
        {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            binding.chipGroup.addView(chip);
        }
    }

    private void setListeners()
    {
        binding.recipeImage.setOnClickListener(this::choosePhotoFromPhone);
        binding.postButton.setOnClickListener(v -> postOrDraftRecipe("post"));
        binding.draftButton.setOnClickListener(v -> postOrDraftRecipe("draft"));
        binding.requireBaking.setOnClickListener(v -> toggleBakingRecipe());
        binding.addIngredient.setOnClickListener(v -> addIngredient());
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {

            List<Integer> ids = group.getCheckedChipIds();
            for (Integer id : checkedIds){
                Chip chip = group.findViewById(id);
                Toast.makeText(requireContext(), chip.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIngredient()
    {
//        LinearLayout linearLayoutSubParent = new LinearLayout(requireContext());
//        linearLayoutSubParent.setOrientation(LinearLayout.VERTICAL);
//        linearLayoutSubParent.setWeightSum(100f); // you can also add more widget by providing weight
//
//        LinearLayout.LayoutParams linearLayoutSubParentParams =
//                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 100f);
//        linearLayoutSubParent.setLayoutParams(linearLayoutSubParentParams);
//        linearLayoutSubParent.setPadding(0, 0, 0, 0);

        // Add TextInputLayout to parent layout first
//        TextInputLayout textInputLayout = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
//        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 100f);
//        textInputLayout.setLayoutParams(textInputLayoutParams);
//        textInputLayout.setHintTextAppearance(R.style.TextSizeHint);
//
//        // Add EditText control to TextInputLayout
//        final TextInputEditText editText = new TextInputEditText(requireContext());
//        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        editTextParams.setMargins(0, 10, 0, 0);
//        editText.setLayoutParams(editTextParams);
//
//        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
//        editText.setHint("Enter value");
//        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
//        editText.setId(++numberOfLines);
//        editText.setEnabled(true);
//
//        textInputLayout.addView(editText, editTextParams);
//        binding.ingredients.addView(textInputLayout);

        Spinner ingredientSpinner = new Spinner(requireContext());
        ArrayAdapter<CharSequence> ingredientAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.ingredients, android.R.layout.simple_spinner_item);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientSpinner.setAdapter(ingredientAdapter);

        binding.ingredients.addView(ingredientSpinner);
    }

    private void toggleBakingRecipe()
    {
        if (binding.requireBaking.isChecked())
        {
            binding.text1.setVisibility(View.VISIBLE);
            binding.bakingHours.setVisibility(View.VISIBLE);
            binding.bakingMinutes.setVisibility(View.VISIBLE);
            binding.textInputLayout5.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.text1.setVisibility(View.GONE);
            binding.bakingHours.setVisibility(View.GONE);
            binding.bakingMinutes.setVisibility(View.GONE);
            binding.textInputLayout5.setVisibility(View.GONE);
        }
    }

    private void postOrDraftRecipe(String action)
    {
        extractStrings();
        if(validateInput())
        {
            Toast.makeText(requireContext(), "please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = FirebaseManager.getDataRef("/").push().getKey();
        Recipe recipe;
        if (binding.requireBaking.isChecked())
            recipe = new BakingRecipe(id, FirebaseManager.getUid(), title, description, ingredients, tags, directions, makingTime, numberOfPeople, bakingTime, temp);
        else recipe = new Recipe(id, FirebaseManager.getUid(), title, description, ingredients, tags, directions, makingTime, numberOfPeople);
        String path = "";
        if (action.equals("post"))
            path = "Recipes/" + id;
        else if (action.equals("draft"))
            path = "users/" + FirebaseManager.getUid() + "/drafts/" + id;
        imageReference = FirebaseManager.getStorageRef("Recipes/" + id);
        if (uploadRecipeImage())
        {
            DatabaseReference recipeRef = FirebaseManager.getDataRef(path);
            recipeRef.setValue(recipe);
        }
//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.fragment_container, new ExploreFragment())
//                .commit();
//        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput()
    {
        if (!binding.requireBaking.isChecked())
            return (title.isEmpty() || description.isEmpty() || ingredients.isEmpty() ||
                    directions.isEmpty() || numberOfPeople.isEmpty() || tags.isEmpty() ||
                    binding.hours.getSelectedItem().toString().equals("hours") ||
                    binding.minutes.getSelectedItem().toString().equals("minutes"));
        else return title.isEmpty() || description.isEmpty() || ingredients.isEmpty() ||
                directions.isEmpty() || numberOfPeople.isEmpty() || temp.isEmpty() ||
                binding.hours.getSelectedItem().toString().equals("hours") ||
                binding.minutes.getSelectedItem().toString().equals("minutes") ||
                binding.bakingHours.getSelectedItem().toString().equals("hours") ||
                binding.bakingMinutes.getSelectedItem().toString().equals("minutes");
    }

    private void extractStrings()
    {
        title = binding.title.getText().toString().trim();
        description = binding.description.getText().toString().trim();
//        ingredients = binding.ingredients.getText().toString().trim();
        ingredients = new ArrayList<>();
        for (int i = 0; i < binding.ingredients.getChildCount(); i++)
        {
//            TextInputLayout til = (TextInputLayout) binding.ingredients.getChildAt(i);
//            ingredients.add(til.getEditText().getText().toString().trim());
            Spinner spinner = (Spinner) binding.ingredients.getChildAt(i);
            ingredients.add(spinner.getSelectedItem().toString().trim());
        }
        tags = new ArrayList<>();
        for (Integer id : binding.chipGroup.getCheckedChipIds())
        {
            Chip chip = binding.chipGroup.findViewById(id);
            tags.add(chip.getText().toString());
        }
        Log.d(TAG, "extractStrings: ingredients: " + ingredients);
        directions = binding.directions.getText().toString().trim();
        makingTime = binding.hours.getSelectedItem().toString() + ":" + binding.minutes.getSelectedItem().toString();
        bakingTime = binding.bakingHours.getSelectedItem().toString() + ":" + binding.bakingMinutes.getSelectedItem().toString();
        numberOfPeople = binding.numberOfPeople.getText().toString().trim();
        temp = binding.temp.getText().toString().trim();
    }

    private void choosePhotoFromPhone (View view)
    {
        MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(view.getContext());
        mBuilder.setTitle("Choose Recipe Image")
                .setMessage("you can select from galley or camera")
                .setCancelable(true)
                .setPositiveButton("camera", (dialog, which) ->
                {
                    isFromCamera = true;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityLauncher.launch(cameraIntent, this::myOnActivityResult);
                })
                .setNegativeButton("gallery", (dialog, which) ->
                {
                    isFromCamera = false;
                    Intent galleryIntent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image From");
                    activityLauncher.launch(chooserIntent, this::myOnActivityResult);
                })
                .setNeutralButton("Cancel", (dialog, which) -> Log.d(TAG, "simpleAlert: canceled"))
                .create()
                .show();
    }

    private void myOnActivityResult (ActivityResult result)
    {
        Intent data = result.getData();
        int resultCode = result.getResultCode();
        Log.d(TAG, "myOnActivityResult: resultCode" + resultCode + "data" + data + "result ok" + Activity.RESULT_OK);
        if (isFromCamera && resultCode == Activity.RESULT_OK)
            matchBitmap = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
        else if (!isFromCamera && resultCode == Activity.RESULT_OK && Objects.requireNonNull(data).getData() != null)
            try
            {
                imagePath = data.getData();
                matchBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        binding.recipeImage.setImageBitmap(matchBitmap);
    }

    private boolean uploadRecipeImage ()
    {
        UploadTask uploadTask = null;
        if (isFromCamera)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            matchBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            uploadTask = imageReference.putBytes(data);
        }
        else if (imagePath == null)
            Log.e("BAD", "addDogToDB: null imagePath");
        else
            uploadTask = imageReference.putFile(imagePath);
        if (uploadTask != null)
            uploadTask
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Image upload NOT successfully", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(requireContext(), "Image upload successfully", Toast.LENGTH_SHORT).show());
        else
        {
            Toast.makeText(requireContext(), "Please upload a photo!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void extractTagList(ArrayList<String> allTags)
    {
        allTags.addAll(Arrays.asList(getResources().getStringArray(R.array.tags)));
    }
}