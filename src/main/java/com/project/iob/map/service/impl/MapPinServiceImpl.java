package com.project.iob.map.service.impl;

import com.project.iob.map.dto.MapPinDto;
import com.project.iob.map.repository.MapPinMapper;
import com.project.iob.map.service.MapPinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapPinServiceImpl implements MapPinService {

    private final MapPinMapper mapPinMapper;

    @Override
    public List<MapPinDto> getAllPins() {
        return mapPinMapper.getAllPins();
    }

    @Override
    public int createPin(MapPinDto mapPinDto) {
        return mapPinMapper.insertPin(mapPinDto);
    }

    @Override
    public int deletePin(Long pinId) {
        return mapPinMapper.deletePin(pinId);
    }
}	