package com.example.glow.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Junior implements Parcelable {
    private String imageUrl,level,phone,section,university,username;
    private int numPatient;

    public Junior(String imageUrl, String level, String phone, String section, String university, String username, int numPatient) {
        this.imageUrl = imageUrl;
        this.level = level;
        this.phone = phone;
        this.section = section;
        this.university = university;
        this.username = username;
        this.numPatient = numPatient;
    }

    protected Junior(Parcel in) {
        imageUrl = in.readString();
        level = in.readString();
        phone = in.readString();
        section = in.readString();
        university = in.readString();
        username = in.readString();
        numPatient = in.readInt();
    }

    public static final Creator<Junior> CREATOR = new Creator<Junior>() {
        @Override
        public Junior createFromParcel(Parcel in) {
            return new Junior(in);
        }

        @Override
        public Junior[] newArray(int size) {
            return new Junior[size];
        }
    };

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

    public int getNumPatient() {
        return numPatient;
    }

    public void setNumPatient(int numPatient) {
        this.numPatient = numPatient;
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
        dest.writeInt(numPatient);
    }
}
