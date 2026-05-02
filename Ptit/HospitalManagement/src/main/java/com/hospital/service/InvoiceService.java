// service/InvoiceService.java
package com.hospital.service;

import com.hospital.entity.Invoice;
import com.hospital.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getRevenueByDepartment(Long departmentId, LocalDate from, LocalDate to) {
        return invoiceRepository.findByAppointmentDoctorDepartmentIdAndPaymentDateBetween(departmentId, from, to);
    }

    public List<Invoice> getRevenueByDateRange(LocalDate from, LocalDate to) {
        return invoiceRepository.findByPaymentDateBetween(from, to);
    }

    public List<Invoice> getInvoicesByUserPhone(String phone) {
        return invoiceRepository.findByAppointment_PatientPhoneOrderByPaymentDateDesc(phone);
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public BigDecimal getTotalRevenueByDateRange(LocalDate from, LocalDate to) {
        List<Invoice> invoices = invoiceRepository.findByPaymentDateBetween(from, to);
        return invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}