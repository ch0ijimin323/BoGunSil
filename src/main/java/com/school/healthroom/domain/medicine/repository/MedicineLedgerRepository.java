package com.school.healthroom.domain.medicine.repository;

import com.school.healthroom.domain.medicine.entity.MedicineLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicineLedgerRepository extends JpaRepository<MedicineLedger, Long> {

    Optional<MedicineLedger> findTopByMedicineCodeOrderByIdDesc(String medicineCode);

    @Query("SELECT l FROM MedicineLedger l " +
           "WHERE (:startDate IS NULL OR l.ledgerDate >= :startDate) " +
           "AND (:endDate IS NULL OR l.ledgerDate <= :endDate) " +
           "AND (:medicineCode IS NULL OR :medicineCode = '' OR l.medicineCode = :medicineCode) " +
           "ORDER BY l.ledgerDate DESC, l.id DESC")
    List<MedicineLedger> search(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("medicineCode") String medicineCode);

    @Modifying
    @Query("DELETE FROM MedicineLedger l WHERE l.ledgerDate < :cutoffDate")
    int deleteByLedgerDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
}
