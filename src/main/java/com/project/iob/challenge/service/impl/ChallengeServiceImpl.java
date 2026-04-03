package com.project.iob.challenge.service.impl;

import com.project.iob.challenge.dto.*;
import com.project.iob.challenge.entity.UserChallenges;
import com.project.iob.challenge.entity.UserChallengeLogs;
import com.project.iob.challenge.service.ChallengeService;
import com.project.iob.challenge.service.repository.mybatis.UserChallengeDao;
import com.project.iob.challenge.service.repository.mybatis.UserChallengeLogDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    private final UserChallengeDao challengeDao;
    private final UserChallengeLogDao logDao;

    // 1. 챌린지 추가
    @Override
    public void createChallenge(ChallengeRequestDto requestDto) {
        UserChallenges challenge = new UserChallenges();
        challenge.setUserEmail(requestDto.getUserEmail());
        challenge.setTitle(requestDto.getTitle());
        challenge.setDescription(requestDto.getDescription());
        challenge.setStartDate(requestDto.getStartDate());
        challenge.setEndDate(requestDto.getEndDate());
        challenge.setGoalDays(requestDto.getGoalDays());
        challengeDao.insertChallenge(challenge);
    }

    // 2. 이메일별 챌린지 목록 불러오기
    @Override
    @Transactional(readOnly = true)
    public List<ChallengeResponseDto> getChallengesByEmail(String userEmail) {
        return challengeDao.selectChallengesByEmail(userEmail)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3. 챌린지 인증
    @Override
    public ChallengeLogResponseDto verifyChallenge(Long challengeId, ChallengeLogRequestDto requestDto) {
        LocalDate checkedDate = requestDto.getCheckedDate() != null
                ? requestDto.getCheckedDate()
                : LocalDate.now();

        // 중복 인증 방지
        if (logDao.countTodayLog(challengeId, checkedDate) > 0) {
            throw new IllegalStateException("오늘은 이미 인증을 완료했습니다.");
        }

        // 로그 INSERT
        UserChallengeLogs log = new UserChallengeLogs();
        log.setChallengeId(challengeId);
        log.setCheckedDate(checkedDate);
        logDao.insertLog(log);

        // INSERT 후 count 조회
        int totalAchieveCount = logDao.countLogsByChallengeId(challengeId);

        // 응답 조립
        ChallengeLogResponseDto response = new ChallengeLogResponseDto();
        response.setLogId(log.getLogId());
        response.setChallengeId(challengeId);
        response.setCheckedDate(checkedDate);
        response.setTotalAchieveCount(totalAchieveCount);
        return response;
    }

    // 4. 챌린지 삭제
    @Override
    public void deleteChallenge(Long challengeId, String userEmail) {
        challengeDao.deleteChallenge(challengeId, userEmail);
    }

    // Entity → ResponseDto 변환
    private ChallengeResponseDto toResponseDto(UserChallenges challenge) {
        ChallengeResponseDto dto = new ChallengeResponseDto();
        dto.setChallengeId(challenge.getChallengeId());
        dto.setUserEmail(challenge.getUserEmail());
        dto.setTitle(challenge.getTitle());
        dto.setDescription(challenge.getDescription());
        dto.setStartDate(challenge.getStartDate());
        dto.setEndDate(challenge.getEndDate());
        dto.setGoalDays(challenge.getGoalDays());
        dto.setAchieveCount(logDao.countLogsByChallengeId(challenge.getChallengeId()));
        dto.setLastCheckedDate(logDao.selectLastCheckedDate(challenge.getChallengeId()));
        return dto;
    }
}