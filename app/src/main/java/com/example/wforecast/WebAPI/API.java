package com.example.wforecast.WebAPI;

import com.example.wforecast.models.CityData;
import com.example.wforecast.models.Message;
import com.example.wforecast.models.MessageList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("/data/2.5/weather")
    Call<CityData> getCityInfo(@Query("lat") double lat,
                               @Query("lon") double lon,
                               @Query("appid") String appId,
                               @Query("units") String units);

    @GET("/data/2.5/onecall")
    Call<Message> getHourlyLocalForecast(@Query("lat") double lat,
                                         @Query("lon") double lon,
                                         @Query("appid") String appId,
                                         @Query("units") String units);

    @GET("/data/2.5/group")
    Call<MessageList> getWeathersById(@Query("id") String ids,
                                      @Query("appid") String appId,
                                      @Query("units") String units);
}
