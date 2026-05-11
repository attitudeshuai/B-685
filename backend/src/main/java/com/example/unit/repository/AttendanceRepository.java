package com.example.unit.repository;

import com.example.unit.entity.Attendance;
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
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    List<Attendance> findByUserIdAndAttendanceDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) " +
           "ORDER BY a.attendanceDate DESC")
    Page<Attendance> findByConditions(@Param("userId") Long userId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.checkInTime IS NOT NULL")
    Long countCheckInDays(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.checkOutTime IS NOT NULL")
    Long countCheckOutDays(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.status = 'LATE'")
    Long countLateDays(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.status = 'EARLY_LEAVE'")
    Long countEarlyLeaveDays(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(a.workHours), 0) FROM Attendance a WHERE a.userId = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Double sumWorkHours(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
