package com.project.sinabro.bottomSheet.place;

// 리스트뷰 내 등록된 장소 관련 정보를 담기 위한 클래스
public class PlaceItem {
    private String address;
    private String placeName;
    private String detailAddress;
    private int peopleCnt;
    private long updateElapsedTime;
    private String placeId;

    public PlaceItem(String address, String placeName, String detailAddress, int peopleCnt, long updateElapsedTime, String placeId) {
        this.address = address;
        this.placeName = placeName;
        this.detailAddress = detailAddress;
        this.peopleCnt = peopleCnt;
        this.updateElapsedTime = updateElapsedTime;
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public int getPeopleCnt() {
        return peopleCnt;
    }

    public void setPeopleCnt(int peopleCnt) {
        this.peopleCnt = peopleCnt;
    }

    public long getUpdateElapsedTime() {
        return updateElapsedTime;
    }

    public void setUpdateElapsedTime(int peopleCnt) {
        this.updateElapsedTime = updateElapsedTime;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int peopleCnt) {
        this.placeId = placeId;
    }
}
