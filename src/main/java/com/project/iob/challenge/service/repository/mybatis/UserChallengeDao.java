package com.project.iob.challenge.service.repository.mybatis;

import com.project.iob.challenge.entity.UserChallenges;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserChallengeDao {

    // 챌린지 추가
    void insertChallenge(UserChallenges challenge);

    // 챌린지 목록 조회
    List<UserChallenges> selectChallengesByEmail(String userEmail);

    // 챌린지 단건 조회 (삭제 전 존재 확인용)
    UserChallenges selectChallengeById(@Param("challengeId") Long challengeId,
                                       @Param("userEmail") String userEmail);

    // 챌린지 삭제
    void deleteChallenge(@Param("challengeId") Long challengeId,
                         @Param("userEmail") String userEmail);
}