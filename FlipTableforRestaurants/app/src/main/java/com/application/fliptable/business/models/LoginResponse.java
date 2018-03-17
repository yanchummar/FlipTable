package com.application.fliptable.business.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yanchummar on 2/26/18.
 */

public class LoginResponse {
    @SerializedName("error")
    private int error;
    @SerializedName("message")
    private String message;
    @SerializedName("id")
    private int id;

    public int getError() {
        return error;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
