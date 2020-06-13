package com.example.wforecast.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.wforecast.Models.CityData;
import com.example.wforecast.Models.FavoriteCoordinates;
import com.example.wforecast.Models.MessageList;
import com.example.wforecast.WebAPI.API;
import com.example.wforecast.WebAPI.ApiConsumer;
import com.example.wforecast.repository.Database.FavoritesDao;
import com.example.wforecast.repository.Database.FavoritesDatabase;
import com.example.wforecast.utils.Common;

import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@Getter
public class FavoritesRepository {

    private static final String TAG = "FavoritesRepository";

    public static FavoritesRepository instance;
    private FavoritesDao dao;
    private FavoritesDatabase database;
    private MutableLiveData<MessageList> messages;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<CityData> cityInfo;

    public static synchronized FavoritesRepository getInstance(Application application) {
        if (instance == null) {
            instance = new FavoritesRepository(application);
        }
        return instance;
    }

    private FavoritesRepository(Application application) {
        database = FavoritesDatabase.getInstance(application);
        dao = database.getDao();
        messages = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        cityInfo = new MutableLiveData<>();
    }

    public void updateFavCoords() throws ExecutionException, InterruptedException { // called when user enters the app in order to get a list of fav cities
        isLoading.setValue(true);
        Retrofit retrofit = ApiConsumer.getInstance().getRetrofitClient();
        API api = retrofit.create(API.class);

        List<FavoriteCoordinates> coords = getAllFavoriteCoordinates();

        String finalIds = "";

        for (FavoriteCoordinates coord : coords) {
            finalIds += coord.getCityId() + ",";
        }

        finalIds = removeLastCharacter(finalIds);

        final Call<MessageList> call = api.getWeathersById(finalIds, Common.API_KEY_2, Common.UNITS);

        call.enqueue(new Callback<MessageList>() {
            @Override
            public void onResponse(Call<MessageList> call, Response<MessageList> response) {
                System.out.println(response.body());
                messages.postValue(response.body());
            }

            @Override
            public void onFailure(Call<MessageList> call, Throwable t) {
                Log.e(TAG, "onFailure: failed");
                t.printStackTrace();
            }
        });

        isLoading.postValue(false);
    }

    public void postCityInfo(FavoriteCoordinates favoriteCoordinates) throws ExecutionException, InterruptedException { // called when user adds a new fav city
        Retrofit retrofit = ApiConsumer.getInstance().getRetrofitClient();
        API api = retrofit.create(API.class);

        final Call<CityData> call = api.getCityInfo(favoriteCoordinates.getLat(), favoriteCoordinates.getLon(), Common.API_KEY_2, Common.UNITS);

        call.enqueue(new Callback<CityData>() {
            @Override
            public void onResponse(Call<CityData> call, Response<CityData> response) {
                System.out.println(response.body());
                cityInfo.postValue(response.body());
            }

            @Override
            public void onFailure(Call<CityData> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
            }
        });
        updateFavCoords();
    }

    public void insertCoordinate(FavoriteCoordinates favoriteCoordinates) {
        new InsertCoordinateAsync(dao).execute(favoriteCoordinates);
    }

    private List<FavoriteCoordinates> getAllFavoriteCoordinates() throws ExecutionException, InterruptedException {
        return new GetAllFavCords(dao).execute().get();
    }

    public void removeItem(int pos) throws ExecutionException, InterruptedException {
        List<FavoriteCoordinates> list = getAllFavoriteCoordinates();
        FavoriteCoordinates temp = list.get(pos);
        new RemoveItemAsync(dao).execute(temp);
    }

    private String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }

    // class space

    public static class RemoveItemAsync extends AsyncTask<FavoriteCoordinates, Void, Void> {
        private FavoritesDao dao;

        private RemoveItemAsync(FavoritesDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(FavoriteCoordinates... favoriteCoordinates) {
            dao.removeItem(favoriteCoordinates[0]);
            return null;
        }
    }

    public static class GetAllFavCords extends AsyncTask<Void, Void, List<FavoriteCoordinates>> {

        private FavoritesDao dao;

        private GetAllFavCords(FavoritesDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<FavoriteCoordinates> doInBackground(Void... voids) {
            return dao.getAllFavoriteCoordinates();
        }
    }

    public static class InsertCoordinateAsync extends AsyncTask<FavoriteCoordinates, Void, Void> {

        private FavoritesDao dao;

        private InsertCoordinateAsync(FavoritesDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(FavoriteCoordinates... favoriteCoordinates) {
            dao.insert(favoriteCoordinates[0]);
            return null;
        }
    }

}
