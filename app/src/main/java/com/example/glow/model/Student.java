package com.example.glow.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String imageUrl, level, phone, section, university, username;
    private float rate;

    private int numPatient;


    public Student(String imageUrl, String level, String phone, float rate, String section, String university, String username,int numPatient) {
        this.imageUrl = imageUrl;
        this.level = level;
        this.phone = phone;
        this.rate = rate;
        this.section = section;
        this.university = university;
        this.username = username;
        this.numPatient=numPatient;

    }


    protected Student(Parcel in) {
        imageUrl = in.readString();
        level = in.readString();
        phone = in.readString();
        section = in.readString();
        university = in.readString();
        username = in.readString();
        rate = in.readFloat();
        numPatient = in.readInt();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getNumPatient() {
        return numPatient;
    }

    public void setNumPatient(int numPatient) {
        this.numPatient = numPatient;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(level);
        dest.writeString(phone);
        dest.writeString(section);
        dest.writeString(university);
        dest.writeString(username);
        dest.writeFloat(rate);
        dest.writeInt(numPatient);
    }
}
