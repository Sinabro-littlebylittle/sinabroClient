package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.CoordinateToAddress;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KakaoAPI {
    @GET("v2/local/geo/coord2address.json")
    Call<CoordinateToAddress> getAddress(@Query("input_coord") String input_coord, @Query("x") double x, @Query("y") double y);
}
