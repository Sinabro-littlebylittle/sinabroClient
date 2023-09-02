package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.Place;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    @PUT("/api/places/private/places/{placeId}")
    Call<Place> updatePlace(@Path("placeId") String placeId, @Body Place place);

    @DELETE("/api/places/private/places/{placeId}")
    Call<ResponseBody> deletePlace(@Path("placeId") String placeId);
}
