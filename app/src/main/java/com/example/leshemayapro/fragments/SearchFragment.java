package com.example.leshemayapro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.BakingRecipe;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.Recipe;
import com.example.leshemayapro.databinding.FragmentNewPostBinding;
import com.example.leshemayapro.databinding.FragmentSearchBinding;
import com.example.leshemayapro.utilities.adapters.RecipeListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentSearchBinding binding;
    private RecipeListAdapter adapter;

    private ArrayList<Recipe> allRecipes;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        handleAdapter();
        setListeners();
        return binding.getRoot();
    }

    private void setListeners()
    {
        binding.searchButton.setOnClickListener(v -> search());
    }

    private void search()
    {
        String searchTerm = binding.search.getText().toString().trim();
        extractResultList(searchTerm);
    }

    private void handleAdapter() {
        allRecipes = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter = new RecipeListAdapter(this.requireContext(), allRecipes);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.resultList.setLayoutManager(llm);
        binding.resultList.setAdapter(adapter);
    }

    private void extractResultList(String searchTerm)
    {
        allRecipes.clear();
        // fetch data from firebase according to ingredients or tags.
        DatabaseReference myReference = FirebaseManager.getDataRef("Recipes/");
        myReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot recipeSnapshot : snapshot.getChildren())
                {
                    Recipe r = recipeSnapshot.getValue(BakingRecipe.class);
                    for (String ingredient: r.getIngredients())
                    {
                        if (ingredient.equalsIgnoreCase(searchTerm))
                        {
                            allRecipes.add(r);
                        }
                    }
                    for (String tag: r.getTags())
                    {
                        if (tag.equalsIgnoreCase(searchTerm))
                        {
                            allRecipes.add(r);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}