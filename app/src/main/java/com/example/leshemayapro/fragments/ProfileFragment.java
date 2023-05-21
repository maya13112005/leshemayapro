package com.example.leshemayapro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leshemayapro.R;
import com.example.leshemayapro.activities.LoginActivity;
import com.example.leshemayapro.classes.BakingRecipe;
import com.example.leshemayapro.classes.BetterActivityResult;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.PrefManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.FragmentExploreBinding;
import com.example.leshemayapro.databinding.FragmentProfileBinding;
import com.example.leshemayapro.utilities.adapters.RecipeListAdapter;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
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
    private PrefManager prefManager;

    private ArrayList<Recipe> allRecipes = new ArrayList<>();

    private FragmentProfileBinding binding;
    private RecipeListAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(requireContext());
        handleRecipeList();
        imageReference = FirebaseManager.getStorageRef("profile_images/" + FirebaseManager.getUid());
        initViews();
        setListeners();
        return binding.getRoot();
    }

    private void initViews()
    {
        imageReference.getDownloadUrl().addOnSuccessListener(uri ->
                Glide
                        .with(this)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.profileImage));
    }

    private void handleRecipeList() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter = new RecipeListAdapter(this.requireContext(), allRecipes);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.profileRecipeList.setLayoutManager(llm);
        binding.profileRecipeList.setAdapter(adapter);
        extractMyRecipeList();
    }

    private void setListeners()
    {
        binding.profileImage.setOnClickListener(this::choosePhotoFromPhone);
        binding.posts.setOnClickListener(v -> extractMyRecipeList());
        binding.saves.setOnClickListener(v -> fetchAllSaves());
        binding.drafts.setOnClickListener(v -> fetchAllDrafts());
        binding.signOut.setOnClickListener(this::logoutVerify);
    }

    private void fetchAllDrafts()
    {
        prefManager.setPref(PrefManager.KEY_ACTION_INDICATOR, "draft");
        allRecipes.clear();
        DatabaseReference databaseReference = FirebaseManager.getDataRef("users/" + FirebaseManager.getUid() + "/drafts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot matchSnapshot : snapshot.getChildren())
                    if(matchSnapshot.getValue(Recipe.class).getUid().equals(FirebaseManager.getUid()))
                        allRecipes.add(matchSnapshot.getValue(BakingRecipe.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {}
        });
    }

    private void fetchAllSaves()
    {
        prefManager.setPref(PrefManager.KEY_ACTION_INDICATOR, "saved");
        allRecipes.clear();
        DatabaseReference databaseReference = FirebaseManager.getDataRef("users/" + FirebaseManager.getUid() + "/saved");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot matchSnapshot : snapshot.getChildren())
                    allRecipes.add(matchSnapshot.getValue(BakingRecipe.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {}
        });
    }

    private void extractMyRecipeList() {
        // todo: link with firebase.
        prefManager.setPref(PrefManager.KEY_ACTION_INDICATOR, "profile");
        allRecipes.clear();
        DatabaseReference databaseReference = FirebaseManager.getDataRef("Recipes/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot matchSnapshot : snapshot.getChildren())
                    if(matchSnapshot.getValue(Recipe.class).getUid().equals(FirebaseManager.getUid()))
                        allRecipes.add(matchSnapshot.getValue(BakingRecipe.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {}
        });
//        Recipe r1 = new Recipe("1", "help", "blah", "vegan", "bruh", "blah", "1/5", "10 min");
//        Recipe r2 = new Recipe("2", "help", "blah", "vegan", "bruh", "blah", "1/5", "10 min");
//        Recipe r3 = new Recipe("3", "help", "blah", "vegan", "bruh", "blah", "1/5", "10 min");
//        Recipe r4 = new Recipe("4", "help", "blah", "vegan", "bruh", "blah", "1/5", "10 min");
//        Recipe r5 = new Recipe("5", "help", "blah", "vegan", "bruh", "blah", "1/5", "10 min");
//        allRecipes.add(r1);
//        allRecipes.add(r2);
//        allRecipes.add(r3);
//        allRecipes.add(r4);
//        allRecipes.add(r5);
//        adapter.notifyDataSetChanged();
    }

    private void logoutVerify (View view)
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        builder.setTitle(R.string.confirm_logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> logout())
                .setNegativeButton(R.string.cancel,(dialogInterface, i) -> {})
                .setIcon(R.drawable.ic_logout)
                .setCancelable(true)
                .show();
    }

    private void logout ()
    {
//        FacebookSdk.setClientToken(getString(R.string.facebook_app_id));
//        FacebookSdk.sdkInitialize(this.requireContext());
//        LoginManager.getInstance().logOut();
        FirebaseManager.signOut();

        Bundle result = new Bundle();
        result.putString("bundleKey", "result");
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("requestKey", result);
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        onDestroy();
    }

    private void choosePhotoFromPhone (View view)
    {
        MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(view.getContext());
        mBuilder.setTitle("Are you sure want to change profile image?")
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
        if (isFromCamera && resultCode == Activity.RESULT_OK) {
            matchBitmap = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
            uploadMatchImage();
        }
        else if (!isFromCamera && resultCode == Activity.RESULT_OK && Objects.requireNonNull(data).getData() != null) {
            try {
                imagePath = data.getData();
                matchBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagePath);
                uploadMatchImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        binding.profileImage.setImageBitmap(matchBitmap);
    }

    private void uploadMatchImage ()
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
}