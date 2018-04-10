package com.example.administrator.myone;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Share {
    private String shareArticle;
    private int shareImageId;

    public Share(String shareArticle, int shareImageId){
        this.shareImageId =shareImageId;
        this.shareArticle = shareArticle;
    }

    public String getShareArticle(){
        return shareArticle;
    }

    public int getShareImageId(){
        return shareImageId;
    }

}
