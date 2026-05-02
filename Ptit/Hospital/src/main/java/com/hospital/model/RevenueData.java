package com.hospital.model;

public class RevenueData {
    private int doctorId;
    private String doctorName;
    private int totalPatients;
    private long totalRevenue;

    private long examRevenue;
    private long testRevenue;
    private long drugRevenue;

    public RevenueData() {}

    public RevenueData(int doctorId, String doctorName, int totalPatients, long totalRevenue) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.totalPatients = totalPatients;
        this.totalRevenue = totalRevenue;
    }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }
    public long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getExamRevenue() { return examRevenue; }
    public void setExamRevenue(long examRevenue) { this.examRevenue = examRevenue; }
    public long getTestRevenue() { return testRevenue; }
    public void setTestRevenue(long testRevenue) { this.testRevenue = testRevenue; }
    public long getDrugRevenue() { return drugRevenue; }
    public void setDrugRevenue(long drugRevenue) { this.drugRevenue = drugRevenue; }

    public String getFormattedRevenue() {
        return String.format("%,d", totalRevenue);
    }
}