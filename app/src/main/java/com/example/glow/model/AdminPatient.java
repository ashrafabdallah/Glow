package com.example.glow.model;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AdminPatient implements Serializable  {
    private String username,imageUrl,phone;
    private List<Map<String, Object>> tasks;


    public AdminPatient(String username, String imageUrl, List<Map<String, Object>> tasks,String phone) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.tasks=tasks;
        this.phone=phone;
    }

    protected AdminPatient(Parcel in) {
        username = in.readString();
        imageUrl = in.readString();
        phone = in.readString();
    }



    public List<Map<String, Object>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Map<String, Object>> tasks) {
        this.tasks = tasks;
    }







    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }







}
