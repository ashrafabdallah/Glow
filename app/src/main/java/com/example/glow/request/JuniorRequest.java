package com.example.glow.request;

public class JuniorRequest {
    private String university, phone, level, section;
    private int customer_id;
    public JuniorRequest(String university, String phone, String level, String section, int customer_id)
    {
        this.university=university;
        this.phone=phone;
        this.level=level;
        this.section=section;
        this.customer_id=customer_id;

    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
}
