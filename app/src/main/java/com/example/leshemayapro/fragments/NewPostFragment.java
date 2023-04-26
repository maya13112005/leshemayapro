package com.example.leshemayapro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.BetterActivityResult;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.FragmentExploreBinding;
import com.example.leshemayapro.databinding.FragmentNewPostBinding;
import com.example.leshemayapro.databinding.FragmentProfileBinding;
import com.example.leshemayapro.utilities.adapters.ChipAdapter;
import com.example.leshemayapro.utilities.adapters.RecipeListAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    private StorageReference imageReference;
    private boolean isFromCamera;
    private Bitmap matchBitmap;
    private Uri imagePath;
    private FragmentNewPostBinding binding;
    private ChipAdapter adapter;

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
//        ArrayList<String> allTags = new ArrayList<>();
//        extractTagList(allTags);
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        adapter = new ChipAdapter(this.requireContext(), allTags);
//        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
//        binding.chipGroup.setLayoutManager(llm);
//        binding.chipGroup.setAdapter(adapter);
        setListeners();

//        binding.linearLayout.setOnEditorActionListener((v, actionId, event) -> {
//
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//                CheckBox c =new CheckBox(NewPostFragment.this);
//
//                addView(checkBox)
//                //CREATIN A EDITTEXT
//                return true;
//            }
//            return false;
//        });
        return binding.getRoot();
    }

    private void setListeners()
    {
        binding.recipeImage.setOnClickListener(this::choosePhotoFromPhone);
        binding.postButton.setOnClickListener(v -> postRecipe());
    }

    private void postRecipe()
    {
        if(validateInput())
        {
            Toast.makeText(requireContext(), "please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = binding.title.getText().toString().trim(),
                description = binding.description.getText().toString().trim(),
                ingredients = binding.ingredients.getText().toString().trim(),
                directions = binding.directions.getText().toString().trim(),
                makingTime = binding.makingTime.getText().toString().trim(),
                numberOfPeople = binding.title.getText().toString().trim();
        String id = FirebaseManager.getDatabase().getReference().push().getKey();
        Recipe recipe = new Recipe(id, FirebaseManager.getUid(), title, description, ingredients, directions, makingTime, numberOfPeople);
        DatabaseReference recipeRef = FirebaseManager.getDataRef("Recipes/" + id);
        recipeRef.setValue(recipe);
        imageReference = FirebaseManager.getStorageRef("Recipes/" + id);
        uploadRecipeImage();
        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput()
    {
        return (binding.title.getText().toString().isEmpty() ||
                binding.description.getText().toString().isEmpty() ||
                binding.ingredients.getText().toString().isEmpty() ||
                binding.directions.getText().toString().isEmpty()||
                binding.makingTime.getText().toString().isEmpty() ||
                binding.title.getText().toString().isEmpty());
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

    private void uploadRecipeImage ()
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
            Toast.makeText(requireContext(), "Please upload a photo!", Toast.LENGTH_SHORT).show();
    }

    private void extractTagList(ArrayList<String> allTags) {
        allTags.addAll(Arrays.asList(getResources().getStringArray(R.array.tags)));


    }


}