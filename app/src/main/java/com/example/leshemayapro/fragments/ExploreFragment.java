package com.example.leshemayapro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.FragmentExploreBinding;
import com.example.leshemayapro.utilities.adapters.RecipeListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentExploreBinding binding;
    private RecipeListAdapter adapter;

    private static final String TAG = ExploreFragment.class.getSimpleName();

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Explore.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        binding.swipeRefresh.setOnRefreshListener(this::myUpdateOperation);
        ArrayList<Recipe> allRecipes = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter = new RecipeListAdapter(this.requireContext(), allRecipes);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recipeList.setLayoutManager(llm);
        binding.recipeList.setAdapter(adapter);
        extractRecipeList(allRecipes);
        return binding.getRoot();
    }

    private void extractRecipeList(ArrayList<Recipe> allRecipes) {
        // todo: link with firebase.
        DatabaseReference databaseReference = FirebaseManager.getDataRef("Recipes/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot matchSnapshot : snapshot.getChildren())
                    allRecipes.add(matchSnapshot.getValue(Recipe.class));
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

    private void myUpdateOperation() {
        Log.i(TAG, "Performing update");
        binding.swipeRefresh.setRefreshing(false);
    }
}