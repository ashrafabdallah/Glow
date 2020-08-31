package com.example.glow.response.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientListFromStudentRegisterToPatiebtProfile {
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("adress")
    @Expose
    private String adress;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("active-disactive")
    @Expose
    private Integer activeDisactive;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActiveDisactive() {
        return activeDisactive;
    }

    public void setActiveDisactive(Integer activeDisactive) {
        this.activeDisactive = activeDisactive;
    }

}
