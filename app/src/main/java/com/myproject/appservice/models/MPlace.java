package com.myproject.appservice.models;

public class MPlace {

    private String uid;
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private Double latitude, longitude;
    private boolean autoLocalice;

    public MPlace(String uid, String street, String number, String postalCode, String city) {
        this.uid = uid;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
    }

    public MPlace(String street, String number, String postalCode, String city) {
        this.uid = uid;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isAutoLocalice() {
        return autoLocalice;
    }

    public void setAutoLocalice(boolean autoLocalice) {
        this.autoLocalice = autoLocalice;
    }

    @Override
    public String toString() {
        return street+", "+number+", "+postalCode+", "+city;
    }
}

