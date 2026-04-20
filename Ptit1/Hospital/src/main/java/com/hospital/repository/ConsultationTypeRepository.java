package com.hospital.repository;

import com.hospital.entity.ConsultationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationTypeRepository extends JpaRepository<ConsultationType, Long> {
}