package com.application.fliptable.business.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yanchummar on 1/16/18.
 */

public class Reservation implements Parcelable {
    @SerializedName("id")
    private String reservationId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("spotId")
    private String spotId;
    @SerializedName("status")
    private String status;
    @SerializedName("spotName")
    private String spotName;
    @SerializedName("spotLocation")
    private String spotLocation;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("foodieCount")
    private int foodieCount;
    @SerializedName("bookingDate")
    private long bookingDate;
    @SerializedName("timeSlot")
    private String timeSlot;
    @SerializedName("freeBooking")
    private boolean freeBooking;
    @SerializedName("tipAmount")
    private int tipAmount;
    @SerializedName("cost")
    private int cost;
    private boolean loading;

    public Reservation(){
    }

    public Reservation(String reservationId, String name, int foodieCount, long bookingDate, String timeSlot){
        this.reservationId = reservationId;
        this.name = name;
        this.foodieCount = foodieCount;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
    }

    public String getReservationId() {
        return reservationId;
    }
    public String getUserId() {
        return userId;
    }
    public String getSpotId() {
        return spotId;
    }
    public String getStatus() {
        return status;
    }
    public String getSpotLocation() {
        return spotLocation;
    }
    public String getSpotName() {
        return spotName;
    }
    public boolean isFreeBooking() {
        return freeBooking;
    }
    public long getBookingDate() {
        return bookingDate;
    }
    public int getFoodieCount() {
        return foodieCount;
    }
    public int getTipAmount() {
        return tipAmount;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getTimeSlot() {
        return timeSlot;
    }
    public int getCost() {
        return cost;
    }
    public boolean isLoading() {
        return loading;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setSpotLocation(String spotLocation) {
        this.spotLocation = spotLocation;
    }
    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }
    public void setBookingDate(long bookingDate) {
        this.bookingDate = bookingDate;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFoodieCount(int foodieCount) {
        this.foodieCount = foodieCount;
    }
    public void setFreeBooking(boolean freeBooking) {
        this.freeBooking = freeBooking;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /** Parcelable */
    public Reservation(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        public Reservation[] newArray(int size) {

            return new Reservation[size];
        }

    };

    public void readFromParcel(Parcel in) {
        reservationId = in.readString();
        userId = in.readString();
        spotId = in.readString();
        status = in.readString();
        spotName = in.readString();
        spotLocation = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        foodieCount = in.readInt();
        bookingDate = in.readLong();
        timeSlot = in.readString();
        freeBooking = Boolean.valueOf(in.readString());
        tipAmount = in.readInt();
        cost = in.readInt();
        loading = Boolean.valueOf(in.readString());
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reservationId);
        dest.writeString(userId);
        dest.writeString(spotId);
        dest.writeString(status);
        dest.writeString(spotName);
        dest.writeString(spotLocation);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeInt(foodieCount);
        dest.writeLong(bookingDate);
        dest.writeString(timeSlot);
        dest.writeString(String.valueOf(freeBooking));
        dest.writeInt(tipAmount);
        dest.writeInt(cost);
        dest.writeString(String.valueOf(loading));
    }
}