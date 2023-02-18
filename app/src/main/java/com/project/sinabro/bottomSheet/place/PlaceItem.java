package com.project.sinabro.bottomSheet.place;

// 리스트뷰 내 등록된 장소 관련 정보를 담기 위한 클래스
public class PlaceItem {
    private String placeName;
    private String detailAddress;
    private int peopleCnt;

    public PlaceItem(String placeName, String detailAddress, int peopleCnt) {
        this.placeName = placeName;
        this.detailAddress = detailAddress;
        this.peopleCnt = peopleCnt;
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
}
