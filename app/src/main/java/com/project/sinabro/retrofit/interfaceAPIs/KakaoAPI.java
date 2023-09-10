package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.CoordinateToAddress;
import com.project.sinabro.models.SearchKeyword;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KakaoAPI {
    @GET("v2/local/geo/coord2address.json")
    Call<CoordinateToAddress> getAddress(@Query("input_coord") String input_coord, @Query("x") double x, @Query("y") double y);

    @GET("v2/local/search/keyword.json")
    Call<SearchKeyword> searchByKeyword(
            @Query("query") String query
    );
}
