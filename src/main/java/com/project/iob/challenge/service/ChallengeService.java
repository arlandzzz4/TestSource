package com.project.iob.challenge.service;

import com.project.iob.challenge.dto.*;
import java.util.List;

public interface ChallengeService {

    // 1. 챌린지 추가
    void createChallenge(ChallengeRequestDto requestDto);

    // 2. 이메일별 챌린지 목록 불러오기
    List<ChallengeResponseDto> getChallengesByEmail(String userEmail);

    // 3. 인증 버튼 클릭
    ChallengeLogResponseDto verifyChallenge(Long challengeId, ChallengeLogRequestDto requestDto);

    // 4. 챌린지 삭제
    void deleteChallenge(Long challengeId, String userEmail);
}