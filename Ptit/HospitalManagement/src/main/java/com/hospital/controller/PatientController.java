// controller/PatientController.java
package com.hospital.controller;

import com.hospital.entity.Appointment;
import com.hospital.entity.Invoice;
import com.hospital.entity.MedicalRecord;
import com.hospital.service.AppointmentService;
import com.hospital.service.InvoiceService;
import com.hospital.service.MedicalRecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final InvoiceService invoiceService;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @GetMapping("/search-appointment")
    public String searchForm() {
        return "views/search-appointment";
    }

    @PostMapping("/search-appointment")
    public String searchResult(@RequestParam String phone, Model model) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPhone(phone);
        model.addAttribute("appointments", appointments);
        model.addAttribute("searchPhone", phone);
        return "views/search-appointment";
    }

    @GetMapping("/appointment/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("app", appointment);
        return "views/appointment-detail";
    }

    @PostMapping("/appointment/cancel/{id}")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        if (appointmentService.cancelAppointment(id)) {
            ra.addFlashAttribute("message", "Hủy lịch hẹn thành công");
        } else {
            ra.addFlashAttribute("error", "Hủy lịch thất bại");
        }
        return "redirect:/search-appointment";
    }

    @GetMapping("/medical-record")
    public String medicalRecord(HttpSession session, Model model) {
        String phone = (String) session.getAttribute("userPhone");
        if (phone == null) return "redirect:/login";
        List<Appointment> appointments = appointmentService.getAppointmentsByPhone(phone);
        model.addAttribute("appointments", appointments);
        return "views/medical-record";
    }

    @GetMapping("/medical-record/view/{id}")
    public String viewMedicalRecord(@PathVariable Long id, Model model) {
        MedicalRecord record = medicalRecordService.getByAppointmentId(id).orElse(null);
        model.addAttribute("record", record);
        return "views/medical-record-view";
    }

    @GetMapping("/payments")
    public String paymentHistory(HttpSession session, Model model) {
        String phone = (String) session.getAttribute("userPhone");
        if (phone == null) return "redirect:/login";

        List<Invoice> invoices = invoiceService.getInvoicesByUserPhone(phone);
        model.addAttribute("invoices", invoices);
        return "views/payment-history";
    }

    @GetMapping("/invoice/view/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        model.addAttribute("invoice", invoice);
        return "views/invoice-detail";
    }
}