package com.example.wforecast.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.wforecast.models.Message;
import com.example.wforecast.repository.CurrentLocationRepository;

public class CurrentLocationViewModel extends ViewModel {
    private static final String TAG = "CurrentLocationViewMode";

    private CurrentLocationRepository repository;

    public CurrentLocationViewModel() {
        repository = CurrentLocationRepository.getInstance();
    }

    public void updateHourlyForecast() {
        repository.updateHourlyForecast();
    }

    public LiveData<Boolean> isLoading() {
        return repository.isLoading();
    }

    public LiveData<Message> getHourlyForecast() {
        return repository.getHourlyForecast();
    }
}