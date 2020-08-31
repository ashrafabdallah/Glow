package com.example.glow.response.student;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentProfileResponse {

    public StudentProfileResponse(List<StudentProfileData> data) {
        this.data = data;
    }

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private List<StudentProfileData> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<StudentProfileData> getData() {
        return data;
    }

    public void setData(List<StudentProfileData> data) {
        this.data = data;
    }

}