package com.project.domain.auth.repository.querydsl;

import com.project.domain.auth.entity.QUsers;
import com.project.domain.auth.entity.Users;
import com.project.domain.auth.repository.jpa.UserRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
	public boolean existsByEmail(String email) {
		QUsers user = QUsers.users;

        Users result = queryFactory
                .selectFrom(user)
                .where(
                	QUsers.users.email.eq(email)
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
		QUsers user = QUsers.users;

        Users result = queryFactory
                .selectFrom(user)
                .where(
                	QUsers.users.providerId.eq(providerId)
                )
                .fetchOne();
        
        if(result == null) {
			return true;
		} 
        return false;
	}

}