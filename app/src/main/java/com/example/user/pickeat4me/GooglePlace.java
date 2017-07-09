package com.example.user.pickeat4me;

import java.io.Serializable;

/**
 * Created by user on 6/9/2017.
 */

public class GooglePlace implements Serializable {
    private String id;
    private String name;
    private String lat;
    private String lng;
    private String phoneNo;
    private String address;
    private String website;
    private String rating;
    private String opened;
    private String priceLevel;
    private String photoReference;
    private String[] weekdayText;

    public GooglePlace(){

    }

    public GooglePlace(String i, String n, String latitude, String longitude, String a){
        id = i;
        name = n;
        lat = latitude;
        lng = longitude;
        address = a;
    }

    public String getPlaceID (){
        return id;
    }

    public void setPlaceID(String i){id = i;}

    public void setPhotoReference(String p){
        photoReference = p;
    }

    public String getPhotoReference(){
        return photoReference;
    }

    public String getPlaceName(){
        return name;
    }

    public void setPlaceName(String n){name = n;}

    public double getLatitude(){
        return Double.parseDouble(lat);
    }

    public void setLat(String l) {lat = l;}

    public double getLongitude(){
        return Double.parseDouble(lng);
    }

    public void setLng(String l){lng = l;}

    public String getAddress(){
        return address;
    }

    public void setAddress(String a){address = a;}

    public void setPhoneNo(String p){
        phoneNo = p;
    }

    public String getPhoneNo(){
        if (phoneNo != null)
            return phoneNo;
        else
            return String.valueOf(R.string.notAvailable);
    }

    public void setWebsite(String w){
        website = w;
    }

    public String getWebsite (){
        if (website != null)
            return website;
        else
            return String.valueOf(R.string.notAvailable);
    }

    public void setRating(String r){
        rating = r;
    }

    public String getRating (){
        if (rating != null)
            return rating;
        else
            return String.valueOf(R.string.notAvailable);
    }

    public void setOpened(String o){
        opened = o;
    }

    public boolean getOpened (){
        if (opened != null)
            return Boolean.parseBoolean(opened);
        else
            return false;
    }

    public void setPriceLevel (String p){
        priceLevel = p;
    }

    public int getPriceLevel(){
        if (!priceLevel.equalsIgnoreCase("N/A"))
            return Integer.valueOf(priceLevel);
        else
            return -1;
    }

    public void setWeekdayText(String[] array){
        weekdayText = array;
    }

    public String[] getWeekdayText(){
        return weekdayText;
    }

}
