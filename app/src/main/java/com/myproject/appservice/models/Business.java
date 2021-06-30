package com.myproject.appservice.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Business implements Serializable {

    private String id;
    private String idUser;
    private String nameBusiness;
    private String phone;
    private int kindBusiness;
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private String address;
    private Double longitude, latitude;
    private ArrayList<String> categories;
/*    private ArrayList<String> categories;
    private ArrayList<Schedule> schedules;
    private ArrayList<Service> services;
*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameBusiness() {
        return nameBusiness;
    }

    public void setNameBusiness(String nameBusiness) {
        this.nameBusiness = nameBusiness;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getKindBusiness() {
        return kindBusiness;
    }

    public void setKindBusiness(int kindBusiness) {
        this.kindBusiness = kindBusiness;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
