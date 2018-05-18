package com.example.android.theguardianfeed;

public class News {

    private String mTitle;
    private String mBody;
    private String mAuthor;

    News(String title, String body, String author){
        mTitle = title;
        mBody = body;
        mAuthor = author;
    }

    String getTitle(){
        return mTitle;
    }

    String getBody(){
        return mBody;
    }

    String getAuthor(){
        return mAuthor;
    }
}
