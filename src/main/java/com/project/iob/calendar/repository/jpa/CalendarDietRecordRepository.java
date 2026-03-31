package com.project.iob.calendar.repository.jpa;

import com.project.iob.calendar.entity.CalendarDietRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarDietRecordRepository extends JpaRepository<CalendarDietRecord, Long> {

    List<CalendarDietRecord> findByUserEmailAndRecordDate(String userEmail, LocalDate recordDate);

    void deleteByUserEmailAndRecordDate(String userEmail, LocalDate recordDate);
}
