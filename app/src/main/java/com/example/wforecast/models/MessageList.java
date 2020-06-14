package com.example.wforecast.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
public class MessageList {

    @SerializedName("cnt")
    private int cnt;
    @SerializedName("list")
    List<CityData> list;

    public int size() {
        if (list == null) {
            list = new ArrayList<>();
            return 0;
        }
        return list.size();
    }
}
