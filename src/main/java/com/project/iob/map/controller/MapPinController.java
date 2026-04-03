package com.project.iob.map.controller;

import com.project.iob.map.dto.MapPinDto;
import com.project.iob.map.service.MapPinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MapPinController {

    private final MapPinService mapPinService;

    // 핀 전체 조회
    @GetMapping("/pins")
    public ResponseEntity<List<MapPinDto>> getAllPins() {
        return ResponseEntity.ok(mapPinService.getAllPins());
    }

    // 핀 등록
    @PostMapping("/pins")
    public ResponseEntity<Integer> createPin(@RequestBody MapPinDto mapPinDto) {
        return ResponseEntity.ok(mapPinService.createPin(mapPinDto));
    }

 // 핀 삭제
    @DeleteMapping("/pins/{pinId}")
    public ResponseEntity<Void> deletePin(@PathVariable("pinId") Long pinId) {
        mapPinService.deletePin(pinId);
        return ResponseEntity.ok().build();
    }
}