package com.project.sinabro.sideBarMenu.bookmark;

public class ListItem {
    private int listIconColorValue;
    private String listName;

    public ListItem(int listIconColorValue, String listName) {
        this.listIconColorValue = listIconColorValue;
        this.listName = listName;
    }

    public int getListIconColorValue() {
        return listIconColorValue;
    }

    public void setListIconColorValue(int listColorValue) {
        this.listIconColorValue = listColorValue;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

}

