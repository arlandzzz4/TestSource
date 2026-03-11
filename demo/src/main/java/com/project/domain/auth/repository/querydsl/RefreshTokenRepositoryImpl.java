package com.project.domain.auth.repository.querydsl;

import java.util.Optional;

import com.project.domain.auth.entity.RefreshToken;
import com.project.domain.auth.repository.jpa.RefreshTokenRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import static com.project.domain.auth.entity.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
	public Optional<RefreshToken> findByEmail(String email, String provider) {
    	return Optional.ofNullable(
                queryFactory
                    .selectFrom(refreshToken)
                    .where("local".equalsIgnoreCase(provider)?refreshToken.email.eq(email):refreshToken.providerId.eq(email))
                    .fetchOne()
            );
	}
    
    @Override
    public Optional<RefreshToken> findByEmail(String email) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.email.eq(email))
                .fetchOne()
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.token.eq(token))
                .fetchOne()
        );
    }

	@Override
	public void deleteRefreshTokenById(String email, String provider) {
		queryFactory
			.delete(refreshToken)
			.where(refreshToken.email.eq(email).and(refreshToken.provider.eq(provider)))
			.execute();
	}
}