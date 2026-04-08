package com.project.iob.map.service;

import com.project.iob.map.dto.MapPinDto;
import java.util.List;

public interface MapPinService {

    // 전체 핀 조회
    List<MapPinDto> getAllPins();

    // 핀 등록
    int createPin(MapPinDto mapPinDto);

    // 핀 삭제
    int deletePin(Long pinId);
}