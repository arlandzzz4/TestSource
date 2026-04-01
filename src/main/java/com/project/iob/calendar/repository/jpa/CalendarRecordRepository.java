package com.project.iob.calendar.repository.jpa;

import com.project.iob.calendar.entity.CalendarRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarRecordRepository extends JpaRepository<CalendarRecord, Long> {

    @Query("SELECT c FROM CalendarRecord c " +
           "WHERE c.userEmail = :email " +
           "AND YEAR(c.recordDate) = :year " +
           "AND MONTH(c.recordDate) = :month")
    List<CalendarRecord> findByUserEmailAndYearMonth(
        @Param("email") String email,
        @Param("year") int year,
        @Param("month") int month
    );

    Optional<CalendarRecord> findByUserEmailAndRecordDate(String userEmail, LocalDate recordDate);
}
