package com.project.sinabro.models;

public class Place {

    private String id;
    private String markerId;
    private String placeName;
    private String address;
    private String detailAddress;
    private String latitude;
    private String longitude;

    // 기본 생성자
    public Place() {
    }

    // 모든 필드를 매개변수로 받는 생성자
    public Place(String id, String markerId, String placeName, String address, String detailAddress, String latitude, String longitude) {
        this.id = id;
        this.markerId = markerId;
        this.placeName = placeName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 각 필드에 대한 getter와 setter 메소드
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) { this.longitude = longitude; }
}