package com.project.domain.user.querydsl;

import com.project.domain.user.entity.QUser;
import com.project.domain.user.entity.User;
import com.project.domain.user.repository.jpa.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
	public boolean existsByEmail(String email) {
		QUser user = QUser.user;

        User result = queryFactory
                .selectFrom(user)
                .where(
                	QUser.user.email.eq(email)
                )
                .fetchOne();
        
        if(result == null) {
        	log.debug("이메일 중복 체크: {}는 사용 가능한 이메일입니다.", email);
			return true;
		} 
        return false;
	}

	@Override
	public boolean existsByProviderId(String providerId) {
		QUser user = QUser.user;

		User result = queryFactory
                .selectFrom(user)
                .where(
                	QUser.user.providerId.eq(providerId)
                )
                .fetchOne();
        
        if(result == null) {
			return true;
		} 
        return false;
	}

}