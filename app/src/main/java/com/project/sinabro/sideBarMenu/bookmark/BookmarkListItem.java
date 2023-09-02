package com.project.sinabro.sideBarMenu.bookmark;

import com.project.sinabro.models.Bookmark;

import java.util.List;

public class BookmarkListItem {
    private String id;
    private String userId;
    private String bookmarkName;
    private int iconColor;
    private List<String> bookmarkedPlaceId;

    public BookmarkListItem(String id, String userId, String bookmarkName, int iconColor, List<String> bookmarkedPlaceId) {
        this.id = id;
        this.userId = userId;
        this.bookmarkName = bookmarkName;
        this.iconColor = iconColor;
        this.bookmarkedPlaceId = bookmarkedPlaceId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public int getIconColor() {
        return iconColor;
    }

    public List<String> getBookmarkedPlaceId() {
        return bookmarkedPlaceId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public void setBookmarkedPlaceId(List<String> bookmarkedPlaceId) {
        this.bookmarkedPlaceId = bookmarkedPlaceId;
    }
}

