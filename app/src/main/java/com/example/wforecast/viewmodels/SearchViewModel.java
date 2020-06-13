package com.example.wforecast.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wforecast.Models.CityData;
import com.example.wforecast.Models.FavoriteCoordinates;
import com.example.wforecast.repository.FavoritesRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;

public class SearchViewModel extends AndroidViewModel {
    private static final String TAG = "SearchViewModel";

    private FavoritesRepository repository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = FavoritesRepository.getInstance(application);
    }

    public LiveData<CityData> getCityInfo() {
        return repository.getCityInfo();
    }

    public void updateCityInfo(LatLng latLng) throws ExecutionException, InterruptedException {
        repository.postCityInfo(new FavoriteCoordinates(latLng.latitude, latLng.longitude));
    }

    public void addFavoriteCoordinate(int cityId, LatLng latLng) {
        repository.insertCoordinate(new FavoriteCoordinates(cityId, latLng.latitude, latLng.longitude));
    }

}
