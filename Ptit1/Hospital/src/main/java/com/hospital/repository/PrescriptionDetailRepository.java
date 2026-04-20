package com.hospital.repository;

import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
    List<PrescriptionDetail> findByPrescription(Prescription prescription);
    long countByMedicineId(Long medicineId);

    @Query("SELECT DISTINCT pd.medicine.id FROM PrescriptionDetail pd")
    List<Long> findDistinctMedicineIds();
}