package com.example.wforecast.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
public class Day {
    @SerializedName("dt")
    private long dt;
    @SerializedName("sunrise")
    private long sunrise;
    @SerializedName("sunset")
    private long sunset;
    @SerializedName("temp")
    private Temp temp;
    @SerializedName("feels_like")
    private Feels_like feels_like;
    @SerializedName("pressure")
    private int pressure;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("dew_point")
    private double dew_point;
    @SerializedName("wind_speed")
    private double wind_speed;
    @SerializedName("wind_deg")
    private int wind_deg;
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("clouds")
    private int clouds;
    @SerializedName("uvi")
    private double uvi;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private class Temp {
        @SerializedName("day")
        private double day;
        @SerializedName("min")
        private double min;
        @SerializedName("max")
        private double max;
        @SerializedName("night")
        private double night;
        @SerializedName("eve")
        private double eve;
        @SerializedName("morn")
        private double morn;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private class Feels_like {
        @SerializedName("day")
        private double day;
        @SerializedName("night")
        private double night;
        @SerializedName("eve")
        private double eve;
        @SerializedName("morn")
        private double morn;
    }
}


