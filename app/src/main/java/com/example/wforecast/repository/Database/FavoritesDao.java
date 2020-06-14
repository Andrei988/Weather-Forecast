package com.example.wforecast.repository.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.wforecast.models.FavoriteCoordinates;

import java.util.List;


@Dao
public interface FavoritesDao {

    @Insert
    void insert(FavoriteCoordinates favoriteCoordinates);

    @Query("SELECT * FROM coordinates")
    List<FavoriteCoordinates> getAllFavoriteCoordinates();

    @Delete
    void removeItem(FavoriteCoordinates favoriteCoordinates);

    @Query("SELECT * FROM coordinates WHERE id=:id")
    FavoriteCoordinates getItem(int id);
}
