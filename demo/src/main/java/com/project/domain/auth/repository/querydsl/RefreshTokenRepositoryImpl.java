package com.project.domain.auth.repository.querydsl;

import static com.project.domain.auth.entity.QRefreshToken.refreshToken1;

import java.util.Optional;

import com.project.domain.auth.entity.RefreshToken;
import com.project.domain.auth.repository.jpa.RefreshTokenRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
	public Optional<RefreshToken> findByEmail(String email, String provider) {
    	return Optional.ofNullable(
                queryFactory
                    .selectFrom(refreshToken1)
                    .where("local".equalsIgnoreCase(provider)?refreshToken1.email.eq(email):refreshToken1.providerId.eq(email))
                    .fetchOne()
            );
	}
    
    @Override
    public Optional<RefreshToken> findByEmail(String email) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(refreshToken1)
                .where(refreshToken1.email.eq(email))
                .fetchOne()
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(refreshToken1)
                .where(refreshToken1.refreshToken.eq(token))
                .fetchOne()
        );
    }

	@Override
	public void deleteRefreshTokenById(String email, String provider) {
		queryFactory
			.delete(refreshToken1)
			.where(refreshToken1.email.eq(email).and(refreshToken1.provider.eq(provider)))
			.execute();
	}
}