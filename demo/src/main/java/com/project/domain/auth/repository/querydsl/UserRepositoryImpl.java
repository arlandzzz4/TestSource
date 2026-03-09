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
	public boolean existsByEmail(String email, String provider, String password) {
		QUsers user = QUsers.users;

        Users result = queryFactory
                .selectFrom(user)
                .where(
                    emailEq(email)
                )
                .fetchOne();

        return false;
	}
	// --- 동적 쿼리용 BooleanExpression 메서드 ---
    private BooleanExpression emailEq(String email) {
        return email != null ? QUsers.users.email.eq(email) : null;
    }

    private BooleanExpression roleEq(String provicer) {
        return provicer != null ? QUsers.users.provider.eq(provicer) : null;
    }

}