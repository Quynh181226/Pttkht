package com.hospital.model;

public class RevenueStat {
    private String doctorName;
    private long revenue;
    private int patientCount;

    public RevenueStat(String doctorName, long revenue, int patientCount) {
        this.doctorName = doctorName;
        this.revenue = revenue;
        this.patientCount = patientCount;
    }

    public String getDoctorName() { return doctorName; }
    public long getRevenue() { return revenue; }
    public int getPatientCount() { return patientCount; }
}