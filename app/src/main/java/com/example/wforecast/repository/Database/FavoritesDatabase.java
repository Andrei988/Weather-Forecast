package com.example.wforecast.repository.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.wforecast.Models.FavoriteCoordinates;


@Database(entities = {FavoriteCoordinates.class}, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {

    private static final String TAG = "FavoritesDatabase";

    private static FavoritesDatabase instance;

    public abstract FavoritesDao getDao();

    public static synchronized FavoritesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FavoritesDatabase.class, "coordinates")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
