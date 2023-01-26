package com.project.sinabro.model;

public class Places {
    private Long place_id;
    private String place_name;
    private String address;
    private Double latitude;
    private Double longitude;

    public Long getPlace_id() {
        return place_id;
    }

    public void setPlace_id(Long place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Places{" + "place_id=" + place_id + ", place_name='" + place_name + '\'' + ", address='" + address + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }
}
