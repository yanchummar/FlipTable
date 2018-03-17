package com.application.fliptable.business.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yanchummar on 1/15/18.
 */

public class InternetTimeResponse {
    @SerializedName("time")
    private String time;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}

