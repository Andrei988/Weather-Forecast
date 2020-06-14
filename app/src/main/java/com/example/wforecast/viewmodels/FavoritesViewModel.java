package com.example.wforecast.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wforecast.models.MessageList;
import com.example.wforecast.repository.FavoritesRepository;

import java.util.concurrent.ExecutionException;

public class FavoritesViewModel extends AndroidViewModel {
    private static final String TAG = "FavoritesViewModel";

    private FavoritesRepository repository;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = FavoritesRepository.getInstance(application);
    }

    public void updateFavoriteCoordinates() throws ExecutionException, InterruptedException {
        repository.updateFavCoords();
    }

    public LiveData<MessageList> getFavoriteCoordinates() {
        return repository.getMessages();
    }


    public LiveData<Boolean> isLoading() {
        return repository.getIsLoading();
    }

    public void removeItem(int pos) throws ExecutionException, InterruptedException {
        repository.removeItem(pos);
    }
}
