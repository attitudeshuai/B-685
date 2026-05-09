package com.example.unit.repository;

import com.example.unit.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    List<AttendanceRecord> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) " +
           "ORDER BY a.attendanceDate DESC")
    List<AttendanceRecord> findByConditions(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    List<AttendanceRecord> findByAttendanceDateBetweenOrderByAttendanceDateDesc(LocalDate startDate, LocalDate endDate);
}
