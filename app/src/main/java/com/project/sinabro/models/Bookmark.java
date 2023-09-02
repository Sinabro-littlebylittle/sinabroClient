package com.project.sinabro.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bookmark {

    @SerializedName("_id")
    private String id; // Unique ID generated by MongoDB (or any identifier you use)

    @SerializedName("userId")
    private String userId; // Reference to 'user_infos'

    @SerializedName("bookmarkName")
    private String bookmarkName;

    @SerializedName("iconColor")
    private int iconColor;

    @SerializedName("bookmarkedPlaceId")
    private List<String> bookmarkedPlaceId; // List of references to 'places'

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public List<String> getBookmarkedPlaceId() {
        return bookmarkedPlaceId;
    }

    public void setBookmarkedPlaceId(List<String> bookmarkedPlaceId) {
        this.bookmarkedPlaceId = bookmarkedPlaceId;
    }
}