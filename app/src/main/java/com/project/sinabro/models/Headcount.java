package com.project.sinabro.models;

import com.google.gson.annotations.SerializedName;

public class Headcount {

    @SerializedName("_id")
    private String id;

    @SerializedName("placeId")
    private PlaceId placeId;

    @SerializedName("headcount")
    private int headcount;

    @SerializedName("createdTime")
    private String createdTime;

    @SerializedName("updateElapsedTime")
    private int updateElapsedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlaceId getPlaceId() {
        return placeId;
    }

    public void setPlaceId(PlaceId placeId) {
        this.placeId = placeId;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getUpdateElapsedTime() {
        return updateElapsedTime;
    }

    public void setUpdateElapsedTime(int updateElapsedTime) {
        this.updateElapsedTime = updateElapsedTime;
    }

    public static class PlaceId {

        @SerializedName("_id")
        private String id;

        @SerializedName("markerId")
        private MarkerId markerId;

        @SerializedName("placeName")
        private String placeName;

        @SerializedName("address")
        private String address;

        @SerializedName("detailAddress")
        private String detailAddress;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public MarkerId getMarkerId() {
            return markerId;
        }

        public void setMarkerId(MarkerId markerId) {
            this.markerId = markerId;
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDetailAddress() {
            return detailAddress;
        }

        public void setDetailAddress(String detailAddress) {
            this.detailAddress = detailAddress;
        }

        public static class MarkerId {

            @SerializedName("_id")
            private String id;

            @SerializedName("latitude")
            private String latitude;

            @SerializedName("longitude")
            private String longitude;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }
        }
    }
}