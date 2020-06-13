package com.example.wforecast.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wforecast.R;
import com.example.wforecast.viewmodels.FavoritesViewModel;

import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";

    private FavoritesViewModel mViewModel;

    private RecyclerView recyclerView;
    private FavoriteCoordinatesAdapter favoriteCoordinatesAdapter;

    private ProgressBar mProgressBar;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @SneakyThrows
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        mViewModel.updateFavoriteCoordinates();
        mViewModel.isLoading().observe(this, isLoading -> {
            int visibility = isLoading ? View.VISIBLE : View.INVISIBLE;
            if (mProgressBar != null) {
                mProgressBar.setVisibility(visibility);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.rv);
        initRecyclerView();
        mProgressBar = view.findViewById(R.id.pg);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mViewModel.updateFavoriteCoordinates();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        mViewModel.getFavoriteCoordinates().observe(this.getViewLifecycleOwner(), messages -> {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            favoriteCoordinatesAdapter = new FavoriteCoordinatesAdapter(mViewModel.getFavoriteCoordinates().getValue(), getActivity());
            recyclerView.setAdapter(favoriteCoordinatesAdapter);
            favoriteCoordinatesAdapter.setOnClickListener(position -> {
                mViewModel.removeItem(position);
                Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                favoriteCoordinatesAdapter.notifyDataSetChanged();
            });
            favoriteCoordinatesAdapter.setMessages(messages);
        });
    }
}
