package com.project.iob.auth.repository.querydsl;

import static com.project.iob.auth.entity.QRefreshToken.refreshToken1;

import java.util.Optional;

import com.project.global.auth.Provider;
import com.project.iob.auth.entity.RefreshToken;
import com.project.iob.auth.repository.jpa.RefreshTokenRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
	public Optional<RefreshToken> findByEmail(String email, String providerCode) {
    	return Optional.ofNullable(
                queryFactory
                    .selectFrom(refreshToken1)
                    .where(Provider.LOCAL.equals(providerCode)?refreshToken1.email.eq(email):refreshToken1.providerId.eq(email))
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
	public void deleteRefreshTokenById(String email, String providerCode) {
		queryFactory
			.delete(refreshToken1)
			.where(refreshToken1.email.eq(email).and(refreshToken1.providerCode.eq(providerCode)))
			.execute();
	}
}