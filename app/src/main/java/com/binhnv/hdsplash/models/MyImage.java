package com.binhnv.hdsplash.models;

import android.support.v7.graphics.Palette;
import android.util.Log;

import com.binhnv.hdsplash.other.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sev_user on 1/27/2016.
 */
public class MyImage implements Serializable {


    private Link urls;
    private User user;
    private List<Category> categories;
    private String color;
    private int width;
    private int height;
    public int mLoved;
    public long modifiedDate;

    private String image_src;
    private String author;
    private float ratio;
    private int category;


    transient private Palette.Swatch swatch;

    public MyImage withLoved(int loved){
        this.mLoved = loved;
        return this;
    }

    public MyImage (String url, int width, int height, String author, int category, int loved, long modified_date){
        urls = new Link();
        urls.setFull(url);
        user = new User();
        user.setName(author);
        //this.author = author;
        this.width = width;
        this.height = height;
        this.category = category;
        this.mLoved = loved;
        this.modifiedDate = modified_date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUrl() {
        return getLinks().getFull();
    }

    public String getHighResImage(int minWidth, int minHeight) {
        String url = getLinks().getFull() + "?fm=png";

        //minimize processing costs of unsplash image hosting
        //try to eliminate the white line on top

        if (minWidth > 0 && minHeight > 0) {
            float phoneRatio = (1.0f * minWidth) / minHeight;
            if (phoneRatio < getRatio()) {
                if (minHeight < 1080) {
                    //url = url + "&h=" + minHeight;
                    url = url + "&h=" + 1080;
                }
            } else {
                if (minWidth < 1920) {
                    //url = url + "&w=" + minWidth;
                    url = url + "&w=" + 1920;
                }
            }
        }

        return url;
    }

    public Link getLinks() {
        return urls;
    }

    public void setLinks(Link urls) {
        this.urls = urls;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getImageSrc(int screenWidth) {
        return getLinks().getFull() + "?q=75&fm=jpg&w=" + Utils.optimalImageWidth(screenWidth);

        /*
        wait with this one for now. i don't want to bring up the generation quota of unsplash
        String url = image_src + "?q=75&fit=max&fm=jpg";

        if (screenWidth > 0) {
            //it's enough if we load an image with 2/3 of the size
            url = url + "&w=" + (screenWidth / 3 * 2);
        }

        return url;
        */
    }

    public void setImageSrc(String image_src) {
        this.image_src = image_src;
    }

    public String getAuthor() {
        return getUser().getName();
        //Log.d("han.hanh", author + " a");
        //return author;
    }

    public int getLoved(){
        return mLoved;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getRatio() {
        return  getWidth()/getHeight();
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Palette.Swatch getSwatch() {
        return swatch;
    }

    public void setSwatch(Palette.Swatch swatch) {
        this.swatch = swatch;
    }

    /*public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }*/

    public int getCategory() {
        //return getCategories().get(0).getId();
        return category;
    }

    public void convertCategory(){
//        this.category = 0;
        if (getCategories()==null || getCategories().size()==0){
            this.category = 1000;
            return;
        }
        switch (getCategories().get(0).getTitle()){
            case "Nature":
                this.category = 4;
                break;
            case "People":
                this.category = 8;
                break;
            case "Objects":
                this.category = 32;
                break;
            case "Buildings":
                this.category = 1;
                break;
            case "Food & Drink":
                this.category = 2;
                break;
            case "Technology":
                this.category = 16;
                break;
            default:
                this.category = 1000;
                break;
        }
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
