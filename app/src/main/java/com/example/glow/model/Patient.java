package com.example.glow.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;


public class Patient implements Parcelable {

private String active;
    private String address;
    private String age;
    private String imageUrl;
    private String userName;
    private String userId;
    private String phone;

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    private String done;
    private String check;

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Patient(){}
    public Patient(String active, String address, String age, String imageUrl, String userName, String userId,String phone) {
        this.active = active;
        this.address = address;
        this.age = age;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userId = userId;
        this.phone = phone;
    }

    protected Patient(Parcel in) {
        active = in.readString();
        address = in.readString();
        age = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
        userId = in.readString();
        phone = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(active);
        dest.writeString(address);
        dest.writeString(age);
        dest.writeString(imageUrl);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(phone);
    }
}
