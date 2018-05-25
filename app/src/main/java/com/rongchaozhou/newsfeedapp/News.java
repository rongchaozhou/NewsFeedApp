package com.rongchaozhou.newsfeedapp;

/**
 * An News object contains information related to a single news.
 */
public class News {
    private String mTitle;
    private String mDate;
    private String mAuthor;
    private String mSection;
    private String mUrl;

    public News(String title, String date, String author, String section, String url) {
        mTitle = title;
        mDate = date;
        mAuthor = author;
        mSection = section;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }
}
