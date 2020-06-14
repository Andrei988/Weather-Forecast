package com.example.wforecast.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(tableName = "coordinates")
public class FavoriteCoordinates {
    @PrimaryKey(autoGenerate = true)
    int id;
    int cityId;
    double lat;
    double lon;

    public FavoriteCoordinates(int cityId, double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.cityId = cityId;
    }


    public FavoriteCoordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public FavoriteCoordinates(int position) {
        id = position;
    }
}
