package com.hospital.model;

public class Patient {
    private int id;
    private int userId;
    private String fullname;
    private String phone;
    private String email;
    private String dob;
    private String gender;
    private String address;
    private String identityNo;
    private String insuranceNo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIdentityNo() { return identityNo; }
    public void setIdentityNo(String identityNo) { this.identityNo = identityNo; }
    public String getInsuranceNo() { return insuranceNo; }
    public void setInsuranceNo(String insuranceNo) { this.insuranceNo = insuranceNo; }
}