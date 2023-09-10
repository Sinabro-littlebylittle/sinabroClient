package com.project.sinabro.search;

// 리스트뷰 내 등록된 장소 관련 정보를 담기 위한 클래스
public class SearchHistoryItem {
    private String id;
    private String searchKeyword;
    private String latitude;
    private String longitude;
    private String createdTime;

    public SearchHistoryItem(String id, String searchKeyword, String latitude, String longitude, String createdTime) {
        this.id = id;
        this.searchKeyword = searchKeyword;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getCreatedTime() { return createdTime; }

    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
}
