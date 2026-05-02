package com.hospital.model;

import java.sql.Date;

public class Invoice {
    private int id;
    private int appointmentId;
    private long totalAmount;
    private String status;
    private Date paymentDate;

    public Invoice() {
    }

    public Invoice(int id, int appointmentId, long totalAmount, String status, Date paymentDate) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
}