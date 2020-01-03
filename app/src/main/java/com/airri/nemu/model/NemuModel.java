package com.airri.nemu.model;

public class NemuModel {

    private String id;
    private String googleid;
    private String fname;
    private String subject;
    private String category;
    private String description;
    private String location;
    private String phone;
    private String status;
    private String date;
    private String photo;
    private String founderPhone;
    private String founderName;

    public NemuModel () {

    }

    public NemuModel(String googleid, String fname, String subject, String category, String description, String location, String phone, String status, String date, String photo, String founderPhone, String founderName) {
        this.googleid = googleid;
        this.fname = fname;
        this.subject = subject;
        this.category = category;
        this.description = description;
        this.location = location;
        this.phone = phone;
        this.status = status;
        this.date = date;
        this.photo = photo;
        this.founderPhone = founderPhone;
        this.founderName = founderName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public String getFounderPhone() {
        return founderPhone;
    }

    public void setFounderPhone(String founderPhone) {
        this.founderPhone = founderPhone;
    }

    public String getFounderName() {
        return founderName;
    }

    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }
}
