package com.application.fliptable.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import static com.application.fliptable.utils.RestaurantUtils.round;

/**
 * Created by yanchummar on 12/21/17.
 */

public class Spot implements Parcelable{
    @SerializedName("city")
    private String city;
    @SerializedName("spotId")
    private String spotId;
    @SerializedName("trending")
    private Boolean isTrending;
    @SerializedName("name")
    private String spotName;
    @SerializedName("image")
    private String spotImageUrl;
    @SerializedName("rating")
    private String spotRating;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("location")
    private String spotLocation;
    @SerializedName("cuisines")
    private String cuisines;
    @SerializedName("priceLevel")
    private int priceLevel;
    @SerializedName("cost")
    private String cost;
    @SerializedName("openStatus")
    private Boolean openStatus;
    @SerializedName("openingTime")
    private String openingTime;
    @SerializedName("closingTime")
    private String closingTime;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("imageList")
    private ArrayList photosList;
    @SerializedName("amenities")
    private ArrayList amenitiesList;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("distance")
    private String distance;

    public Spot(){
    }

    public Spot(String spotName, String spotImageUrl, String spotRating, String spotLocation, String cuisines, int priceLevel, String cost, Boolean openStatus, String openingTime, String closingTime, ArrayList photosList, Boolean isTrending){
        this.spotName = spotName;
        this.spotImageUrl = spotImageUrl;
        if (!spotRating.contains(".")){
            this.spotRating = spotRating + ".0";
        }else{
            this.spotRating = spotRating.substring(0, 3);
        }
        this.isTrending = isTrending;
        this.spotLocation = spotLocation;
        this.cuisines = cuisines;
        this.priceLevel = priceLevel;
        this.cost = cost.replace("Rs.", "₹");
        this.openStatus = openStatus;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.photosList = photosList;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public void setSpotImageUrl(String spotImageUrl) {
        this.spotImageUrl = spotImageUrl;
    }

    public void setSpotRating(String spotRating) {
        this.spotRating = spotRating;
    }

    public void setSpotLocation(String spotLocation) {
        this.spotLocation = spotLocation;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setOpenStatus(Boolean openStatus) {
        this.openStatus = openStatus;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhotosList(ArrayList<String> photosList) {
        this.photosList = photosList;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setTrending(Boolean trending) {
        isTrending = trending;
    }

    public void setAmenitiesList(ArrayList amenitiesList) {
        this.amenitiesList = amenitiesList;
    }

    public String getCity() {
        return city;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getDistance() {
        double distanceInKm = Double.valueOf(distance) * 1.60934;
        String unit = " km ";
        if (distanceInKm <= 0.1){
            distanceInKm = distanceInKm * 1000;
            unit = " m ";
            if (distanceInKm < 10){
                return "You are here";
            }
        }
        return String.valueOf(round(distanceInKm, 1)).concat(unit).concat("from here");
    }

    public Boolean isTrending() {
        return isTrending;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public String getSpotImageUrl() {
        return spotImageUrl;
    }

    public String getSpotRating() {
        if (!spotRating.contains(".")){
            this.spotRating = spotRating + ".0";
        }else{
            this.spotRating = spotRating.substring(0,3);
        }
        return spotRating;
    }

    public String getSpotLocation() {
        return spotLocation;
    }

    public String getCuisines() {
        return cuisines;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public String getCost() {
        return cost.replace("Rs.","₹");
    }

    public boolean getOpenStatus() {
        return openStatus;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getPhotosList() {
        return photosList;
    }

    public ArrayList getAmenitiesList() {
        return amenitiesList;
    }

    public boolean isVerified() {
        return verified;
    }

    /** Parcelable */
    public Spot(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Creator<Spot> CREATOR = new Creator<Spot>() {
        public Spot createFromParcel(Parcel in) {
            return new Spot(in);
        }

        public Spot[] newArray(int size) {

            return new Spot[size];
        }

    };

    public void readFromParcel(Parcel in) {
        city = in.readString();
        spotId = in.readString();
        isTrending = Boolean.valueOf(in.readString());
        spotName = in.readString();
        spotImageUrl = in.readString();
        spotRating = in.readString();
        lat = in.readString();
        lng = in.readString();
        spotLocation = in.readString();
        cuisines = in.readString();
        priceLevel = in.readInt();
        cost = in.readString();
        openingTime = in.readString();
        closingTime = in.readString();
        phone = in.readString();
        address = in.readString();
        openStatus = Boolean.valueOf(in.readString());
        photosList = new ArrayList<>();
        amenitiesList = new ArrayList();
        in.readList(photosList, ClassLoader.getSystemClassLoader());
        in.readList(amenitiesList, ClassLoader.getSystemClassLoader());
        verified = Boolean.valueOf(in.readString());
        distance = in.readString();
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(spotId);
        dest.writeString(String.valueOf(isTrending));
        dest.writeString(spotName);
        dest.writeString(spotImageUrl);
        dest.writeString(spotRating);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(spotLocation);
        dest.writeString(cuisines);
        dest.writeInt(priceLevel);
        dest.writeString(cost);
        dest.writeString(openingTime);
        dest.writeString(closingTime);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(String.valueOf(openStatus));
        dest.writeList(photosList);
        dest.writeList(amenitiesList);
        dest.writeString(String.valueOf(verified));
        dest.writeString(distance);
    }

}
