package com.hospital.model;

import java.sql.Timestamp;

public class MedicalRecord {
    private int id;
    private int appointmentId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private Timestamp createdAt;

    public MedicalRecord() {
    }

    public MedicalRecord(int id, int appointmentId, String diagnosis, String prescription, String notes, Timestamp createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}