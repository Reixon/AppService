package com.myproject.appservice.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Service implements Serializable {

    private String id;
    private String name;
    private String time;
    private float price;

    public Service(){

    }

    public Service(String nombre, String duracion, float precio) {
        this.name = nombre;
        this.time = duracion;
        this.price = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
