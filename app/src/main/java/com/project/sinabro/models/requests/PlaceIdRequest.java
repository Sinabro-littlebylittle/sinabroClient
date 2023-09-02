package com.project.sinabro.models.requests;

public class PlaceIdRequest {
    private String placeId;

    public PlaceIdRequest(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
