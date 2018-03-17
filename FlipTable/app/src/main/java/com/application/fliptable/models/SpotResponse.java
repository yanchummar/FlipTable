package com.application.fliptable.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yanchummar on 1/14/18.
 */

public class SpotResponse {
    @SerializedName("time")
    private String time;
    @SerializedName("spots")
    private ArrayList<Spot> spotList;

    public String getTime() {
        return time;
    }

    public ArrayList<Spot> getSpotList() {
        return spotList;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSpotList(ArrayList<Spot> spotList) {
        this.spotList = spotList;
    }
}
