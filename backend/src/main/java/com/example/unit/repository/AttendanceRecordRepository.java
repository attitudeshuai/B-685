package com.example.unit.repository;

import com.example.unit.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByUserIdAndRecordDate(Long userId, LocalDate recordDate);

    List<AttendanceRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:startDate IS NULL OR a.recordDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.recordDate <= :endDate) " +
           "ORDER BY a.recordDate DESC, a.userId")
    Page<AttendanceRecord> findByConditions(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            Pageable pageable);

    @Query("SELECT a FROM AttendanceRecord a WHERE " +
           "a.userId = :userId AND " +
           "FUNCTION('YEAR', a.recordDate) = :year AND " +
           "FUNCTION('MONTH', a.recordDate) = :month " +
           "ORDER BY a.recordDate DESC")
    List<AttendanceRecord> findByUserIdAndMonth(@Param("userId") Long userId,
                                                @Param("year") Integer year,
                                                @Param("month") Integer month);

    List<AttendanceRecord> findByRecordDateBetweenOrderByRecordDateDesc(LocalDate startDate, LocalDate endDate);
}
