package com.example.wforecast.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wforecast.Models.Message;
import com.example.wforecast.WebAPI.API;
import com.example.wforecast.WebAPI.ApiConsumer;
import com.example.wforecast.utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CurrentLocationRepository {

    private static final String TAG = "CurrentLocationRep";

    public static CurrentLocationRepository instance;
    private MutableLiveData<Message> hourlyForecast;
    private MutableLiveData<Boolean> isLoading;

    public CurrentLocationRepository() {
        isLoading = new MutableLiveData<>();
        hourlyForecast = new MutableLiveData<>();
    }

    public static CurrentLocationRepository getInstance() {
        if (instance == null) {
            instance = new CurrentLocationRepository();
        }
        return instance;
    }

    public void updateHourlyForecast() {
        isLoading.setValue(true);
        Retrofit retrofit = ApiConsumer.getInstance().getRetrofitClient();
        API api = retrofit.create(API.class);
        final Call<Message> call = api.getHourlyLocalForecast(Common.CURRENT_LOCATION.getLatitude(),
                                                                Common.CURRENT_LOCATION.getLongitude(),
                                                                Common.API_KEY_2, Common.UNITS);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                hourlyForecast.postValue(response.body());
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<Message> getHourlyForecast() {
        return hourlyForecast;
    }
}
