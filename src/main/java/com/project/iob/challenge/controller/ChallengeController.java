package com.project.iob.challenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.challenge.dto.ChallengeLogRequestDto;
import com.project.iob.challenge.dto.ChallengeLogResponseDto;
import com.project.iob.challenge.dto.ChallengeRequestDto;
import com.project.iob.challenge.dto.ChallengeResponseDto;
import com.project.iob.challenge.service.ChallengeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Challenge API", description = "챌린지 API")
@RestController
@RequestMapping("api/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "챌린지 등록")
    @PostMapping
    public ResponseEntity<String> createChallenge(
            @RequestBody ChallengeRequestDto requestDto) {
        challengeService.createChallenge(requestDto);
        return ResponseEntity.ok("챌린지가 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "내 챌린지 목록 조회")
    @GetMapping
    public ResponseEntity<List<ChallengeResponseDto>> getMyChallenges(
            @RequestParam("userEmail") String userEmail) {
        List<ChallengeResponseDto> list = challengeService.getChallengesByEmail(userEmail);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "챌린지 인증")
    @PostMapping("/{challengeId}/verify")
    public ResponseEntity<?> verifyChallenge(
    		@PathVariable("challengeId") Long challengeId,
            @RequestBody ChallengeLogRequestDto requestDto) {
        try {
            ChallengeLogResponseDto result = challengeService.verifyChallenge(challengeId, requestDto);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "챌린지 삭제")
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<String> deleteChallenge(
    		@PathVariable("challengeId") Long challengeId,
            @RequestParam("userEmail") String userEmail) {
        challengeService.deleteChallenge(challengeId, userEmail);
        return ResponseEntity.ok("챌린지가 삭제되었습니다.");
    }
}