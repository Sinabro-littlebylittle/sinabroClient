package com.project.sinabro.bottomSheet.place.bookmark;
//RecyclerView의 한 줄에 보여줄 데이터를 클래스
public class Dictionary {

    private String name;            // 즐겨찾기 리스트 이름
    private int ColorPicker;    // 선택한 색상
    public Dictionary(String name, int ColorPicker){
        this.name = name;
        this.ColorPicker = ColorPicker;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dictionary(String name) {
        this.name = name;
    }

    public int getColorPicker(){return ColorPicker; }

    public void setColorPicker(int colorPicker){this.ColorPicker = ColorPicker;}





}
