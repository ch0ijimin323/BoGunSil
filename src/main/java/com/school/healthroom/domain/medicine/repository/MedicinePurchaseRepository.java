package com.school.healthroom.domain.medicine.repository;

import com.school.healthroom.domain.medicine.entity.MedicinePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MedicinePurchaseRepository extends JpaRepository<MedicinePurchase, Long> {

    @Query("SELECT m FROM MedicinePurchase m " +
           "WHERE (:startDate IS NULL OR m.purchaseDate >= :startDate) " +
           "AND (:endDate IS NULL OR m.purchaseDate <= :endDate) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(m.medicineCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(COALESCE(m.supplier, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.purchaseDate DESC, m.id DESC")
    List<MedicinePurchase> search(@Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("keyword") String keyword);

    @Modifying
    @Query("DELETE FROM MedicinePurchase m WHERE m.purchaseDate < :cutoffDate")
    int deleteByPurchaseDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
}
