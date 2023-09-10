package com.project.sinabro.search;

// 리스트뷰 내 등록된 장소 관련 정보를 담기 위한 클래스
public class SearchedPlaceItem {
    private String placeName;
    private String roadAddressName;
    private String categoryName;
    private String x, y;

    public SearchedPlaceItem(String placeName, String roadAddressName, String x, String y, String categoryName, int distance) {
        this.placeName = placeName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.categoryName = categoryName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getRoadAddressName() {
        return roadAddressName;
    }

    public void setRoadAddressName(String detailAddress) {
        this.roadAddressName = detailAddress;
    }

    public String getX() { return x; }

    public void setX(String x) { this.x = x; }

    public String getY() { return y; }

    public void setY(String y) { this.y = y; }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
