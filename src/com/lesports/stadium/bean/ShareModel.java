package com.lesports.stadium.bean;


public class ShareModel {
    private String title;
    private String text;
    private String url;
    private String imageUrl;
    private String path;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void setImagePath(String path){
    	this.path = path;
    }
    
    public String getImagPath(){
    	return path;
    }

}