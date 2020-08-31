package com.example.glow.response.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientListFromStudentData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("register_to_patient_profiles")
    @Expose
    private List<PatientListFromStudentRegisterToPatiebtProfile> registerToPatientProfiles = null;

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

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public List<PatientListFromStudentRegisterToPatiebtProfile> getRegisterToPatientProfiles() {
        return registerToPatientProfiles;
    }

    public void setRegisterToPatientProfiles(List<PatientListFromStudentRegisterToPatiebtProfile> registerToPatientProfiles) {
        this.registerToPatientProfiles = registerToPatientProfiles;
    }



}
