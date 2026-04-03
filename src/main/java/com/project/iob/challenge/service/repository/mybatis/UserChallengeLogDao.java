package com.project.iob.challenge.service.repository.mybatis;

import com.project.iob.challenge.entity.UserChallengeLogs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

@Mapper
public interface UserChallengeLogDao {

    // 인증 로그 추가
    void insertLog(UserChallengeLogs log);

    // n일 달성 카운트
    int countLogsByChallengeId(Long challengeId);

    // 오늘 중복 인증 확인
    int countTodayLog(@Param("challengeId") Long challengeId,
                      @Param("checkedDate") LocalDate checkedDate);

    // 최근 인증 날짜
    LocalDate selectLastCheckedDate(Long challengeId);
}