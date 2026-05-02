package com.hospital.model;

import java.sql.Date;

public class User {
    private int id;
    private String fullname;
    private String phone;
    private String email;
    private String password;
    private String role;
    private Date dob;
    private String gender;
    private String address;
    private String identityNo;
    private String insuranceNo;

    public User() {
    }

    public User(int id, String fullname, String phone, String email, String password, String role, Date dob, String gender, String address, String identityNo, String insuranceNo) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.identityNo = identityNo;
        this.insuranceNo = insuranceNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }
}