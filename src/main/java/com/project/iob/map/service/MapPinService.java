package com.project.iob.map.service;

import com.project.iob.map.dto.MapPinDto;
import com.project.iob.map.repository.MapPinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapPinService {

    private final MapPinMapper mapPinMapper;

    // 전체 핀 조회
    public List<MapPinDto> getAllPins() {
        return mapPinMapper.getAllPins();
    }

    // 핀 등록
    public int createPin(MapPinDto mapPinDto) {
        return mapPinMapper.insertPin(mapPinDto);
    }

    // 핀 삭제
    public int deletePin(Long pinId) {
        return mapPinMapper.deletePin(pinId);
    }
}