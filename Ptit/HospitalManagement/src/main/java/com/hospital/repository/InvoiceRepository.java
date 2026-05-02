package com.hospital.repository;

import com.hospital.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByAppointmentDoctorDepartmentIdAndPaymentDateBetween(Long departmentId, LocalDate from, LocalDate to);
    List<Invoice> findByPaymentDateBetween(LocalDate from, LocalDate to);
    List<Invoice> findByAppointment_PatientPhoneOrderByPaymentDateDesc(String phone);
}