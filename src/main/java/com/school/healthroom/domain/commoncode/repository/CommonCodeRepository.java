package com.school.healthroom.domain.commoncode.repository;

import com.school.healthroom.domain.commoncode.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    boolean existsByGroupCodeAndCode(String groupCode, String code);

    Optional<CommonCode> findByGroupCodeAndCode(String groupCode, String code);

    @Query("SELECT c FROM CommonCode c " +
           "WHERE (:groupCode IS NULL OR :groupCode = '' OR c.groupCode = :groupCode) " +
           "AND (:useYn IS NULL OR c.useYn = :useYn) " +
           "ORDER BY c.groupCode ASC, c.sortOrder ASC, c.id ASC")
    List<CommonCode> search(@Param("groupCode") String groupCode, @Param("useYn") Boolean useYn);

    List<CommonCode> findByGroupCodeAndUseYnTrueOrderBySortOrderAscIdAsc(String groupCode);

    List<CommonCode> findByGroupCodeAndParentCodeAndUseYnTrueOrderBySortOrderAscIdAsc(String groupCode, String parentCode);
}
