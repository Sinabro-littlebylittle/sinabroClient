package com.project.sinabro.retrofit;

import com.project.sinabro.models.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlacesAPI {
    @GET("/api/places")
    Call<List<Place>> getPlaces();

    @POST("/api/places/private")
    Call<Place> addPlaceInformation(@Body Place place);

    @PUT("/api/places/private/{id}")
    Call<Place> updatePlace(@Path("id") String id, @Body Place place);

    @DELETE("/api/places/private/{id}")
    Call<Integer> deletePlace(@Path("id") String id);
}
