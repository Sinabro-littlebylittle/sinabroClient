package com.project.sinabro.models;

public class SearchHistory {

    private String _id; // MongoDB의 ObjectId 타입은 보통 Java에서 String으로 처리됩니다.
    private String searchKeyword;
    private String latitude;
    private String longitude;
    private String createdTime; // Date와 같은 타입을 사용하려면 Date 형식으로 변경해야 합니다.
    private String userId; // MongoDB의 ObjectId 타입은 보통 Java에서 String으로 처리됩니다.
    private int __v;

    // 기본 생성자
    public SearchHistory() {}

    // 모든 필드를 파라미터로 받는 생성자
    public SearchHistory(String _id, String searchKeyword, String latitude, String longitude, String createdTime, String userId, int __v) {
        this._id = _id;
        this.searchKeyword = searchKeyword;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdTime = createdTime;
        this.userId = userId;
        this.__v = __v;
    }

    // Getter 메소드들
    public String get_id() {
        return _id;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public int get__v() {
        return __v;
    }

    // Setter 메소드들
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}