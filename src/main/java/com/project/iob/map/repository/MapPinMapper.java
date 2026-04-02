package com.project.iob.map.repository;

import com.project.iob.map.dto.MapPinDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MapPinMapper {

    // 전체 핀 조회 (삭제된 것 제외)
    List<MapPinDto> getAllPins();

    // 핀 등록
    int insertPin(MapPinDto mapPinDto);

    // 핀 삭제 (del_yn = Y 로 변경)
    int deletePin(Long pinId);
}