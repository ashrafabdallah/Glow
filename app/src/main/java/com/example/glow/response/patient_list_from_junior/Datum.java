
package com.example.glow.response.patient_list_from_junior;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("register_to_patient_profiles")
    @Expose
    private List<RegisterToPatientProfile> registerToPatientProfiles = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<RegisterToPatientProfile> getRegisterToPatientProfiles() {
        return registerToPatientProfiles;
    }

    public void setRegisterToPatientProfiles(List<RegisterToPatientProfile> registerToPatientProfiles) {
        this.registerToPatientProfiles = registerToPatientProfiles;
    }

}
