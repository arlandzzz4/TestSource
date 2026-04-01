package com.project.iob.calendar.repository.mybatis;

import com.project.iob.calendar.dto.CalendarDietDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarDAO {

    /** [음식 이름 검색] */
    List<CalendarDietDto.FoodSearchResponse> searchFood(@Param("keyword") String keyword);
}
