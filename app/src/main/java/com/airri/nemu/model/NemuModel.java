package com.airri.nemu.model;

public class NemuModel {

    private String user_id, fname, subject, category, description, location, phone, status;

    public NemuModel () {

    }

    public NemuModel(String user_id, String fname, String subject, String category, String descriptiobn, String location, String phone, String status) {
        this.user_id = user_id;
        this.fname = fname;
        this.subject = subject;
        this.category = category;
        this.description = descriptiobn;
        this.location = location;
        this.phone = phone;
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
