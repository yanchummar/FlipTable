package com.application.fliptable.business.rest;

import com.application.fliptable.business.models.LoginResponse;
import com.application.fliptable.business.models.InternetTimeResponse;
import com.application.fliptable.business.models.ReservationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yanchummar on 1/14/18.
 */

public interface ApiInterface {
    @GET("internetTime.php")
    Call<InternetTimeResponse> getInternetTime();

    @GET("getReservations.php")
    Call<ReservationResponse> getReservations(@Query("spotId") int spotId);

    @FormUrlEncoded
    @POST("updateReservation.php")
    Call<String> updateReservation(@Field("id") int id, @Field("status") String status);

    @FormUrlEncoded
    @POST("login/login.php")
    Call<LoginResponse> login(@Field("mail") String mail, @Field("password") String password);
}