package com.example.android.theguardianfeed;

public class News {

    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mDate;
    private String mUrl;

    News(String title, String section, String author, String date, String url){
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    String getTitle(){
        return mTitle;
    }

    String getSection(){
        return mSection;
    }

    String getAuthor(){
        return mAuthor;
    }

    String getDate(){
        return mDate;
    }

    String getUrl(){
        return mUrl;
    }
}
